## 4. Modelagem de Dados

> **Diagrama Entidade-Relacionamento (DER)** — ver diagrama gerado na conversa.

### Entidades e Campos Principais

| Entidade             | Campos-chave                                                                                                                                  | Observações                                                                                                                                                                    |
| -------------------- | --------------------------------------------------------------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| `Usuario`            | `id`, `nome`, `email`, `senha_hash`, `papel` (enum: `ADMIN`, `USUARIO`)                                                                       | 1 usuário = 1 conta (v1 sem colaboração). `papel` padrão é `USUARIO` no cadastro público. `ADMIN` é definido diretamente no banco (seed), não por autopromoção via formulário. |
| `RefreshToken`       | `id`, `usuario_id` (FK), `token_hash`, `expira_em`, `revogado` (boolean)                                                                      | Permite revogação explícita (troca de senha, logout) sem manter blacklist de access tokens.                                                                                    |
| `Cliente`            | `id`, `usuario_id` (FK), `nome`, `email`, `telefone`                                                                                          | Cadastro de referência do usuário dono da conta; reutilizável entre projetos.                                                                                                  |
| `Estado`             | `id`, `sigla`, `nome`                                                                                                                         | Cadastro dos estados utilizados pelo sistema.                                                                                                                                  |
| `TabelaSinapiVersao` | `id`, `estado_id` (FK), `mes_referencia`, `regime_desoneracao`, `data_publicacao`                                                             | Uma linha por combinação **estado + mês + regime**.                                                                                                                            |
| `Composicao`         | `id`, `tabela_versao_id` (FK), `codigo`, `descricao`, `unidade`                                                                               | Vinculada à versão SINAPI de origem.                                                                                                                                           |
| `Insumo`             | `id`, `tabela_versao_id` (FK), `codigo`, `descricao`, `preco_unitario`                                                                        | Relaciona-se com `Composicao` via tabela associativa (N:N).                                                                                                                    |
| `Projeto`            | `id`, `usuario_id` (FK), `cliente_id` (FK, nullable), `nome`, `estado_id` (FK), `regime_desoneracao`, `bdi_percentual`                        | Configurações globais do orçamento (regime e BDI) ficam aqui. `cliente_id` é nullable, pois o orçamento pode ser feito sem cliente definido ainda.                             |
| `Orcamento`          | `id`, `projeto_id` (FK), `tabela_versao_id` (FK), `status`                                                                                    | `tabela_versao_id` trava a versão SINAPI usada, garantindo que o orçamento não mude retroativamente.                                                                           |
| `GrupoEtapa`         | `id`, `orcamento_id` (FK), `grupo_pai_id` (FK, autorreferência), `nome`, `ordem`                                                              | Estrutura em árvore para a EAP (etapas e subetapas).                                                                                                                           |
| `ItemOrcamento`      | `id`, `grupo_etapa_id` (FK), `tipo`, `composicao_id` (FK, nullable), `item_manual_id` (FK, nullable), `quantidade`, `preco_unitario_snapshot` | `tipo` indica se o item vem da SINAPI ou é manual. `preco_unitario_snapshot` congela o preço no momento da adição.                                                             |
| `ItemManual`         | `id`, `orcamento_id` (FK), `descricao`, `unidade`, `preco_unitario`                                                                           | Preço estático — não sofre atualização automática mensal.                                                                                                                      |

---

### Relacionamentos Principais

* `Usuario` **1:N** `Projeto`
* `Usuario` **1:N** `RefreshToken` — histórico de sessões ativas/revogadas.
* `Usuario` **1:N** `Cliente` — cada usuário só vê e gerencia os próprios clientes cadastrados.
* `Cliente` **1:N** `Projeto` — um cliente pode ter vários projetos ao longo do tempo.
* `Projeto` **1:N** `Orcamento`
* `Orcamento` **1:N** `GrupoEtapa`

    * `GrupoEtapa` pode possuir subetapas por meio de uma relação autorreferenciada.
* `GrupoEtapa` **1:N** `ItemOrcamento`
* `ItemOrcamento` referencia opcionalmente `Composicao` **OU** `ItemManual`, nunca os dois simultaneamente.
* `Composicao` **N:N** `Insumo`

    * A relação utiliza uma tabela associativa contendo o **coeficiente de consumo**.
* `Estado` **1:N** `TabelaSinapiVersao`
* `TabelaSinapiVersao` **1:N** `Composicao`
* `TabelaSinapiVersao` **1:N** `Insumo`
* `TabelaSinapiVersao` **1:N** `Orcamento` — versão SINAPI travada.
* `Orcamento` **1:N** `ItemManual`

---

### Ponto de Atenção Arquitetural

O campo `tabela_versao_id` em `Orcamento` é o mecanismo que implementa a regra de negócio definida na seção **2.3 — Regras de Negócio Complexas**:

> **"Um orçamento salvo não deve mudar retroativamente."**

O campo `tabela_versao_id` deve ser definido no momento da criação do orçamento e **nunca deve ser atualizado automaticamente** quando uma nova versão da SINAPI for publicada.

Dessa forma, o sistema pode atualizar normalmente sua base de dados com novos meses e estados sem alterar os valores dos orçamentos existentes.

### Regra de Integridade do `ItemOrcamento`

Para garantir que um item não seja simultaneamente SINAPI e manual, deve ser aplicada a seguinte regra:

```text
tipo = SINAPI
→ composicao_id IS NOT NULL
→ item_manual_id IS NULL

tipo = MANUAL
→ composicao_id IS NULL
→ item_manual_id IS NOT NULL
```

Essa regra deve ser validada **na camada de aplicação e, preferencialmente, também no banco de dados**.

### Regra de Permissão do Usuário

O campo `Usuario.papel` define o nível de acesso do usuário:

| Papel     | Origem                           | Permissões                                                                                                |
| --------- | -------------------------------- | --------------------------------------------------------------------------------------------------------- |
| `USUARIO` | Cadastro público                 | Acesso aos próprios projetos, clientes e orçamentos.                                                      |
| `ADMIN`   | Seed/configuração administrativa | Acesso às funcionalidades administrativas definidas pelo sistema, incluindo gerenciamento da base SINAPI. |

O usuário público **não pode alterar o próprio `papel` por meio da API ou da interface**.

## 7 DDD: Bounded Contexts e Linguagem Ubíqua

### Regra de Nomenclatura

Os termos de **domínio** relacionados ao negócio de construção civil e à SINAPI devem ser escritos em **português e sem acentos**.

Exemplo:

```java id="8l5hjm"
Orcamento
```

e não:

```java id="y7j7by"
Orçamento
```

Essa regra segue o princípio da **Linguagem Ubíqua (Ubiquitous Language)** do DDD: o código deve utilizar os mesmos termos empregados nas conversas com arquitetos, engenheiros e demais profissionais do domínio, evitando traduções desnecessárias.

Já os termos **técnicos e estruturais** relacionados às camadas, padrões e mecanismos da aplicação devem seguir a convenção do ecossistema Java/Spring e ser escritos em **inglês**:

* `domain`
* `application`
* `infrastructure`
* `Repository`
* `UseCase`
* `Controller`
* `Mapper`
* `DTO`

#### Exemplo

A combinação dos dois padrões resulta em nomes como:

```java id="f6g4ob"
OrcamentoRepository
CriarOrcamentoUseCase
OrcamentoController
OrcamentoMapper
```

A regra geral é:

> **Domínio em português, sem acentos. Estrutura técnica em inglês.**

---

## Contexto: Identidade

### Agregado Raiz

`Usuario`

### Entidades Internas

* `RefreshToken`

### Invariantes

* Um e-mail não pode se repetir entre usuários.
* Um `RefreshToken` revogado nunca pode gerar um novo `AccessToken`.
* Todo novo usuário deve nascer com o papel `USUARIO`.
* O papel `ADMIN` nunca pode ser atribuído por meio do cadastro público.

### Linguagem Ubíqua

* `Usuario`
* `Cliente`
* `Papel`

    * `Admin`
    * `Usuario`
* `Sessao`

---

## Contexto: Catálogo (SINAPI)

### Agregado Raiz

`TabelaSinapiVersao`

### Entidades Internas

* `Composicao`
* `Insumo`

### Invariantes

* Uma `TabelaSinapiVersao` nunca pode ser deletada.
* Uma `Composicao` deve pertencer a exatamente uma versão SINAPI.
* Um `Insumo` deve pertencer a exatamente uma versão SINAPI.
* Uma versão SINAPI é definida pela combinação:

    * Estado;
    * Mês de referência;
    * Regime de desoneração.

### Linguagem Ubíqua

* `Composicao`
* `Insumo`
* `Regime de Desoneracao`
* `Estado`
* `Mes de Referencia`
* `Importacao`

---

## Contexto: Orçamentação

> **Núcleo do domínio — maior rigor de modelagem.**

### Agregado Raiz

`Orcamento`

### Entidades Internas

* `GrupoEtapa`
* `ItemOrcamento`
* `ItemManual`

### Invariantes

* `tabela_versao_id` não pode ser alterado após a criação do orçamento.
* O orçamento não pode sofrer alteração retroativa da versão SINAPI utilizada.
* O preço final deve obedecer à fórmula:

```text id="p9ex9u"
Preco Final = Custo Total × (1 + BDI / 100)
```

* Um `ItemOrcamento` deve referenciar **`Composicao` OU `ItemManual`**, nunca os dois simultaneamente.
* Itens manuais não sofrem atualização automática de preço durante as viradas de mês da SINAPI.
* Alterações futuras na base SINAPI não podem modificar os valores congelados de um orçamento existente.

### Linguagem Ubíqua

* `Orcamento`
* `Projeto`
* `Etapa`
* `EAP`
* `Item`
* `BDI`
* `Regime`
* `Custo Direto`
* `Preco Final`
* `Curva ABC`

---

## Contexto: Relatórios

### Agregado Raiz

**Nenhum.**

O contexto de Relatórios é um contexto predominantemente de **leitura e geração de arquivos**. Não possui estado próprio de negócio e consome dados disponibilizados pelo contexto de Orçamentação.

### Invariantes

* Não escreve dados pertencentes a outros contextos.
* Apenas lê e transforma dados para apresentação/exportação.
* A geração de um relatório não deve alterar o estado do `Orcamento`.

### Linguagem Ubíqua

* `Curva ABC`
* `Exportacao`
* `Relatorio`

---

## Resumo dos Bounded Contexts

| Bounded Context  | Agregado Raiz        | Responsabilidade Principal                                      |
| ---------------- | -------------------- | --------------------------------------------------------------- |
| **Identidade**   | `Usuario`            | Usuários, autenticação, sessões e papéis.                       |
| **Catálogo**     | `TabelaSinapiVersao` | Dados SINAPI, versões, composições, insumos e importação.       |
| **Orçamentação** | `Orcamento`          | Projetos, orçamentos, EAP, itens, cálculos e regras de negócio. |
| **Relatórios**   | Nenhum               | Leitura, geração de Curva ABC e exportação de PDF/Excel/CSV.    |

### Diretriz Arquitetural

Os Bounded Contexts devem possuir **modelos de domínio independentes**, mesmo quando determinados conceitos possuem nomes semelhantes.

A comunicação entre contextos deve ocorrer por meio de **contratos explícitos**, evitando o compartilhamento direto de entidades de domínio ou detalhes de persistência.

Essa separação mantém os limites definidos no **Monólito Modular** e permite que, caso necessário no futuro, um Bounded Context seja extraído para um serviço independente sem exigir um redesenho completo do domínio.

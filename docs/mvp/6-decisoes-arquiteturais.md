## 6 Decisões Arquiteturais (ADRs)

> **ADRs (Architecture Decision Records)** são registros curtos de decisões arquiteturais importantes, documentando o **contexto**, a **decisão tomada** e suas **consequências**.

---

### ADR-001 — Uso do PostgreSQL

**Status:** Aceito

#### Contexto

A estrutura de dados do BuildPrice é predominantemente relacional. As composições SINAPI possuem relacionamento com insumos, enquanto os orçamentos possuem estruturas hierárquicas, como etapas e subetapas da EAP.

#### Decisão

Utilizar **PostgreSQL** como banco de dados principal da aplicação.

#### Consequências

* Integridade referencial por meio de chaves estrangeiras e constraints.
* Suporte robusto a relacionamentos complexos.
* Bom suporte a consultas relacionais e hierárquicas.
* Ecossistema maduro e amplamente utilizado.
* Possibilidade de evolução futura para recursos de particionamento, índices especializados e otimizações de consulta.

---

### ADR-002 — Hospedagem em VPS no MVP

**Status:** Aceito

#### Contexto

O MVP será operado inicialmente por um desenvolvedor solo e possui uma expectativa de dezenas de usuários simultâneos. Uma arquitetura de infraestrutura altamente distribuída adicionaria complexidade e custos sem benefício proporcional neste estágio.

#### Decisão

Utilizar uma **VPS** como infraestrutura principal do backend e banco de dados, com Docker para empacotamento e execução dos serviços.

O frontend será hospedado na **Vercel**.

#### Consequências

* Baixo custo operacional inicial.
* Controle direto sobre a infraestrutura.
* Configuração e manutenção relativamente simples.
* Menor complexidade em comparação com uma infraestrutura cloud distribuída.
* A capacidade da VPS deverá ser monitorada conforme a utilização crescer.
* Uma migração futura para infraestrutura mais escalável poderá ser realizada caso a demanda justifique.

---

### ADR-003 — Ingestão Manual dos Dados SINAPI no MVP

**Status:** Aceito

#### Contexto

A ingestão dos dados da SINAPI é considerada o maior risco técnico do produto. Os arquivos disponibilizados pela Caixa podem exigir parsing, normalização e validações específicas.

Automatizar a obtenção dos dados por scraping adicionaria complexidade e uma dependência operacional desnecessária durante o desenvolvimento inicial.

#### Decisão

Na versão 1, a ingestão dos dados SINAPI será realizada por **upload manual de arquivos pelo administrador**, sem scraping automatizado.

Fluxo:

```text
Upload pelo Admin
       ↓
Parsing
       ↓
Validação
       ↓
Normalização
       ↓
Criação da versão
       ↓
Publicação
```

#### Consequências

* Menor complexidade no MVP.
* Maior controle sobre quais arquivos são importados.
* Possibilidade de validar os dados antes da publicação.
* Dependência operacional do administrador para realizar os uploads.
* O processo poderá ser automatizado futuramente caso a operação manual deixe de escalar.

---

### ADR-004 — Versionamento Imutável da SINAPI

**Status:** Aceito

#### Contexto

Orçamentos antigos precisam continuar utilizando exatamente os dados SINAPI que estavam disponíveis no momento em que foram criados. Uma atualização da tabela não pode alterar retroativamente um orçamento existente.

#### Decisão

A entidade `TabelaSinapiVersao` **nunca deve ser deletada fisicamente**.

Cada novo upload deve criar uma nova versão identificada pela combinação:

```text
Estado + Mês de Referência + Regime
```

A versão utilizada pelo orçamento é registrada por meio de:

```text
Orcamento.tabela_versao_id
```

Esse relacionamento é imutável após a criação do orçamento.

#### Consequências

* Orçamentos antigos permanecem reproduzíveis.
* Novas versões podem ser adicionadas sem modificar versões anteriores.
* O histórico da SINAPI é preservado.
* Aumento progressivo do volume de dados armazenados.
* Necessidade futura de estratégias de indexação, particionamento ou arquivamento lógico, caso o volume se torne relevante.

> **Regra:** arquivar uma versão antiga não significa excluí-la fisicamente. A integridade histórica dos orçamentos deve ser preservada.

---

### ADR-005 — Monólito Modular com DDD + Clean Architecture

**Status:** Aceito

#### Contexto

O projeto será inicialmente operado por um **desenvolvedor solo** e o MVP deverá atender dezenas de usuários simultâneos.

A adoção de microsserviços neste estágio traria overhead de infraestrutura e complexidade, incluindo:

* Comunicação entre serviços;
* Consistência distribuída;
* Orquestração;
* Monitoramento;
* Deploy de múltiplos serviços;
* Maior complexidade operacional.

Não existe, neste momento, benefício suficiente que justifique essa complexidade.

#### Decisão

Adotar uma arquitetura de **Monólito Modular**, utilizando conceitos de **DDD (Domain-Driven Design)** e **Clean Architecture** de forma pragmática.

A aplicação terá um único deploy, mas será organizada internamente em módulos de negócio isolados por contratos claros.

Estrutura inicial:

```text
src/
└── ...
    ├── identidade/
    ├── catalogo/
    ├── orcamentacao/
    ├── relatorios/
    └── shared/
```

Os módulos serão organizados por **domínio de negócio**, e não por camada técnica global.

#### Módulos Principais

| Módulo          | Responsabilidade                                    |
| --------------- | --------------------------------------------------- |
| `identidade/`   | Autenticação, usuários, papéis e tokens.            |
| `catalogo/`     | SINAPI, composições, insumos, versões e ingestão.   |
| `orcamentacao/` | Projetos, clientes, orçamentos, EAP e cálculos.     |
| `relatorios/`   | PDF, Excel e Curva ABC.                             |
| `shared/`       | Componentes realmente compartilhados entre módulos. |

#### Regra de Dependência entre Módulos

Um módulo **não deve acessar diretamente**:

* Repositórios de outro módulo;
* Entidades de outro módulo;
* Detalhes internos de implementação de outro módulo.

A comunicação entre módulos deve ocorrer exclusivamente por meio de uma **interface pública/serviço exposto pelo módulo proprietário**.

Exemplo:

```text
orcamentacao
      │
      │ chama contrato público
      ▼
catalogo
      │
      └── acesso interno ao repositório SINAPI
```

Isso evita acoplamento direto entre os detalhes internos dos módulos.

#### Estrutura Interna dos Módulos

Nos módulos com maior complexidade de negócio, será utilizada uma separação baseada em quatro camadas:

```text
modulo/
├── domain/
├── application/
├── infrastructure/
└── interfaces/
```

##### `domain/`

Contém:

* Entidades de negócio;
* Value Objects;
* Regras de domínio;
* Interfaces de repositório.

Não deve possuir dependências de:

* Spring;
* JPA;
* PostgreSQL;
* Bibliotecas de infraestrutura.

##### `application/`

Contém os **casos de uso** da aplicação e é responsável por orquestrar as operações do domínio.

##### `infrastructure/`

Contém implementações técnicas, como:

* JPA/Hibernate;
* Repositórios;
* Integrações externas;
* Apache POI;
* iText/JasperReports;
* Outros componentes de infraestrutura.

##### `interfaces/`

Contém as interfaces de entrada da aplicação, como:

* Controllers REST;
* DTOs;
* Mappers;
* Validações relacionadas à entrada/saída da API.

#### Aplicação Pragmática

A separação em quatro camadas **não será aplicada de forma uniforme a todos os módulos**.

Nos módulos com regras de negócio críticas, haverá maior rigor:

* `orcamentacao`

    * Cálculo de BDI;
    * EAP;
    * Versionamento/travamento da SINAPI;
    * Regras de orçamento.
* `catalogo`

    * Versionamento SINAPI;
    * Parsing;
    * Normalização;
    * Validação dos dados.

Módulos simples, como `identidade` e CRUDs simples de `Cliente`, poderão utilizar uma estrutura mais enxuta, por exemplo:

```text
domain/
infrastructure/
```

sem uma separação rígida de `application`, quando isso não agregar valor.

#### Consequências

**Benefícios:**

* Lógica crítica isolada dos detalhes técnicos.
* Testes unitários rápidos e independentes de infraestrutura.
* Menor complexidade operacional no MVP.
* Limites de domínio definidos desde o início.
* Possibilidade de extrair módulos para microsserviços no futuro.

**Trade-offs aceitos:**

* Maior quantidade de boilerplate.
* Possível duplicação entre entidade de domínio e entidade JPA.
* Necessidade de DTOs e mappers.
* Exigência de disciplina arquitetural para evitar vazamento de dependências de infraestrutura para o domínio.

#### Evolução

Avaliar o uso do **Spring Modulith** para auxiliar na imposição e verificação automática dos limites entre módulos durante o build.

---

### ADR-006 — Autenticação com JWT + Access Token + Refresh Token

**Status:** Aceito

#### Contexto

A API precisa autenticar as requisições sem depender de uma sessão armazenada integralmente no servidor.

Porém, um JWT de longa duração apresenta um problema: depois de emitido, não é possível revogá-lo facilmente antes de sua expiração sem introduzir mecanismos como blacklist.

#### Decisão

Utilizar dois tokens:

1. **Access Token**
2. **Refresh Token**

#### Access Token

O Access Token terá vida curta, por exemplo:

```text
15 minutos
```

Será enviado nas requisições por meio do header:

```http
Authorization: Bearer <access-token>
```

O token conterá as informações necessárias para autenticação e autorização, incluindo o **papel do usuário** (`USUARIO` ou `ADMIN`).

O Access Token será validado de maneira stateless, sem consulta ao banco a cada requisição.

#### Refresh Token

O Refresh Token terá vida mais longa, por exemplo:

```text
7 dias
```

Será utilizado exclusivamente para solicitar um novo Access Token por meio do endpoint:

```http
POST /auth/refresh
```

O usuário não precisará realizar login novamente enquanto o Refresh Token estiver válido.

Os Refresh Tokens serão registrados no banco contendo, no mínimo:

```text
id
usuario_id
token_hash
expira_em
revogado
```

O token armazenado deverá ser um **hash**, e não o valor original do Refresh Token.

#### Revogação

A persistência dos Refresh Tokens permitirá revogação explícita em situações como:

* Logout;
* Troca de senha;
* Logout forçado;
* Revogação administrativa;
* Outros eventos de segurança.

#### Consequências

**Benefícios:**

* Access Token stateless.
* Baixa frequência de consultas ao banco.
* Access Token possui janela de exposição curta.
* Refresh Tokens podem ser revogados individualmente.
* Não é necessário manter uma blacklist de Access Tokens.

**Trade-off aceito:**

O sistema não é 100% stateless, pois o Refresh Token precisa ser persistido e validado no banco.

Essa é uma escolha consciente para equilibrar **segurança, revogação e simplicidade arquitetural**.

---

## Decisões Futuras

As seguintes decisões ainda deverão ser formalizadas conforme o projeto evoluir:

### ADR Futuro — Separação do Módulo de Importação

Avaliar se o módulo de importação SINAPI deve permanecer dentro de `catalogo` ou ser separado em um módulo específico.

Critérios:

* Volume de processamento;
* Frequência de importação;
* Tempo de processamento;
* Complexidade do parsing;
* Necessidade de processamento assíncrono.

---

### ADR Futuro — Volume e Particionamento de Dados

Cada versão SINAPI pode conter aproximadamente **120 mil itens**, considerando composições e insumos.

Com aproximadamente:

```text
27 estados
× 12 meses
× 2 regimes
```

o crescimento anual da base pode ser significativo.

Deverão ser avaliadas futuramente as seguintes estratégias:

#### Índices

Em `TabelaSinapiVersao`:

```text
(estado_id, mes_referencia, regime_desoneracao)
```

Em `Composicao`:

```text
(tabela_versao_id, codigo)
```

#### Particionamento

Avaliar particionamento das tabelas por:

* Versão;
* Ano;
* Outro critério que se mostre adequado após análise do volume real.

#### Arquivamento Lógico

Caso o custo de armazenamento na VPS se torne relevante, avaliar o **arquivamento lógico** de versões muito antigas.

> O arquivamento não deve resultar em exclusão física de dados necessários para reproduzir orçamentos históricos.

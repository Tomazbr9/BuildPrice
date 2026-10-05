# 7. DDD: Bounded Contexts e Linguagem Ubíqua

## 7.1 Regra de Nomenclatura

O código-fonte do BuildPrice deve utilizar **inglês de forma consistente**, tanto para conceitos de domínio quanto para elementos técnicos da arquitetura.

Essa decisão mantém o projeto alinhado ao padrão já adotado nos módulos existentes, como:

```java
User
RefreshToken
SinapiTableVersion
Composition
Item
State
```

Portanto, novos módulos e conceitos também devem seguir a mesma convenção.

Exemplos:

```java
Budget
Project
Client
BudgetItem
ManualItem
```

e não:

```java
Orcamento
Projeto
Cliente
ItemOrcamento
ItemManual
```

A Linguagem Ubíqua do negócio continua sendo preservada conceitualmente nas conversas, documentação de produto e interface com o usuário.

Por exemplo:

```text
Orçamento → Budget
Projeto → Project
Cliente → Client
Composição → Composition
Insumo → Item
```

O importante é que exista um mapeamento claro e consistente entre a linguagem do negócio e a terminologia utilizada no código.

Os termos técnicos e estruturais também permanecem em inglês:

- `domain`
- `application`
- `infrastructure`
- `presentation`
- `Repository`
- `UseCase`
- `Controller`
- `Mapper`
- `DTO`
- `Gateway`
- `Adapter`

### Exemplo

```java
BudgetRepository
CreateBudgetUseCase
BudgetController
BudgetMapper
```

A regra geral passa a ser:

> **Código em inglês de forma consistente. A linguagem do negócio em português permanece documentada e mapeada explicitamente para os termos utilizados no código.**

---

# 7.2 Contexto: Identity

## Aggregate Root

`User`

## Entidades internas

- `RefreshToken`

## Invariantes

- Um e-mail não pode pertencer a mais de um usuário.
- Um `RefreshToken` revogado nunca pode gerar um novo `AccessToken`.
- Todo novo usuário criado por meio do cadastro público deve possuir o papel `USER`.
- O papel `ADMIN` nunca pode ser atribuído pelo próprio usuário através da API pública.
- Um `RefreshToken` expirado não pode ser reutilizado.
- Tokens de atualização devem ser armazenados de forma segura, utilizando hash.

## Linguagem Ubíqua

- `User`
- `Role`
  - `ADMIN`
  - `USER`
- `Session`
- `AccessToken`
- `RefreshToken`
- `Authentication`

## Responsabilidade

O contexto `identity` é responsável exclusivamente por:

- usuários;
- autenticação;
- autorização;
- sessões;
- papéis;
- emissão e validação de tokens.

Outros módulos não devem acessar diretamente:

- `UserRepository`;
- entidades JPA do módulo;
- detalhes internos de autenticação.

Quando outro contexto precisar identificar o usuário autenticado, deverá utilizar contratos apropriados e apenas as informações necessárias, como:

```text
userId
```

---

# 7.3 Contexto: Catalog

## Aggregate Root

`SinapiTableVersion`

## Entidades e conceitos internos

- `State`
- `Composition`
- `Item`
- `CompositionItem`
- `CompositionChild`

## Invariantes

- Uma `SinapiTableVersion` representa exatamente uma combinação de:
  - estado;
  - mês de referência;
  - regime de desoneração.
- Não podem existir duas versões com a mesma combinação:
  - `state`;
  - `referenceMonth`;
  - `taxReliefRegime`.
- Uma `Composition` pertence a exatamente uma `SinapiTableVersion`.
- Um `Item` pertence a exatamente uma `SinapiTableVersion`.
- Um mesmo código pode existir em versões SINAPI diferentes.
- A identificação de uma composição ou insumo deve sempre considerar sua respectiva versão.
- Versões SINAPI utilizadas por orçamentos históricos não devem ser alteradas de forma que modifiquem retroativamente seus resultados.
- Relações entre composições e insumos devem preservar seus coeficientes.
- Relações entre composição principal e composição auxiliar também devem preservar seus coeficientes.

## Linguagem Ubíqua

- `SinapiTableVersion`
- `Composition`
- `Item`
- `CompositionItem`
- `CompositionChild`
- `Coefficient`
- `State`
- `ReferenceMonth`
- `TaxReliefRegime`
- `EXEMPTED`
- `NOT_EXEMPTED`
- `Import`
- `UnitCost`
- `UnitPrice`

## Responsabilidade

O contexto `catalog` é responsável por:

- importação dos arquivos SINAPI;
- versionamento dos dados;
- estados;
- composições;
- insumos;
- estrutura analítica das composições;
- busca de composições;
- busca de insumos;
- consulta de versões históricas.

O módulo `catalog` é o proprietário dos dados SINAPI.

Nenhum outro módulo deve acessar diretamente:

```text
CompositionRepository
ItemRepository
SinapiTableVersionRepository
StateRepository
```

O acesso deve ocorrer por meio de contratos públicos.

Exemplo:

```text
budget
   |
   | public contract
   v
catalog
   |
   v
internal repositories
```

---

# 7.4 Contexto: Clients

## Aggregate Root

`Client`

## Entidades internas

Nenhuma.

`Client` é um agregado simples.

## Invariantes

- Um `Client` pertence a exatamente um `User`.
- Um usuário só pode consultar e modificar seus próprios clientes.
- Um cliente de um usuário nunca pode ser associado ao projeto de outro usuário.
- O contexto de orçamento não deve acessar diretamente `ClientRepository`.
- Outros contextos devem referenciar um cliente apenas por meio de seu identificador:

```text
clientId
```

quando uma cópia completa dos dados não for necessária.

## Linguagem Ubíqua

- `Client`
- `Name`
- `Email`
- `Phone`

## Responsabilidade

O contexto `clients` é responsável por:

- cadastrar clientes;
- consultar clientes;
- listar clientes;
- editar clientes;
- remover clientes;
- garantir isolamento dos clientes por usuário.

---

# 7.5 Contexto: Budget

> **Este é o núcleo funcional do domínio do BuildPrice e deve possuir maior rigor de modelagem.**

O contexto `budget` será responsável pela organização de projetos e pela elaboração dos orçamentos.

## Principais Aggregate Roots

### `Project`

Representa o projeto ou obra sendo orçada.

Pode possuir informações como:

```text
id
userId
clientId
name
stateId
taxReliefRegime
bdiPercentage
```

### `Budget`

Representa uma versão de orçamento vinculada a um projeto.

Pode possuir informações como:

```text
id
projectId
sinapiTableVersionId
status
```

Um `Project` pode possuir múltiplos `Budget`, permitindo revisões ou diferentes versões do orçamento.

---

## Entidades do orçamento

- `BudgetGroup`
- `BudgetItem`
- `ManualItem`

### `BudgetGroup`

Representa uma etapa da EAP.

Exemplos:

```text
Preliminary Services
Foundation
Masonry
Roofing
Finishing
```

Pode possuir estrutura hierárquica:

```text
Budget
|
+-- Foundation
|   |
|   +-- Excavation
|   +-- Concrete
|
+-- Masonry
|
+-- Finishing
```

Um grupo pode possuir:

```text
id
budgetId
parentGroupId
name
order
```

`parentGroupId` poderá ser `null` quando o grupo estiver no primeiro nível da EAP.

---

## `BudgetItem`

Representa um item efetivamente utilizado no orçamento.

Um `BudgetItem` poderá ser originado de uma composição SINAPI ou de um item manual.

Exemplo de informações:

```text
id
budgetGroupId
type
compositionId
manualItemId
quantity
unitPriceSnapshot
```

### Tipos

```text
SINAPI
MANUAL
```

---

## Invariantes

### Versionamento SINAPI

O campo:

```text
sinapiTableVersionId
```

de um `Budget` não pode ser alterado automaticamente após sua criação.

Uma nova importação SINAPI não pode modificar retroativamente os valores de um orçamento existente.

---

### Snapshot de preço

Ao adicionar uma composição SINAPI ao orçamento, o preço utilizado naquele momento deve ser congelado no `BudgetItem`.

Exemplo:

```text
Composition unitCost
        |
        v
unitPriceSnapshot
```

Se o preço da composição mudar em uma versão SINAPI futura, o orçamento antigo deve continuar utilizando o snapshot original.

---

### Integridade do `BudgetItem`

Um `BudgetItem` deve representar uma única origem.

Quando:

```text
type = SINAPI
```

deve valer:

```text
compositionId != null
manualItemId == null
```

Quando:

```text
type = MANUAL
```

deve valer:

```text
compositionId == null
manualItemId != null
```

Nunca os dois simultaneamente.

---

### Quantidade

A quantidade de um item deve ser maior que zero.

```text
quantity > 0
```

---

### Subtotal do item

O subtotal deve ser calculado a partir de:

```text
subtotal =
quantity × unitPriceSnapshot
```

O subtotal não precisa necessariamente ser armazenado no banco, pois pode ser derivado dos dados do item.

---

### Custo direto

O custo direto do orçamento corresponde à soma de todos os itens:

```text
Direct Cost =
Σ BudgetItem subtotal
```

---

### BDI

O preço final deve seguir a regra:

```text
Final Price =
Direct Cost × (1 + BDI / 100)
```

O sistema deve ser capaz de apresentar separadamente:

```text
Direct Cost
BDI Amount
Final Price
```

---

### Arredondamento

Valores monetários devem utilizar:

```java
BigDecimal
```

com:

```java
RoundingMode.HALF_EVEN
```

quando for necessário realizar arredondamento monetário.

---

### Itens manuais

Um `ManualItem` não sofre atualização automática quando novas versões SINAPI forem importadas.

Seu preço permanece estático até que o próprio usuário o altere.

---

### Propriedade do orçamento

Todo `Project` pertence a um usuário.

Consequentemente, apenas o proprietário do projeto pode:

- consultar seus orçamentos;
- adicionar itens;
- remover itens;
- alterar quantidades;
- criar etapas;
- modificar BDI;
- gerar relatórios.

---

## Linguagem Ubíqua

- `Project`
- `Budget`
- `BudgetGroup`
- `BudgetItem`
- `ManualItem`
- `Quantity`
- `UnitPrice`
- `UnitPriceSnapshot`
- `Subtotal`
- `EAP`
- `BDI`
- `DirectCost`
- `FinalPrice`
- `BudgetStatus`

---

# 7.6 Comunicação Budget → Catalog

O módulo `budget` necessita consultar informações do catálogo para adicionar uma composição ao orçamento.

Entretanto, o módulo nunca deve utilizar diretamente:

```java
CompositionRepository
```

nem:

```java
CompositionJpaRepository
```

A comunicação deverá acontecer por meio de um contrato explícito.

Conceitualmente:

```text
budget
   |
   | CompositionCatalogGateway
   v
catalog public API
   |
   v
CompositionRepository
```

Um contrato poderá fornecer apenas os dados necessários:

```java
public record CompositionCatalogData(
        UUID id,
        UUID sinapiTableVersionId,
        String code,
        String description,
        String unit,
        BigDecimal unitCost
) {
}
```

O contexto `budget` não deverá receber entidades de domínio pertencentes ao `catalog`.

---

# 7.7 Comunicação Budget → Clients

O contexto `budget` poderá utilizar `clientId` em `Project`.

Quando precisar validar a existência ou propriedade de um cliente, deverá utilizar um contrato público do módulo `clients`.

Exemplo conceitual:

```text
budget
   |
   | ClientGateway
   v
clients
```

Nunca:

```text
budget
   |
   v
ClientRepository
```

---

# 7.8 Contexto: Reports

## Aggregate Root

Nenhum.

O contexto `reports` é predominantemente responsável por leitura, transformação e geração de arquivos.

Não possui, inicialmente, estado próprio de negócio.

## Responsabilidades

- geração da Curva ABC;
- exportação PDF;
- exportação Excel;
- exportação CSV;
- montagem de documentos para apresentação do orçamento.

## Invariantes

- Não altera entidades pertencentes ao contexto `budget`.
- Não escreve dados pertencentes ao contexto `catalog`.
- A geração de um relatório nunca deve modificar o estado do orçamento.
- Relatórios devem utilizar os valores armazenados no orçamento e seus snapshots, e não consultar preços SINAPI atuais para recalcular um orçamento histórico.

## Linguagem Ubíqua

- `Report`
- `Export`
- `ABC Curve`
- `PDF`
- `Excel`
- `CSV`

---

# 7.9 Resumo dos Bounded Contexts

| Bounded Context | Principais Aggregates | Responsabilidade |
|---|---|---|
| **Identity** | `User` | Usuários, autenticação, autorização, sessões e tokens |
| **Catalog** | `SinapiTableVersion` | Dados SINAPI, versões, composições, insumos e importação |
| **Clients** | `Client` | Gestão dos clientes pertencentes a cada usuário |
| **Budget** | `Project`, `Budget` | Projetos, orçamentos, EAP, itens, BDI e cálculos |
| **Reports** | Nenhum | Curva ABC e exportação PDF/Excel/CSV |

---

# 7.10 Diretriz Arquitetural

Cada Bounded Context deve possuir seu próprio modelo e ser responsável pelos dados que pertencem ao seu domínio.

Um contexto não pode acessar diretamente:

- repositories de outro contexto;
- entidades de domínio de outro contexto;
- entidades JPA de outro contexto;
- mappers internos;
- serviços de infraestrutura internos.

A comunicação entre contextos deve ocorrer através de contratos explícitos.

Exemplo:

```text
Budget
   |
   | Contract
   v
Catalog
```

e:

```text
Budget
   |
   | Contract
   v
Clients
```

Os contratos devem expor apenas os dados necessários para executar o caso de uso.

---

# 7.11 Regra de Dependência entre Contextos

A direção esperada para o MVP é:

```text
Identity

Clients
   ^
   |
Budget ------> Catalog
   |
   v
Reports
```

Conceitualmente:

- `Identity` fornece autenticação e identidade do usuário.
- `Clients` gerencia clientes.
- `Catalog` fornece referências SINAPI.
- `Budget` consome contratos de `Catalog` e `Clients`.
- `Reports` consome dados disponibilizados por `Budget`.

Nenhum desses módulos deve depender de detalhes internos dos outros.

---

# 7.12 Objetivo da Separação

A separação em Bounded Contexts tem como objetivos:

- reduzir acoplamento;
- tornar as regras de negócio explícitas;
- impedir dependências acidentais entre módulos;
- facilitar testes;
- preservar limites arquiteturais;
- permitir evolução independente dos módulos;
- facilitar eventual extração futura de algum contexto para um serviço separado.

O BuildPrice continuará sendo um **Monólito Modular**, com um único deploy, mas com limites internos bem definidos.

A regra fundamental permanece:

> **Cada módulo é proprietário de seu domínio. Outros módulos interagem com ele exclusivamente através de contratos explícitos.**
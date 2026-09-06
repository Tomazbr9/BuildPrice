# 5. Quebra de Tarefas (Backlog)

## 5.1 Ferramenta e Organização

* **Ferramenta de gestão sugerida:** GitHub Projects, considerando que o código também estará hospedado no GitHub.
* **Organização proposta:**

    * Um **Épico** = um card/milestone.
    * Cada **História de Usuário** = uma issue.
    * Issues podem posteriormente ser subdivididas em tarefas técnicas menores quando necessário.

---

## 5.2 Épicos e Histórias de Usuário

### Épico 1 — Autenticação e Conta

* Como usuário, quero me cadastrar com e-mail e senha, para criar minha conta no sistema.
* Como usuário, quero fazer login, para acessar meus projetos.
* Como usuário, quero recuperar minha senha por e-mail, para não perder acesso à conta.
* Como usuário, quero editar meus dados de conta e trocar a senha, para manter meu cadastro atualizado.

### Épico 2 — Gestão de Clientes

* Como usuário, quero cadastrar um novo cliente (nome, e-mail, telefone), para vinculá-lo a projetos futuros.
* Como usuário, quero listar e buscar meus clientes já cadastrados, para reutilizá-los em novos projetos.
* Como usuário, quero editar ou remover um cliente, para manter meus dados corretos.

### Épico 3 — Gestão de Projetos

* Como usuário, quero criar um novo projeto informando nome, cliente, estado, regime de desoneração e BDI, para organizar meus orçamentos.
* Como usuário, quero ver a lista de todos os meus projetos, para acessar rapidamente qualquer um deles.
* Como usuário, quero abrir um projeto e ver a lista de orçamentos vinculados a ele, para gerenciar diferentes versões/revisões.
* Como usuário, quero editar as configurações de um projeto (regime e BDI), para ajustar parâmetros sem recriar tudo.

### Épico 4 — Busca de Composições SINAPI

* Como usuário, quero buscar composições por código, descrição ou categoria, para encontrar rapidamente o item que preciso.
* Como usuário, quero filtrar a busca por estado, mês e regime de desoneração, para garantir que estou usando o preço correto.
* Como usuário, quero ver o detalhe de uma composição (insumos que a compõem), para entender do que ela é feita.

### Épico 5 — Montagem do Orçamento

* Como usuário, quero criar um novo orçamento dentro de um projeto, para começar a orçar.
* Como usuário, quero organizar o orçamento em grupos/etapas (EAP), para estruturar logicamente os itens.
* Como usuário, quero adicionar composições SINAPI ao orçamento, informando a quantidade, para compor o custo.
* Como usuário, quero adicionar itens manuais (fora da SINAPI) com descrição, unidade e preço próprio, para orçar itens que a tabela não cobre.
* Como usuário, quero editar a quantidade ou remover um item do orçamento, para corrigir o que for necessário.
* Como usuário, quero ver o total do orçamento calculado automaticamente (com e sem BDI), para saber o valor final.

### Épico 6 — Relatórios e Exportação

* Como usuário, quero visualizar a Curva ABC do meu orçamento, para identificar os itens de maior peso no custo.
* Como usuário, quero exportar o orçamento em PDF, para apresentar ao cliente ou instituição financeira.
* Como usuário, quero exportar o orçamento em Excel, para poder editar/analisar os dados fora do sistema.

### Épico 7 — Importação e Atualização de Dados SINAPI (Admin)

* Como admin, quero fazer upload do arquivo Excel/PDF da SINAPI (por estado, mês e regime), para disponibilizar os preços atualizados.
* Como admin, quero acompanhar o status de uma importação (processando/concluído/erro), para saber se os dados foram publicados corretamente.
* Como admin, quero que uma nova versão nunca sobrescreva uma antiga, para preservar orçamentos já salvos.

---

## 5.3 Priorização do MVP

A ordem sugerida de implementação é:

| Ordem | Épico                                  | Justificativa                                                             |
| ----: | -------------------------------------- | ------------------------------------------------------------------------- |
|     1 | **Épico 1 — Autenticação**             | Pré-requisito para as funcionalidades autenticadas do sistema.            |
|     2 | **Épico 7 — Importação SINAPI**        | Sem os dados SINAPI disponíveis, não há base para realizar os orçamentos. |
|     3 | **Épico 4 — Busca de Composições**     | Depende dos dados SINAPI previamente importados.                          |
|     4 | **Épicos 3 + 2 — Projetos e Clientes** | Podem ser desenvolvidos em paralelo e estruturam o contexto do orçamento. |
|     5 | **Épico 5 — Montagem do Orçamento**    | É o núcleo funcional e principal entrega de valor do produto.             |
|     6 | **Épico 6 — Relatórios e Exportação**  | Fecha o ciclo de valor, permitindo apresentar e analisar o orçamento.     |

### Fluxo de Implementação

```text
Autenticação
     ↓
Importação SINAPI
     ↓
Busca de Composições
     ↓
Projetos + Clientes
     ↓
Montagem do Orçamento
     ↓
Relatórios + Exportação
```

> **Observação:** os Épicos 2 e 3 podem ser desenvolvidos em paralelo, pois possuem baixo acoplamento entre si e são pré-requisitos funcionais para a montagem completa de um orçamento.

# 2. Levantamento de Requisitos

## 2.1 Requisitos Funcionais

> **Ações que o sistema deve executar.**

* O usuário deve conseguir se cadastrar e fazer login com e-mail e senha.
* O usuário deve conseguir recuperar a senha por e-mail.
* O usuário deve conseguir criar um novo projeto de orçamento.
* Ao criar um projeto, o usuário deve conseguir:

    * Selecionar um cliente já cadastrado; ou
    * Cadastrar um novo cliente, informando:

        * Nome;
        * E-mail;
        * Telefone.
    * Vincular o cliente ao projeto.
* O usuário deve conseguir buscar composições da tabela SINAPI por:

    * Código;
    * Descrição;
    * Categoria.
* O usuário deve conseguir filtrar a busca por:

    * Estado;
    * Mês de referência.
* O usuário deve conseguir adicionar itens (composições) ao orçamento.
* O usuário deve conseguir remover ou editar a quantidade de itens já adicionados ao orçamento.
* O sistema deve calcular automaticamente o total do orçamento com base nos itens adicionados.
* O usuário deve conseguir salvar o orçamento e retomá-lo posteriormente.
* O usuário deve conseguir exportar o orçamento em **PDF**, pronto para apresentação a clientes ou instituições financeiras.
* O sistema deve manter os dados da SINAPI atualizados automaticamente, por estado e por mês.
* O usuário deve conseguir selecionar e alterar o regime de encargos sociais do projeto (**Desonerado** ou **Não Desonerado**) a qualquer momento.
* O usuário deve conseguir inserir uma taxa percentual de **BDI global** para aplicar sobre os custos do orçamento.
* O usuário deve conseguir criar grupos ou etapas, por exemplo:

    * Serviços Preliminares;
    * Fundação;
    * Alvenaria;
    * etc.

  Esses grupos devem permitir estruturar logicamente o orçamento por meio de uma **EAP (Estrutura Analítica do Projeto)**.
* O usuário deve conseguir adicionar itens manuais, provenientes de cotações próprias de mercado ou insumos fora da SINAPI, preenchendo:

    * Descrição;
    * Unidade de medida;
    * Preço unitário.
* O usuário deve conseguir gerar e visualizar a **Curva ABC** do orçamento.
* O usuário deve conseguir exportar o orçamento em formatos de planilha de cálculo:

    * Excel (`.xlsx`);
    * CSV;
    * Além da exportação em PDF.

---

## 2.2 Requisitos Não Funcionais

> **Como o sistema deve se comportar sob o capô.**

* **Escala inicial (MVP):** suportar dezenas de usuários simultâneos, sem necessidade de arquitetura de alta escala neste momento.
* **Autenticação:** e-mail + senha, com fluxo de recuperação de senha por e-mail; sessão gerenciada via **JWT (JSON Web Token)**, utilizando arquitetura stateless.
* **Disponibilidade:** sistema 100% web, acessível via navegador, sem dependência de instalação local.
* **Atualização de dados:** novos índices SINAPI devem estar disponíveis em até **24 horas após a publicação oficial da Caixa**.
* **Conformidade:** o sistema deve estar em conformidade com a **LGPD** no tratamento de dados pessoais dos usuários.
* **Tempo de resposta:** definir uma meta específica após a primeira versão estar disponível para medição. Exemplo: buscas de composição devem responder rapidamente mesmo diante de uma base de dados grande.
* Revisitar e detalhar os requisitos conforme o MVP evoluir para produção.

---

## 2.3 Regras de Negócio Complexas

> **Regras de domínio que exigem precisão — não são apenas ações do usuário, são lógicas que precisam estar documentadas sem ambiguidade antes de virar código.**

### 2.3.1 Cálculo de Composição

O cálculo de uma composição deve considerar:

**Composição = Insumos + Mão de Obra**

O cálculo deve seguir rigorosamente a memória de cálculo oficial da SINAPI.

---

### 2.3.2 Variação por Estado e Mês

Os preços de uma composição variam de acordo com:

* Estado;
* Mês de referência.

O sistema deve sempre calcular os valores com base na combinação **(estado + mês)** selecionada pelo usuário.

---

### 2.3.3 Versionamento da SINAPI

Um orçamento salvo deve manter registrado qual versão da tabela SINAPI foi utilizada no cálculo, considerando:

* Estado;
* Mês de referência;
* Regime de encargos sociais.

A atualização posterior da tabela SINAPI **não deve alterar retroativamente** os valores de orçamentos já salvos/fechados.

---

### 2.3.4 Regra de Arredondamento Monetário

Todos os valores monetários devem ser mantidos com **2 casas decimais**, seguindo o padrão monetário brasileiro.

O arredondamento de valores intermediários, como `R$ 10,125`, deve seguir a regra de **arredondamento bancário (round half to even)**, arredondando para o dígito par mais próximo.

Em Java, isso corresponde à utilização de:

```java
RoundingMode.HALF_EVEN
```

aplicado sobre `BigDecimal`.

---

### 2.3.5 Regime de Desoneração

O banco de dados deve armazenar ambas as versões mensais disponibilizadas pela Caixa:

* **Desonerado**;
* **Não Desonerado**.

O cálculo do valor unitário da composição deve apontar rigorosamente para a tabela correspondente ao regime selecionado pelo usuário.

---

### 2.3.6 Cálculo de BDI

O sistema deve diferenciar:

* **Custo Direto**;
* **Preço Final**.

A fórmula utilizada deve ser:

```text
Preço Final = Custo Total × (1 + BDI / 100)
```

Os arquivos exportados em **PDF** e **Excel** devem explicitar claramente a diferença entre:

* Total sem BDI;
* BDI aplicado;
* Total com BDI.

---

### 2.3.7 Geração da Curva ABC

Para gerar a Curva ABC, o sistema deve:

1. Calcular o custo total de cada item:

   ```text
   Custo Total do Item = Preço Unitário × Quantidade
   ```

2. Ordenar todos os itens do orçamento em **ordem decrescente de valor**.

3. Calcular o **percentual de participação individual** de cada item em relação ao custo total da obra.

4. Calcular o **percentual acumulado** de cada linha em relação ao custo total da obra.

---

### 2.3.8 Isolamento de Itens Próprios

Itens inseridos manualmente pelo usuário não devem sofrer atualização automática durante as viradas de mês da SINAPI.

Os preços devem permanecer **estáticos conforme digitados pelo usuário**, a menos que sejam editados manualmente.

---

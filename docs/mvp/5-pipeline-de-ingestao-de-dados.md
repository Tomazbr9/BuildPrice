## 5 Pipeline de Ingestão de Dados

> **Seção crítica — é o risco técnico mais alto do produto.**

### Fonte dos Dados

Os dados da SINAPI serão obtidos a partir dos arquivos **Excel/PDF disponibilizados pela Caixa Econômica Federal**.

### Estratégia de Obtenção — v1

Na primeira versão do produto, a ingestão dos dados será realizada por meio de **upload manual pelo administrador** (o próprio Bruno).

Não haverá scraping ou coleta automatizada nesta fase.

### Fluxo de Ingestão

O processo seguirá o seguinte fluxo:

```text
Admin faz upload
       ↓
Parsing do arquivo
       ↓
Validação dos dados
       ↓
Criação da nova versão
       ↓
Publicação no sistema
```

A nova versão será identificada pela combinação:

* **Mês de referência**
* **Estado**
* **Regime de encargos**

    * Desonerado
    * Não Desonerado

### Frequência de Atualização

A atualização será realizada **mensalmente**, por estado, contemplando os dois regimes:

* Desonerado;
* Não Desonerado.

### Meta de Tempo

Os novos dados devem estar disponíveis no sistema em até **24 horas após a publicação oficial pela Caixa**.

> **Observação:** essa meta depende de o administrador realizar o upload dos arquivos dentro dessa janela.

### Tratamento de Erros

O sistema deve validar o arquivo antes de substituir ou publicar uma nova versão.

O fluxo esperado é:

```text
Arquivo recebido
       ↓
Validação
   ↙       ↘
Falha      Sucesso
  ↓           ↓
Rejeitar    Publicar
  ↓           ↓
Manter      Nova versão
versão      disponível
anterior
```

Em caso de falha na validação:

* A nova versão **não deve ser publicada**.
* A versão anterior deve permanecer disponível e íntegra.
* O sistema deve informar ao administrador os erros encontrados no arquivo.

### Versionamento Histórico

O sistema deve manter o histórico das versões SINAPI já importadas.

Deve ser possível:

* Consultar tabelas de meses anteriores;
* Selecionar uma versão histórica;
* Utilizar uma versão histórica em um orçamento;
* Garantir que orçamentos antigos continuem vinculados à versão originalmente utilizada.

Essa funcionalidade é necessária para preservar a regra de **versionamento e imutabilidade dos orçamentos** definida na seção 2.3.

### Evolução Futura

Como possível evolução do produto, o processo manual poderá ser substituído ou complementado por **scraping/importação automatizada** dos dados da Caixa.

Essa evolução deverá ser registrada posteriormente como um **ADR (Architecture Decision Record)** específico, caso o processo manual deixe de atender à escala ou à frequência necessária.

> **Decisão para o MVP:** priorizar a confiabilidade e a rastreabilidade da ingestão manual antes de investir em automação.

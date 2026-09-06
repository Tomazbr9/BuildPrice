# Tabela SINAPI — Fundamentação Técnica

## Definição

O **Sistema Nacional de Pesquisa de Custos e Índices da Construção Civil (SINAPI)** é uma referência nacional para a produção de informações relacionadas aos custos e aos índices da construção civil no Brasil.

A estrutura da SINAPI reúne informações de custos de insumos e de composições de serviços utilizados na execução de obras e serviços de engenharia. Os dados são organizados segundo critérios técnicos e possuem referências geográficas e temporais, permitindo que os custos sejam analisados de acordo com o estado e o período de referência.

A SINAPI é utilizada como referência em diferentes processos de elaboração, análise e acompanhamento de orçamentos de obras, especialmente no contexto de obras e serviços de engenharia relacionados à administração pública.

É importante destacar que a SINAPI não deve ser interpretada simplesmente como uma lista de preços. Sua estrutura representa um **sistema de referências de custos**, no qual os preços dos insumos são relacionados às composições de serviços por meio de coeficientes de consumo.

---

## Estrutura da Tabela SINAPI

A estrutura da SINAPI pode ser compreendida a partir de dois elementos fundamentais:

* **Insumos:** representam os recursos utilizados na execução dos serviços.
* **Composições:** representam os serviços e sua respectiva estrutura de formação de custos.

De maneira simplificada, uma composição pode ser representada da seguinte forma:

```text
Composição
│
├── Insumo 1
│   └── Coeficiente de consumo
│
├── Insumo 2
│   └── Coeficiente de consumo
│
├── Insumo 3
│   └── Coeficiente de consumo
│
└── Insumo 4
    └── Coeficiente de consumo
```

Cada insumo possui um determinado preço de referência, enquanto a composição estabelece quanto desse insumo é necessário para produzir uma unidade do serviço.

Assim, o custo da composição resulta da combinação dos preços dos insumos com seus respectivos coeficientes.

---

## Insumos

Os **insumos** constituem os elementos básicos utilizados na formação das composições de serviços.

Dependendo da composição e da metodologia utilizada, podem representar recursos como:

* materiais;
* mão de obra;
* equipamentos;
* outros recursos necessários à execução do serviço.

Um registro de insumo possui informações que permitem sua identificação e utilização dentro das composições, como:

* código;
* descrição;
* unidade de medida;
* preço de referência;
* período de referência;
* localização geográfica;
* demais características pertinentes à classificação do insumo.

O preço de um insumo deve sempre ser interpretado considerando sua respectiva referência temporal e geográfica. Portanto, um mesmo código de insumo pode apresentar valores diferentes em diferentes estados ou meses de referência.

---

## Composições

As **composições de serviços** representam estruturas utilizadas para determinar o custo de execução de uma determinada atividade ou serviço de engenharia.

Uma composição normalmente possui:

* código;
* descrição;
* unidade de medida;
* relação de insumos;
* coeficientes de consumo dos insumos.

A relação entre uma composição e seus insumos é fundamental para compreender a formação do custo.

De forma simplificada:

```text
Composição
    ↓
Insumos utilizados
    ↓
Coeficientes de consumo
    ↓
Preços dos insumos
    ↓
Custo da composição
```

A composição, portanto, não deve ser analisada apenas pelo seu preço final. Sua estrutura interna permite identificar quais recursos participam da formação do custo e em quais quantidades.

---

## Coeficiente de Consumo

O **coeficiente de consumo** representa a quantidade de determinado insumo necessária para produzir uma unidade da composição.

Considerando uma composição expressa em metros quadrados, por exemplo, o coeficiente de um determinado material pode indicar quantos quilogramas, litros, unidades ou outras unidades de medida desse material são necessários para executar um metro quadrado do serviço.

A relação básica pode ser expressa como:

```text
Custo do Insumo = Coeficiente de Consumo × Preço Unitário
```

Por exemplo, considerando hipoteticamente:

```text
Coeficiente de consumo: 2,50 kg
Preço unitário: R$ 10,00/kg
```

O custo correspondente será:

```text
2,50 × R$ 10,00 = R$ 25,00
```

A soma dos custos de todos os insumos integrantes da composição resulta no custo correspondente à unidade do serviço.

---

## Formação do Custo de uma Composição

Considerando uma composição hipotética constituída por quatro insumos, a formação do custo pode ser representada da seguinte maneira:

| Insumo    | Unidade | Coeficiente | Preço Unitário |        Custo |
| --------- | ------: | ----------: | -------------: | -----------: |
| Insumo A  |      kg |        2,50 |       R$ 10,00 |     R$ 25,00 |
| Insumo B  |      un |        1,20 |        R$ 8,00 |      R$ 9,60 |
| Insumo C  |       h |        0,80 |       R$ 25,00 |     R$ 20,00 |
| Insumo D  |       h |        0,10 |       R$ 50,00 |      R$ 5,00 |
| **Total** |         |             |                | **R$ 59,60** |

Matematicamente:

```text
Custo da Composição =
Σ (Coeficiente do Insumo × Preço Unitário do Insumo)
```

No exemplo:

```text
(2,50 × 10,00)
+ (1,20 × 8,00)
+ (0,80 × 25,00)
+ (0,10 × 50,00)

= R$ 59,60
```

Os valores apresentados são exclusivamente ilustrativos e não correspondem a uma composição oficial da SINAPI.

---

## Referência Geográfica

Os dados da SINAPI possuem uma dimensão geográfica relevante.

Os preços podem variar conforme a **Unidade da Federação (UF)** considerada, refletindo diferenças regionais nos custos de materiais, mão de obra e demais recursos.

Consequentemente, uma consulta à SINAPI deve considerar o estado correspondente à referência desejada.

A identificação de um preço pode ser conceitualmente representada como:

```text
Estado
   +
Código do Insumo
   +
Mês de Referência
   +
Regime
   ↓
Preço de Referência
```

Portanto, não é tecnicamente adequado considerar que determinado código possui um único preço nacional e permanente.

---

## Referência Temporal

Os valores da SINAPI são associados a **meses de referência**.

A dimensão temporal é necessária porque os custos dos insumos e serviços sofrem alterações ao longo do tempo em função de fatores econômicos, regionais e de mercado.

Dessa forma, uma referência SINAPI deve ser identificada também pelo seu período:

```text
Código
+
Estado
+
Mês/Ano
=
Referência de Custo
```

Uma mesma composição pode apresentar custos diferentes quando consultada em meses distintos.

Por esse motivo, comparações entre valores SINAPI devem sempre considerar se os períodos de referência são equivalentes.

---

## Regimes de Desoneração

A SINAPI disponibiliza referências considerando diferentes condições relacionadas ao regime de desoneração.

O regime deve ser considerado como uma dimensão da referência de custos, pois pode produzir valores distintos para determinadas composições.

Assim, uma referência completa pode ser representada conceitualmente por:

```text
Estado
+
Mês de Referência
+
Regime de Desoneração
+
Código
=
Referência SINAPI
```

A utilização de um regime diferente daquele considerado na referência original pode produzir resultados incompatíveis com o orçamento ou análise que está sendo realizada.

Por esse motivo, o regime utilizado deve ser explicitamente identificado.

---

## Composições e Insumos

A relação entre composições e insumos pode ser entendida como uma relação de **muitos para muitos**.

Uma composição pode utilizar diversos insumos:

```text
Composição A
├── Insumo 1
├── Insumo 2
├── Insumo 3
└── Insumo 4
```

Da mesma forma, um determinado insumo pode participar de diversas composições:

```text
             ┌── Composição A
             │
Insumo X ────┼── Composição B
             │
             ├── Composição C
             │
             └── Composição D
```

A associação entre os dois elementos deve possuir o respectivo **coeficiente de consumo**, pois o mesmo insumo pode ser utilizado em quantidades diferentes em cada composição.

Conceitualmente:

```text
Composição
     │
     │
     ├── Insumo + Coeficiente
     ├── Insumo + Coeficiente
     ├── Insumo + Coeficiente
     └── Insumo + Coeficiente
```

Essa relação é essencial para a correta interpretação da formação de custos.

---

## Unidade de Medida

A unidade de medida é um elemento fundamental tanto dos insumos quanto das composições.

Exemplos de unidades que podem aparecer em referências de construção civil incluem:

* `m` — metro;
* `m²` — metro quadrado;
* `m³` — metro cúbico;
* `kg` — quilograma;
* `t` — tonelada;
* `l` — litro;
* `un` — unidade;
* `h` — hora.

A unidade da composição determina a referência para a qual seus coeficientes de consumo foram estabelecidos.

Por exemplo, uma composição com unidade `m²` representa o custo correspondente a uma unidade de área, enquanto uma composição com unidade `m³` representa o custo correspondente a uma unidade de volume.

A compatibilidade entre quantidade, unidade e coeficiente deve ser preservada durante os cálculos.

---

## Custo Unitário e Quantidade Executada

É importante diferenciar o **custo unitário da composição** da **quantidade do serviço a ser executada**.

O custo unitário representa o custo de uma unidade da composição.

A quantidade representa quanto daquele serviço será executado.

A relação pode ser expressa como:

```text
Custo do Serviço =
Custo Unitário × Quantidade
```

Por exemplo:

```text
Custo Unitário = R$ 150,00/m²
Quantidade = 100 m²
```

Então:

```text
R$ 150,00 × 100
= R$ 15.000,00
```

Esse cálculo permite utilizar as referências unitárias da SINAPI na composição de quantitativos de uma obra.

---

## Atualização dos Dados

Os dados da SINAPI são disponibilizados periodicamente, acompanhando a evolução dos custos de construção civil.

Uma nova referência temporal não deve ser interpretada como uma simples alteração do valor anterior. Ela constitui uma nova referência de dados correspondente ao respectivo período.

Conceitualmente:

```text
Referência anterior
       ↓
Novo período
       ↓
Nova referência SINAPI
```

Dessa forma, análises históricas podem utilizar o valor correspondente ao período específico que se deseja estudar.

---

## Versionamento e Histórico

Para fins de controle histórico, cada conjunto de dados deve ser associado às suas características de referência.

Uma referência pode ser identificada por:

```text
Estado
+
Mês de Referência
+
Regime de Desoneração
```

A partir dessa identificação, são vinculadas as respectivas composições e informações de insumos.

Conceitualmente:

```text
Referência SINAPI
│
├── Estado
├── Mês/Ano
├── Regime
│
├── Composições
│   ├── Código
│   ├── Descrição
│   ├── Unidade
│   └── Insumos
│
└── Insumos
    ├── Código
    ├── Descrição
    ├── Unidade
    └── Preço
```

A preservação das referências históricas é importante para permitir a reprodução de análises e orçamentos realizados com base em períodos anteriores.

---

## Interpretação dos Valores

Os valores apresentados na SINAPI devem ser interpretados como **referências de custos**, e não necessariamente como o preço final de contratação ou execução de uma obra.

O custo de uma obra pode envolver outros componentes, tais como:

* quantitativos efetivamente executados;
* condições específicas de execução;
* custos indiretos;
* administração;
* tributos;
* riscos;
* margem;
* características específicas do empreendimento;
* condições comerciais e contratuais.

Portanto, a utilização da SINAPI exige a correta compreensão de sua finalidade e de sua metodologia.

A referência de custo de uma composição representa uma base técnica para a elaboração ou análise de custos, não constituindo isoladamente o valor final de uma contratação.

---

## Relação entre Insumo, Composição e Serviço

A estrutura fundamental da SINAPI pode ser resumida em três níveis:

```text
INSUMO
   │
   │ preço unitário
   ↓
COMPOSIÇÃO
   │
   │ coeficiente de consumo
   ↓
CUSTO UNITÁRIO DO SERVIÇO
   │
   │ quantidade executada
   ↓
CUSTO DO SERVIÇO
```

Essa relação demonstra que o preço final de um serviço não surge de um único valor isolado.

Ele é resultado da combinação entre:

1. os insumos utilizados;
2. os respectivos preços;
3. os coeficientes de consumo;
4. a unidade da composição;
5. a quantidade do serviço considerada.

---

## 6.17 Considerações Técnicas

A correta utilização da SINAPI exige atenção a alguns princípios fundamentais:

* identificar corretamente o código da composição ou do insumo;
* considerar a unidade de medida correspondente;
* utilizar o estado correto;
* utilizar o mês de referência correto;
* verificar o regime de desoneração aplicável;
* preservar os coeficientes de consumo das composições;
* diferenciar preço unitário de quantidade executada;
* considerar a estrutura completa da composição;
* preservar a referência temporal utilizada em análises históricas;
* não interpretar automaticamente o custo SINAPI como preço final de contratação.

A SINAPI deve, portanto, ser compreendida como uma **estrutura técnica de referências de custos**, cuja correta interpretação depende da combinação entre dados de insumos, composições, coeficientes, unidades, localização geográfica e período de referência.

---

## Resumo da Estrutura

De forma consolidada, a estrutura conceitual da SINAPI pode ser representada da seguinte maneira:

```text
                 SINAPI
                   │
        ┌──────────┴──────────┐
        │                     │
    Referência             Referência
    Geográfica              Temporal
        │                     │
        └──────────┬──────────┘
                   │
            Regime de
            Desoneração
                   │
                   ↓
          Referência SINAPI
                   │
        ┌──────────┴──────────┐
        │                     │
    Composições             Insumos
        │                     │
        │                     │
        └─────── Relação ─────┘
                  │
          Coeficiente de
             Consumo
                  │
                  ↓
          Custo da Composição
                  │
                  ↓
         Quantidade Executada
                  │
                  ↓
             Custo Total
```

A compreensão dessa estrutura é fundamental para qualquer processo técnico que utilize a SINAPI como referência de custos, uma vez que seus valores devem ser analisados dentro do contexto completo da referência geográfica, temporal, metodológica e da composição à qual estão associados.

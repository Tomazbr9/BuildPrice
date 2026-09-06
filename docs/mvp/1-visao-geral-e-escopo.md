# 1. Visão Geral e Escopo (PRD)

## 1.1 O Problema

> **Qual dor o software resolve?**

A orçamentação de obras no Brasil baseada na tabela SINAPI é um processo fragmentado e ineficiente, por três motivos principais:

1. **Obstáculo de Dados Brutos** — a Caixa Econômica Federal distribui os dados em arquivos Excel e PDF densos, não estruturados para consumo ágil. O profissional gasta tempo excessivo em "limpeza" de dados e filtragens manuais.

2. **Fragilidade do Status Quo** — a maioria dos profissionais autônomos utiliza planilhas locais, o que gera risco de erros em fórmulas, perda de arquivos e dificuldade em manter os preços atualizados mensalmente por estado.

3. **Barreira Financeira e Técnica** — as soluções existentes são, em geral, ERPs complexos ou ferramentas legadas, com custos de assinatura proibitivos para o pequeno profissional e interfaces que exigem longo treinamento.

O **BuildPrice** resolve isso centralizando a inteligência de cálculo e a atualização de dados da SINAPI em uma interface web simples, permitindo montar um orçamento técnico por meio de buscas rápidas e adição de itens em poucos cliques.

---

## 1.2 Público-alvo

> **Quem vai utilizar o sistema?**

* **Engenheiros Civis Autônomos** — focados em obras residenciais e comerciais de pequeno e médio porte.
* **Arquitetos** — que precisam validar a viabilidade financeira de projetos ainda na fase de concepção.
* **Pequenas Empreiteiras** — que precisam de uma ferramenta ágil de orçamentação, sem a necessidade de um ERP completo.

---

## 1.3 Anti-escopo

> **O que o projeto NÃO vai fazer — essencial para evitar feature creep.**

* Não é um ERP completo.
* Não faz gestão financeira da obra.
* Não é um Diário de Obra.
* Não substitui eSocial / folha de pagamento.
* Não emite documentos com validade fiscal ou contratual (ex.: ART, contratos). O sistema apenas gera PDFs de orçamento para apresentação.
* Não é multiusuário/colaborativo na v1 — o modelo é individual (**1 usuário = 1 conta**). O acompanhamento por terceiros (cliente final, equipe) fica fora do escopo inicial.

---

## 1.4 Métricas de Sucesso / KPIs

> **Como saberemos que o produto está funcionando?**

* Redução estimada de **70% no tempo de elaboração** de um orçamento inicial.
* Usuário consegue criar seu primeiro projeto em **menos de 2 minutos após o login**.
* Sistema **100% web**, sem dependência de instalações locais ou versões específicas de Excel.
* Novos índices da **SINAPI disponíveis no sistema em até 24 horas** após a publicação oficial pela Caixa.


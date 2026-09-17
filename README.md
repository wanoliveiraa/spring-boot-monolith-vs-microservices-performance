# Comparativo de Desempenho: Monólito vs Microsserviços

Este repositório contém a implementação e os experimentos realizados no Trabalho de Conclusão de Curso **"Comparativo de Desempenho de Aplicação Spring Boot em Arquitetura Monolítica e de Microsserviços com Docker"**.

O projeto tem como objetivo comparar o desempenho de uma aplicação desenvolvida com **Spring Boot** utilizando duas abordagens arquiteturais:

* **Arquitetura Monolítica**
* **Arquitetura de Microsserviços**

Os experimentos foram executados utilizando **Docker**, com testes de carga realizados por meio do **k6** e monitoramento dos resultados através do **InfluxDB** e **Grafana**.

## 🏗️ Arquiteturas

### Monólito

Na arquitetura monolítica, os componentes da aplicação são executados como uma única aplicação Spring Boot.

```text
                ┌─────────────────┐
                │     Cliente     │
                └────────┬────────┘
                         │
                         ▼
                ┌─────────────────┐
                │ Spring Boot     │
                │   Monólito      │
                └────────┬────────┘
                         │
                         ▼
                    ┌─────────┐
                    │ Banco   │
                    │   de    │
                    │ Dados   │
                    └─────────┘
```

### Microsserviços

Na arquitetura de microsserviços, as funcionalidades são distribuídas entre diferentes serviços independentes.

```text
                ┌─────────────────┐
                │     Cliente     │
                └────────┬────────┘
                         │
              ┌──────────┴──────────┐
              ▼                     ▼
       ┌─────────────┐       ┌─────────────┐
       │ Microsserviço│       │ Microsserviço│
       │      A       │       │      B       │
       └──────┬──────┘       └──────┬──────┘
              │                     │
              └──────────┬──────────┘
                         ▼
                    ┌─────────┐
                    │ Banco   │
                    │   de    │
                    │ Dados   │
                    └─────────┘
```

## 🛠️ Tecnologias

* Java
* Spring Boot
* Docker
* Docker Compose
* k6
* InfluxDB
* Grafana
* WSL2 / Ubuntu

## 📊 Testes de Desempenho

Os testes de carga foram realizados utilizando o **k6**, permitindo analisar métricas de desempenho das duas arquiteturas.

Entre as métricas observadas estão:

* Throughput
* Tempo de resposta
* Latência
* Número de requisições
* Taxa de erros
* Comportamento sob carga

Os resultados dos testes são enviados para o **InfluxDB** e posteriormente visualizados através de dashboards no **Grafana**.

## 🐳 Execução com Docker

O ambiente de testes pode ser executado utilizando Docker Compose.

```bash
docker compose up -d
```

Após a inicialização dos containers, podem ser executados os testes de carga utilizando os scripts disponíveis no projeto.

Exemplo:

```bash
docker compose run --rm pico_test
```

## 📈 Monitoramento

O projeto utiliza:

**InfluxDB**
Responsável pelo armazenamento das métricas coletadas durante os testes.

**Grafana**
Utilizado para visualização e análise dos resultados dos experimentos.

**k6**
Responsável pela execução dos testes de carga e geração das métricas de desempenho.

## 🧪 Cenários de Teste

Foram utilizados diferentes cenários para avaliar o comportamento das aplicações sob carga, incluindo testes relacionados a:

* Leitura de dados
* Escrita de dados
* Processamento em lote
* Picos de requisições

Os mesmos cenários foram aplicados às arquiteturas monolítica e de microsserviços para possibilitar a comparação dos resultados.

## 📁 Estrutura do Projeto

```text
.
├── monolito/
│   └── ...
├── microsservicos/
│   └── ...
├── docker-compose.yml
├── k6/
│   ├── popula_banco/
│   ├── pico_test/
│   └── escrita_batch/
├── grafana/
│   └── ...
└── README.md
```

> A estrutura acima representa a organização conceitual do projeto. Os diretórios podem variar conforme a versão disponibilizada neste repositório.

## 🎓 Trabalho de Conclusão de Curso

Este projeto foi desenvolvido como parte do Trabalho de Conclusão de Curso.

**Título:**
Comparativo de Desempenho de Aplicação Spring Boot em Arquitetura Monolítica e de Microsserviços com Docker

**Tecnologias principais:** Java, Spring Boot, Docker, k6, InfluxDB e Grafana.

## 👨‍💻 Autor

**Walderney Oliveira Azevedo**

---

Este projeto tem finalidade acadêmica e foi desenvolvido para analisar experimentalmente as diferenças de desempenho entre as arquiteturas monolítica e de microsserviços em um ambiente baseado em Spring Boot e Docker.

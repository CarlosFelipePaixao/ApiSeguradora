# API de Seguradora

Stack: Java 21+/24, Spring Boot (Web, Validation, Data JPA), PostgreSQL, Maven, Docker.  
Objetivo: CRUD de clientes e veículos, consulta de áreas/processos e exposição segura via **ngrok** para integrações (front-end, webhooks, BLiP, etc.).

## Sumário

- [Visão geral](#visão-geral)
- [Arquitetura](#arquitetura)
- [Tecnologias](#tecnologias)
- [Pré-requisitos](#pré-requisitos)
- [Clonando o projeto](#clonando-o-projeto)
- [Configuração](#configuração)
  - [.env (variáveis de ambiente)](#env-variáveis-de-ambiente)
  - [application.properties](#applicationproperties)
  - [Timezone](#timezone)
  - [CORS](#cors)
- [Banco de dados](#banco-de-dados)
- [Executando localmente](#executando-localmente)
  - [Maven](#maven)
  - [Docker](#docker)
- [Expondo via ngrok](#expondo-via-ngrok)
  - [Passo a passo](#passo-a-passo)
  - [Observações importantes](#observações-importantes)
- [Endpoints](#endpoints)
  - [Clientes](#clientes)
  - [Carros](#carros)
  - [Áreas](#áreas)
  - [Health e documentação](#health-e-documentação)
- [Exemplos de requisições (cURL)](#exemplos-de-requisições-curl)
  - [Notas para Windows PowerShell](#notas-para-windows-powershell)
- [Códigos de status e validações](#códigos-de-status-e-validações)
- [Boas práticas de segurança com ngrok](#boas-práticas-de-segurança-com-ngrok)
- [Troubleshooting](#troubleshooting)
- [Testes](#testes)
- [Contribuição](#contribuição)
- [Licença](#licença)

## Visão geral

Esta API gerencia clientes, veículos e áreas de uma seguradora. Foi projetada para rodar localmente e ser exposta de forma segura para integrações externas usando **ngrok**. Inclui validações de domínio (CPF, e-mail, unicidade) e integração com PostgreSQL.

## Arquitetura

- Camada Web: Controllers REST (ex.: `/api/clientes`, `/api/area`, `/cpf/{cpf}/carros`)
- Camada de Serviço: regras de negócio e validações
- Persistência: Spring Data JPA com PostgreSQL
- Validação: Bean Validation (Hibernate Validator), incluindo `@CPF`
- Observabilidade: logs; Actuator (se habilitado)

## Tecnologias

- Java 21+ ou 24
- Spring Boot (Web, Validation, Data JPA)
- PostgreSQL 14+
- Maven 3.9+
- Docker e Docker Compose (opcional)
- ngrok

## Pré-requisitos

- JDK instalado e no PATH (`java -version`)
- Banco PostgreSQL em execução e acessível
- Conta do ngrok autenticada (`ngrok config add-authtoken <TOKEN>`)
- Opcional: Docker Desktop

## Clonando o projeto

```bash
git clone https://seu-git.com/orga/repositorio-seguradora.git
cd repositorio-seguradora

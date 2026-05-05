# AntiBankFraud - Proteção contra Golpe do Empréstimo

Sistema desenvolvido para prevenir o **golpe do empréstimo**, uma das fraudes bancárias mais comuns no Brasil. O projeto implementa um mecanismo de segurança chamado **"Modo Rua"**, que permite o acesso à conta bancária apenas de regiões previamente definidas pelo usuário.

## 🎯 Objetivo do Projeto

Desenvolver uma aplicação web com **frontend** e **backend** que demonstre uma solução prática contra invasões de contas bancárias, mesmo quando o golpista possui login e senha da vítima.

## ✨ Funcionalidades Principais

- **Modo Rua (Geolocalização)**: Acesso à conta permitido apenas em regiões seguras cadastradas pelo usuário (casa, trabalho, faculdade, etc.).
- **Cálculo de distância** usando fórmula de Haversine.
- **Ativação/Desativação** do Modo Rua pelo próprio usuário.
- **Acesso de Emergência** via código OTP enviado por e-mail secundário.
- **Alertas de segurança**: Registro de tentativas de acesso bloqueadas.
- **Dicas de prevenção** ao golpe do empréstimo na interface.
- **Autenticação segura** com JWT.

## 🛠️ Tecnologias Utilizadas

### Backend
- **Java 21** com **Spring Boot 3**
- Spring Security + **JWT** (JSON Web Token)
- Spring Data JPA
- PostgreSQL (ou MySQL)
- JavaMailSender (envio de OTP)
- Maven

### Frontend
- HTML5, CSS3 e JavaScript

### Outras ferramentas
- Git + GitHub
- Postman (testes de API)

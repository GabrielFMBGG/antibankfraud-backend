# Crédito Seguro - Backend

API REST para o sistema de proteção contra fraudes bancárias com verificação de localização (**Modo Rua**).

## 🎯 Sobre o Projeto

Backend do projeto educacional "Crédito Seguro", responsável pela lógica de autenticação, segurança por geolocalização e registro de alertas.

## ✨ Principais Funcionalidades

- Cadastro e autenticação de usuários com JWT
- Sistema **Modo Rua** com verificação de raio (algoritmo de Haversine)
- Gerenciamento de Zonas Seguras (CRUD)
- Acesso de emergência via OTP (código de 6 dígitos)
- Registro automático de alertas de segurança
- Validações robustas com Bean Validation

## 🛠️ Tecnologias

- Java 21
- Spring Boot 3.5
- Spring Security + JWT
- Spring Data JPA + Hibernate
- PostgreSQL
- JavaMailSender (envio de OTP)
- Docker (para deploy)

## 📋 Endpoints Principais

| Método     | Endpoint                            | Descrição |
|------------|-------------------------------------|---------|
| `POST`     | `/auth/registro`                    | Criar conta |
| `POST`     | `/auth/login`                       | Login + verificação de localização |
| `GET`      | `/auth/modo-rua?email=...`          | Verificar status do Modo Rua |
| `POST`     | `/auth/emergencia`                  | Login via OTP |
| `POST`     | `/zonas-seguras/usuario/{id}`       | Criar zona segura |
| `GET`      | `/zonas-seguras/usuario/{id}`       | Listar zonas |
| `PATCH`    | `/usuarios/{id}/modo-rua`           | Ativar/Desativar Modo Rua |
| `GET`      | `/alertas/usuario/{id}`             | Listar alertas |

## 📦 Deploy

Deploy realizado no render com Docker

## 👥 Autores

- Gabriel Batista
- Henrique Cabral
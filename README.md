# Jhon Barbearia - Sistema de Agendamento 💈

Um sistema completo de agendamento online e gestão para barbearias, construído com **Java, Spring Boot, MySQL e Thymeleaf**.

## 🚀 Funcionalidades

- **Área do Cliente:** Cadastro, login, agendamento de serviços, escolha de barbeiros/horários e acompanhamento do histórico de agendamentos.
- **Área do Barbeiro:** Painel para visualizar os agendamentos do dia e marcar como "Concluído".
- **Área do Administrador:** Dashboard completo com métricas de faturamento, gráficos de serviços mais solicitados, gestão de equipe (barbeiros), gestão de serviços e controle de usuários.
- **Segurança:** Autenticação via Spring Security com senhas criptografadas (BCrypt), sistema Anti-Spam (Rate Limiter por IP) e proteção contra CSRF.
- **Adequação Legal (LGPD):** Termos de Uso, Política de Privacidade e consentimento expresso do usuário.

## 🛠️ Tecnologias Utilizadas

- **Backend:** Java 17, Spring Boot 3.x, Spring Data JPA, Spring Security
- **Frontend:** HTML5, CSS3, Bootstrap 5, Javascript, Thymeleaf
- **Banco de Dados:** MySQL
- **Outros:** Maven, Chart.js

## ⚙️ Como executar localmente

1. Clone este repositório:
   ```bash
   git clone https://github.com/pauloceuma26/Project_Barbearia.git
   ```
2. Configure o banco de dados MySQL no arquivo `src/main/resources/application.properties` ou exporte as seguintes variáveis de ambiente:
   - `DB_URL` (Ex: `jdbc:mysql://localhost:3306/barbearia`)
   - `DB_USERNAME` (Ex: `root`)
   - `DB_PASSWORD` (Sua senha)
3. Execute o projeto usando o Maven Wrapper:
   ```bash
   ./mvnw spring-boot:run
   ```
4. Acesse no navegador: `http://localhost:8080`

## ☁️ Deploy (Produção)

Este projeto está configurado para suportar deploy fácil em plataformas de nuvem (Render, Railway, Heroku). As credenciais do banco de dados e do servidor SMTP (para envio de e-mails de recuperação de senha) devem ser configuradas como variáveis de ambiente na plataforma escolhida:
- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`
- `MAIL_USERNAME`
- `MAIL_PASSWORD`

---
*Desenvolvido por Paulo Rogério Silva.*

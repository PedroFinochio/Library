# Sistema de Gestão de Biblioteca  
Trabalho Prático – Banco de Dados e Aplicação Conectada

## 1. Sobre o Projeto
O Sistema de Gestão de Biblioteca é uma aplicação web desenvolvida com Spring Boot para automatizar o controle de acervo, reservas, empréstimos e relatórios gerenciais.

### Funcionalidades
- Autenticação com três níveis de acesso
- CRUD de livros
- Sistema de reservas e empréstimos
- Lista de favoritos
- Mais de 15 consultas SQL complexas
- Geração de relatórios gerenciais

## 2. Tecnologias Utilizadas
| Camada | Tecnologias |
|--------|-------------|
| Backend | Java 17, Spring Boot |
| Persistência | Spring Data JPA, Hibernate |
| Banco de Dados | MySQL |
| Segurança | Spring Security |
| Frontend | Thymeleaf, HTML, CSS, JavaScript |
| Build | Maven |

## 3. Normalização
O modelo de dados foi normalizado até a **Terceira Forma Normal (3FN)** garantindo consistência e minimização de redundâncias. Uma **desnormalização controlada** foi aplicada no atributo `disponivel` da tabela `LIVROS` exclusivamente para ganho de performance, sem comprometer a integridade.

## 4. Como Executar
### Pré-requisitos
- Java 17+
- MySQL 8.0+
- Maven

### Configurar banco
```sql
CREATE DATABASE Biblioteca;
```

### Executar
```bash
mvn spring-boot:run
```

Acesse: `http://localhost:8080`

## 5. Usuários de Teste
| Perfil | Usuário | Senha |
|--------|---------|-------|
| Administrador | admin | senha123 |
| Bibliotecário | maria.biblio | senha123 |
| Usuário | ana.silva | senha123 |

## 6. Autores
- Pedro Augusto  
- Thiago Martins  
- Jeann Victor  
- Thallysson Luis  
- Nicolas Rodrigues

---

Projeto acadêmico – Banco de Dados e Aplicação Conectada.

# ObraFácil - Sistema de Gestão de Obras

**ObraFácil** é um sistema de gestão de obras desenvolvido com **Spring Boot 3.5.0** e **Java 21**, utilizando **MySQL** como banco de dados. O projeto foi construído com foco em simplicidade, organização e boas práticas de desenvolvimento backend com APIs RESTful.

##  Tecnologias Utilizadas

- Java 21
- Spring Boot 3.5.0
- Spring Data JPA
- MySQL 8.0
- Lombok
- SpringDoc OpenAPI (Swagger)
- Maven

##  Como Executar o Projeto

### Pré-requisitos

- Java 21 instalado
- MySQL 8.0 rodando localmente
- IDE (recomendado: IntelliJ IDEA)
- Maven

##  Documentação da API

Após iniciar a aplicação, acesse:

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/api-docs

##  Endpoints Principais

### Obras
- `GET /api/obras` - Listar obras
- `POST /api/obras` - Criar obra
- `GET /api/obras/{id}` - Buscar obra por ID
- `PUT /api/obras/{id}` - Atualizar obra
- `PATCH /api/obras/{id}/status` - Atualizar status
- `DELETE /api/obras/{id}` - Arquivar obra
- `GET /api/obras/{id}/resumo` - Resumo da obra

### Etapas
- `GET /api/obras/{obraId}/etapas` - Listar etapas
- `POST /api/obras/{obraId}/etapas` - Criar etapa
- `GET /api/etapas/{id}` - Buscar etapa
- `PUT /api/etapas/{id}` - Atualizar etapa
- `PATCH /api/etapas/{id}/progresso` - Atualizar progresso
- `DELETE /api/etapas/{id}` - Remover etapa

### Itens de Checklist
- `GET /api/etapas/{etapaId}/itens` - Listar itens
- `POST /api/etapas/{etapaId}/itens` - Criar item
- `PUT /api/itens-checklist/{id}` - Atualizar item
- `PATCH /api/itens-checklist/{id}/status` - Atualizar status
- `DELETE /api/itens-checklist/{id}` - Remover item

### Cronograma
- `GET /api/obras/{obraId}/cronograma` - Obter cronograma
- `POST /api/obras/{obraId}/cronograma` - Criar cronograma
- `GET /api/obras/{obraId}/cronograma/gantt` - Dados para Gantt
- `POST /api/obras/{obraId}/cronograma/marcos` - Adicionar marco
- `PUT /api/cronograma/marcos/{id}` - Atualizar marco
- `DELETE /api/cronograma/marcos/{id}` - Remover marco

### Clientes e Usuários
- `GET /api/clientes` - Listar clientes
- `POST /api/clientes` - Criar cliente
- `GET /api/usuarios` - Listar usuários
- `POST /api/usuarios` - Criar usuário

##  Desenvolvimento Local

```bash
# Compilar o projeto
./mvnw clean package -DskipTests

# Executar localmente (requer MySQL rodando)
./mvnw spring-boot:run
```

##  Estrutura do Projeto

```
src/main/java/br/com/obrafacil/gestaoobras/
├── config/          # Configurações (Swagger)
├── controller/      # Controllers REST
├── dto/            # Data Transfer Objects
├── enums/          # Enumerações
├── model/          # Entidades JPA
└── repository/     # Repositórios JPA
```

##  Tecnologias Utilizadas

- **Spring Boot 3.5.0**
- **Spring Data JPA**
- **MySQL**
- **SpringDoc OpenAPI (Swagger)**
- **Lombok**


##  Notas

- As tabelas do banco de dados são criadas automaticamente na primeira execução
- O Swagger UI fornece uma interface interativa para testar todos os endpoints
- Todos os endpoints seguem as convenções REST
- O projeto inclui validações e tratamento de erros adequados

##  Autor

Desenvolvido por **Leandro Barreto**  
[🔗 LinkedIn](https://www.linkedin.com/in/leandro-barreto-5128a223a/)  
[🔗 GitHub](https://github.com/LeandroBryto)

---

> Este repositório faz parte do meu processo de aprendizado e evolução como desenvolvedor backend. A ideia é continuar melhorando o sistema com base em boas práticas e necessidades reais de um sistema de gestão de obras.

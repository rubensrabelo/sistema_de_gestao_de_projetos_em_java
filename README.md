# Sistema de Gestão de Projetos

Aplicação desenvolvida para organizar e gerenciar **projetos**, **tarefas** e **colaboradores**, utilizando uma estrutura relacional de dados. O sistema permite:

- Rastrear o progresso de tarefas e projetos  
- Gerenciar atribuições entre colaboradores  
- Gerar relatórios detalhados de produtividade  

---

## Diagrama de classes

``` mermaid
classDiagram
  direction LR
  class Collaborator {
    name: str
    email: str
    function: str
  }
  
  class Task {
    name: str
    description: str
    delivery_forecast: datetime
    created_date: datetime
    updated_date: datetime
    status: StatusEnum
  }
  
  class Project {
    name: string
    description: string
    start_date: datetime
    created_date: datetime
    updated_date: datetime
    status: StatusEnum
  }

  Project "1"-- "*" Task
  Task "*"-- "*" Collaborator

```
---

## Estrutura do Projeto

Organizada em camadas bem definidas, seguindo boas práticas para aplicações Spring Boot. Todos os pacotes estão localizados em:
`src/main/java/com/management/project`


### config/
Classes de configuração da aplicação:

- `ModelMapperConfig`
- `OpenApiConfig` – Configuração do Swagger
- `WebConfig` – Configuração de Content Negotiation

### controller/
Responsável por expor as rotas (endpoints) da aplicação.

#### docs/
Interfaces que definem a estrutura dos controladores para o Swagger:

- `CollaboratorControllerDocs`
- `ProjectControllerDocs`
- `TaskControllerDocs`

#### exceptions/
- `StandardError` – Modelo de erros retornados pela API  
- `ResourceExceptionHandler` – Captura e personaliza mensagens de erro

---

### data/dto/
Responsável pela comunicação entre a API e o cliente.

Cada entidade possui:
- `ResponseDTO`
- `CreateDTO`
- `UpdateDTO`

DTOs adicionais:
- `CollaboratorTaskDTO`
- `CollaboratorTaskCount`
- `ProjectResponseWithTasksDTO`
- `ProjectTaskCountDTO`
- `TaskCollaboratorCountDTO`

---

### model/
Contém as entidades mapeadas para o banco de dados.

#### enums/
- `FunctionEnum` – Função do colaborador  
- `StatusEnum` – Status de projetos e tarefas

Entidades principais:
- `Collaborator`
- `Project`
- `Task`

---

### repository/
Camada de acesso ao banco de dados com Spring Data JPA:

- `CollaboratorRepository`
- `ProjectRepository`
- `TaskRepository`

---

### service/
Camada de regras de negócio, que conecta os controllers aos repositórios.

#### exceptions/
Exceções personalizadas:
- `DatabaseException`
- `DuplicateAssignmentException`
- `EmailAlreadyExistsException`
- *entre outras...*

Serviços principais:
- `CollaboratorService`
- `ProjectService`
- `TaskService`

---

### test/
Testes unitários focados nos serviços de cada entidade.

---

## 🛠️ Tecnologias Utilizadas

- **Java + Spring Boot** – Backend robusto e modular.
- **Spring Data JPA** – Acesso simplificado ao banco de dados.
- **PostgreSQL** – Banco de dados relacional.
- **Swagger/OpenAPI** – Documentação interativa da API.
- **Bean Validation** – Validação automática de dados.
- **Flyway** – Versionamento de banco de dados.
- **ModelMapper** – Conversão entre entidades e DTOs.
- **HATEOAS** – Navegação entre recursos na API.

---


## Acesse a Documentação Interativa

Acesse a documentação da API diretamente pelo navegador:

🔗 [Swagger UI – Documentação da API](http://localhost:8080/swagger-ui.html)


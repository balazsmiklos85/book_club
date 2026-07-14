# Architecture Overview

## Clean Architecture

The application follows [Clean Architecture](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html) principles:

- **Presenters** are ERB views that handle rendering
- **Gateways** are Hanami/ROM repositories and relations for data access
- **Controllers** are Hanami actions that handle HTTP requests
- **Use cases** are Hanami operations encapsulating business logic

## Component Diagrams

```plantuml
@startuml

package "Controllers" {
  component "Login::New" as LoginNew
  component "Register::New" as RegisterNew
  component "Register::Create" as RegisterCreate
  component "Session::Create" as SessionCreate
}

package "Presenters" {
  component "Login::View" as LoginView
  component "Register::View" as RegisterView
}

package "Use cases" {
  component Register as RegisterOp
  component Login as LoginOp
  component RegisterContract as RegisterContract
}

package "Database gateway" {
  component UsersRepo as UsersRepo
  component EmailsRepo as EmailsRepo
  component UsersRelation as UsersRelation
  component EmailsRelation as EmailsRelation
  component UserPasswordRelation as UserPasswordRelation
}

package "Domain layer" {
  component EmailAddressType as EmailAddressType
  component UuidType as UuidType
}

database PostgreSQL as DB

Browser --> LoginNew
Browser --> RegisterNew
Browser --> RegisterCreate
Browser --> SessionCreate

RegisterNew --> RegisterView
LoginNew --> LoginView

RegisterCreate --> RegisterOp
SessionCreate --> LoginOp

RegisterOp --> RegisterContract
RegisterOp --> UsersRepo
RegisterOp --> EmailsRepo
LoginOp --> EmailsRepo
LoginOp --> UserPasswordRelation

UsersRepo --> UsersRelation
EmailsRepo --> EmailsRelation
UserPasswordRelation --> DB

UsersRelation --> DB
EmailsRelation --> DB

RegisterContract --> EmailAddressType
RegisterContract --> UuidType
@enduml
```

The diagram shows the application's component structure organized by Clean Architecture layers.
Each layer delegates inward toward the domain layer.
For detailed component documentation, see:
- [Controllers](../components/controllers.md)
- [Presenters](../components/views.md)
- [Use cases](../components/business-logic.md)
- [Database gateway](../components/database.md)

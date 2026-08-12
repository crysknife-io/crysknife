# Crysknife Multi-Module Sample

A multi-module Todo List application built with [Crysknife](https://github.com/crysknife-io/crysknife).

## What This Demonstrates

- Multi-module Maven project structure for J2CL
- Shared model and service interface in a plain Java module
- `@Singleton` implementation of shared interface, resolved by crysknife DI
- `@Templated` with `@DataField` and `@EventHandler`
- `@PostConstruct` lifecycle

## Prerequisites

- Java 21
- Maven 3.0+

## Build

```bash
mvn clean compile
```

## Run

```bash
open client/target/gwt/launcherDir/multi-module-client/multi-module-client/index.html
```

## Project Structure

```
shared/                                           # Plain Java module
└── src/main/java/.../shared/
    ├── model/Todo.java                           # Data model
    └── service/TodoService.java                  # Service interface

client/                                           # J2CL client module
└── src/main/
    ├── java/.../client/
    │   ├── App.java                              # @Application entry point
    │   ├── TodoComponent.java                    # @Singleton @Templated UI
    │   └── service/TodoServiceImpl.java          # Service implementation
    └── resources/.../client/
        ├── public/index.html                     # Host page
        └── todocomponent.html                    # HTML template
```

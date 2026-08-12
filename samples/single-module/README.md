# Crysknife Single-Module Sample

A minimal "Hello World" application built with [Crysknife](https://github.com/crysknife-io/crysknife).

## What This Demonstrates

- `@Application` + `@GWT3EntryPoint` bootstrap
- `@Singleton` with `@Inject` field injection
- `@Templated` HTML template with `@DataField` binding
- `@EventHandler` for DOM events
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
open target/gwt/launcherDir/single-module/single-module/index.html
```

## Project Structure

```
src/main/java/io/crysknife/samples/single/
├── App.java              # @Application entry point
└── HelloComponent.java   # @Singleton @Templated component

src/main/resources/io/crysknife/samples/single/
├── public/index.html     # Host page
└── hellocomponent.html   # HTML template
```

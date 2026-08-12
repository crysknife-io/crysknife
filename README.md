[![GitHub license](https://img.shields.io/github/license/crysknife-io/crysknife)](https://github.com/crysknife-io/crysknife/blob/main/LICENSE)
![Sonatype Nexus (Releases)](https://img.shields.io/nexus/r/io.crysknife/crysknife-core?server=https%3A%2F%2Foss.sonatype.org&style=plastic)
[![Join the chat at https://gitter.im/vertispan/j2cl](https://img.shields.io/badge/GITTER-join%20chat-green.svg)](https://gitter.im/vertispan/j2cl?utm_source=badge)

# Crysknife is a Jakarta CDI like framework for J2CL.

- [1. Introduction](#1-introduction)
    - [1.1. Motivation](#11-motivation)
    - [1.2. Required software](#12-required-software)
    - [1.3. Getting Started with Crysknife](#13-getting-started-with-crysknife)
        - [1.3.1. Technology Primer](#131-technology-primer)
           - [1.3.1.1. J2CL](#1311-j2cl)
           - [1.3.1.2 Closure Compiler](#1312-closure-compiler)
           - [1.3.1.3 J2CL Maven Plugin](#1313-j2cl-maven-plugin)
           - [1.3.1.4 Jakarta CDI](#1314-jakarta-cdi)
        - [1.3.2. Creating your first project](#132-creating-your-first-project)
- [2. A Gentle Introduction to CDI](#2-a-gentle-introduction-to-cdi)
    - [2.1. What is CDI?](#21-what-is-cdi)
    - [2.2. What is a Bean?](#22-what-is-a-bean)
    - [2.3. Scopes](#23-scopes)
    - [2.4. Injection Points](#24-injection_points)
    - [2.5 Qualifiers](#25-qualifiers)
      - [2.5.1 @Qualifier](#251-qualifier)
      - [2.5.2 @Named](#252-named)
      - [2.5.3 @Typed](#253-typed)
      - [2.5.4 @Alternative](#254-alternative)
      - [2.5.5 @Default](#255-default)
    - [2.6. Producers](#26-producers)
    - [2.7. Events](#27-events)
    - [2.8 Lifecycle](#28-lifecycle)
      - [2.8.1 @PostConstruct](#281-postconstruct)
      - [2.8.2 @PreDestroy](#282-predestroy)
      - [2.8.3 @Startup](#283-startup)
    - [2.9 Interceptors](#29-interceptors)
      - [2.9.1 @InterceptorBinding](#291-interceptorbinding)
      - [2.9.2 @AroundInvoke](#292-aroundinvoke)
      - [2.9.3 Limitations](#293-limitations)
    - [2.10 Decorators](#210-decorators)
      - [2.10.1 @Decorator + @Delegate](#2101-decorator--delegate)
      - [2.10.2 Decorator Chains](#2102-decorator-chains)
      - [2.10.3 Limitations](#2103-limitations)
    - [2.11 BeanManager](#211-beanmanager)
      - [2.11.1 ManagedInstance and Instance](#2111-managedinstance-and-instance)
- [3.0 UI Components](#30-ui-components)
- [3.1. Basic templated bean](#31-basic-templated-bean)
- [3.2. Custom template names](#32-custom-template-names)
- [3.3. Create an HTML template](#33-create-an-html-template)
  - [3.3.1. Using an HTML fragment as a template](#331-using-an-html-fragment-as-a-template)
  - [3.3.2. Select a fragment from a larger HTML template](#332-select-a-fragment-from-a-larger-html-template)
  - [3.3.3. Using a single component instance](#333-using-a-single-component-instance)
  - [3.3.4. Adding behaviour to your components](#334-adding-behaviour-to-your-components)
    - [3.3.4.1. Annotate Elements in the template with @DataField](#3341-annotate-elements-in-the-template-with-datafield)
    - [3.3.4.2. Allowed DataField types](#3342-allowed-datafield-types)
    - [3.3.4.3. Event handlers](#3343-event-handlers)
- [3.4. Crysknife UI Navigation](#34-crysknife-ui-navigation)
  - [3.4.1. Declaring a Page](#341-declaring-a-page)
  - [3.4.2. The Default (Starting) Page](#342-the-default-starting-page)
  - [3.4.3. Page Roles](#343-page-roles)
  - [3.4.4. Page Lifecycle](#344-page-lifecycle)
  - [3.4.5. Following a Manual Link](#345-following-a-manual-link)
  - [3.4.6. Page State (URL Parameters)](#346-page-state-url-parameters)
  - [3.4.7. Page Security](#347-page-security)
    - [3.4.7.1. SecurityContext](#3471-securitycontext)
    - [3.4.7.2. @RolesAllowed and @DenyAll](#3472-rolesallowed-and-denyall)
    - [3.4.7.3. LoginPage and SecurityError Roles](#3473-loginpage-and-securityerror-roles)
    - [3.4.7.4. @IfRole — Element-Level Security](#3474-ifrole--element-level-security)
- [3.5. Data Binding](#35-data-binding)
  - [3.5.1. @Bindable Models](#351-bindable-models)
  - [3.5.2. @Bound Fields](#352-bound-fields)
  - [3.5.3. Nested Bean Properties (Dot-Notation)](#353-nested-bean-properties-dot-notation)
  - [3.5.4. Programmatic Binding with DataBinder](#354-programmatic-binding-with-databinder)
  - [3.5.5. Property Change Handlers](#355-property-change-handlers)
  - [3.5.6. Collection Binding with ListComponent](#356-collection-binding-with-listcomponent)
  - [3.5.7. Converters](#357-converters)
  - [3.5.8. Limitations](#358-limitations)
- [3.6. REST Client](#36-rest-client)
  - [3.6.1. Dependencies](#361-dependencies)
  - [3.6.2. Defining a JAX-RS Service Interface](#362-defining-a-jax-rs-service-interface)
  - [3.6.3. Configuring the REST Endpoint](#363-configuring-the-rest-endpoint)
  - [3.6.4. Injecting and Using the Caller](#364-injecting-and-using-the-caller)
  - [3.6.5. Using Qualifiers for Multiple Endpoints](#365-using-qualifiers-for-multiple-endpoints)
- [3.8. WebSocket Client](#38-websocket-client)
  - [3.8.1. Dependencies](#381-dependencies)
  - [3.8.2. Defining a Client Endpoint](#382-defining-a-client-endpoint)
  - [3.8.3. Connecting](#383-connecting)
  - [3.8.4. Sending Messages via Session](#384-sending-messages-via-session)
  - [3.8.5. Dual @OnMessage — Text and Binary](#385-dual-onmessage--text-and-binary)
  - [3.8.6. Lifecycle Callbacks](#386-lifecycle-callbacks)
  - [3.8.7. Limitations](#387-limitations)
- [4. Contributing to Crysknife](#4-contributing-to-crysknife)
- [5. Crysknife License](#5-crysknife-license)



# 1. Introduction
   Crysknife is a J2CL-based web framework that allows you to write your web application in Java, and compile it into JavaScript. It is designed to be used with the J2CL transpiler, and is not compatible with the GWT. Crysknife is a relaxed implementation of Jakarta CDI specification that also has html templating engine and a set of helper processors that can speed up J2CL development by generating boiler-plate code.

## 1.1. Motivation

The goal of this project is to create an Red Hat Errai-like framework for J2CL. So many parts of it should be familiar to you if you have used Errai before. 

## 1.2. Required software

Crysknife requires the following software in order to run:

* Java 21 or later
* Maven 3.9.0 or later
* Basic knowledge of J2Cl and Maven
* j2cl-maven-plugin 0.22 or later


## 1.3. Getting Started with Crysknife

Crysknife is a framework which allows you to write large web-based applications entirely in Java with a minimum knowledge of Javascript. Jakarta CDI ( Contexts and Dependency Injection for the Java EE platform) is a set of services that allow you to inject dependencies into your Java code in a type-safe way. We believe that it helps to write more modular and reusable code, speeding up development time and reducing the number of errors that can be introduced.  

### 1.3.1. Technology Primer

Crysknife is designed to be used with Google's J2CL (Java to javascript transpiler) and Google's Closure Compiler. Native environment for that tools is Google's Bazel, but we recommend to use Maven as a build tool.

#### 1.3.1.1. J2CL

J2CL is a Google's Java to Closure Javascript transpiler, it means, it generates Javascript code that is compatible with Google's Closure Compiler and must be compiled to executable javascript by it at the end. J2CL java emulation has some limitations. For example, it does not support reflection, multithreading, concurrency, filesystem io and some other features. To check the full list of limitations, please visit J2CL limitations page (https://github.com/google/j2cl/blob/master/docs/limitations.md).
J2CL allows us to work with Javascript API's from the Java, to read more take a looks at J2CL JsInterop documentation ( https://github.com/google/j2cl/blob/master/docs/jsinterop-by-example.md).

#### 1.3.1.2. Closure Compiler

Closure Compiler is a tool for making JavaScript download and run faster. It is a true compiler for JavaScript. Instead of compiling from a source language to machine code, it compiles from JavaScript to better JavaScript. It parses your JavaScript, analyzes it, removes dead code and rewrites and minimizes what's left. It also checks syntax, variable references, and types, and warns about common JavaScript pitfalls.

In j2cl-maven-plugin (and in a bazel of course), Closure Compiler is used to compiler Closure annotated javascript, produced by J2CL, to executable javascript. To read more about Closure Compiler, please visit official documentation (https://developers.google.com/closure/compiler).

#### 1.3.1.3. J2CL Maven Plugin

J2CL Maven Plugin is a Maven plugin that allows you to compile your J2CL project to executable javascipt application. It's a community driven project, not supported by Google. To read more about J2CL Maven Plugin, please visit official documentation (https://github.com/Vertispan/j2clmavenplugin)

#### 1.3.1.4. Jakarta CDI

Jakarta CDI helps in creating clean, decoupled, and maintainable code by promoting a well-defined programming model based around beans, contexts, and dependency injection. Crysknife is a relaxed implementation of Jakarta CDI specification. It's means that it does not support all features of Jakarta CDI, but it's enough to write a large web-based applications entirely in Java with a minimum knowledge of Javascript.

To read more about Jakarta CDI, please visit official documentation (https://jakarta.ee/specifications/cdi/2.0/cdi-spec-2.0.html)

### 1.3.2. Creating your first project

* pre-requisites: working maven j2cl application. If you don't have one, please follow the instructions in the J2CL Maven Plugin documentation (https://github.com/Vertispan/j2clmavenplugin). 

tip : clone application from https://github.com/treblereel/j2cl-tests, use demo or generate it from j2cl-maven-plugin archetype.

* add crysknife  dependencies to your pom.xml

```xml

<dependency>
    <groupId>io.crysknife</groupId>
    <artifactId>crysknife-core</artifactId>
    <version>current-version</version>
</dependency>

<dependency>
    <groupId>io.crysknife</groupId>
    <artifactId>crysknife-processor</artifactId>
    <scope>provided</scope>
    <version>current-version</version>
</dependency>

```

in case you want to have html templating engine and set of useful tools, add the following dependency:

```xml

<dependency>
    <groupId>io.crysknife.ui</groupId>
    <artifactId>crysknife-ui-core</artifactId>
    <version>current-version</version>
</dependency>

<dependency>
    <groupId>io.crysknife.ui</groupId>
    <artifactId>crysknife-ui-generator</artifactId>
    <scope>provided</scope>
    <version>current-version</version>
</dependency>

```

* add @Application annotation to your entry point class
* you can combine it with @GWT3EntryPoint (gwt3-processors project) annotation to make sure that your entry point class will be executed once the page loads.
* Crysknife will generate a bootstrap class (in the form of ${CLASSNAME}+Boostrap) that will initialize your application.
* now all you need your to do is to call the initialize method of the generated bootstrap class.
* Field injection points are injected after the application is initialized.
* @PostConstruct annotated method will be called after the application is initialized.
* Application is ready to use.

tip: to speedup the build, you can reduce the scope of package scanning by specifying the packages attribute of the @Application annotation.

```java
package io.crysknife.demo.client;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;

import io.crysknife.annotation.Application;
import org.treblereel.j2cl.processors.annotations.GWT3EntryPoint;

@Application(packages = {"io.crysknife"})
public class App {

    @Inject
    private MyBean myBean;
    
    @GWT3EntryPoint
    public void onModuleLoad() {
        new AppBootstrap(this).initialize();
    }

    @PostConstruct
    public void init() {
        
    }

}

```

How to compile and run your application and how to run development mode, please read in the J2CL Maven Plugin documentation.

# 2. A Gentle Introduction to CDI

## 2.1. What is CDI?

CDI stands for Contexts and Dependency Injection. Dependency Injection is a design pattern that allows us to remove the hard-coded dependencies and make our application loosely coupled, extendable and maintainable. Crysknife doesn't support transactions. 

tip: It's important to note that Crysknife provides lazy initialization of beans. It means that the bean is created only when it is needed.

## 2.2. What is a Bean?

In CDI context, bean is a class that is managed by the container. It means that the container is responsible for creating, destroying and injecting beans. 

tip: Crysknife uses annotation processing and classGraph to find all beans in the classpath. It means that you must annotate your beans with @ApplicationScoped/Singletone or @Dependent annotation to make them visible to the container.

## 2.3. Scopes

CDI defines a set of scopes that define the lifecycle of a bean. The following scopes are supported by Crysknife:

* @ApplicationScoped/Singleton - the bean lives as long as the application lives. It means that the bean is created when the application is initialized and destroyed when the application is destroyed.

* @Dependent - the bean lives as long as the bean that injected it lives. It means that the bean is created when the bean that injected it is created and destroyed when the bean that injected it is destroyed.

```java

@ApplicationScoped
public class MyBean {
}
```
Remember, CDI beans must be public, never final/abstract, and have a default constructor or a constructor annotated @Inject.


## 2.4. Injection Points

Injection points are the points where the dependencies are injected. Crysknife supports the following injection points:

* Field injection - the dependencies are injected into the fields of the bean. The field must be annotated with @Inject annotation.
* Constructor injection - the dependencies are injected into the constructor of the bean. The constructor must be annotated with @Inject annotation.

Here is a simple example of a bean with a field injection point and a constructor injection point:

```java

import javax.swing.text.MutableAttributeSet;

@ApplicationScoped
public class MyBean {

    @Inject
    private MyDependentBean myDependentBean;

    @Inject
    public MyBean(MutableAttributeSet attributeSet) {
        
    }

}
```

## 2.5 Qualifiers

Qualifiers in CDI are annotations that help to disambiguate beans and injections points, especially when there are multiple implementations or instances of a bean type. They provide a way to differentiate or qualify which bean you want to be injected in a particular use case.


### 2.5.1 @Qualifier

Suppose you have an interface PaymentService, and there are two implementations, CreditCardPaymentService and PayPalPaymentService. If you have a code piece where you want to inject a PaymentService, you would face an ambiguity as to which implementation to use. Qualifiers help resolve such ambiguities.

```java
public interface PaymentService {
    void pay(BigDecimal amount);
}

@CreditCard
@ApplicationScoped
public class CreditCardPaymentService implements PaymentService {
    public void pay(BigDecimal amount) {
        // Implementation for credit card payment
    }
}

@PayPal
@ApplicationScoped
public class PayPalPaymentService implements PaymentService {
    public void pay(BigDecimal amount) {
        // Implementation for PayPal payment
    }
}
```

Here, @CreditCard and @PayPal are qualifiers. You define these qualifiers as annotations:

```java
@Qualifier
@Retention(RUNTIME)
@Target({TYPE, METHOD, FIELD, PARAMETER})
public @interface CreditCard {}

@Qualifier
@Retention(RUNTIME)
@Target({TYPE, METHOD, FIELD, PARAMETER})
public @interface PayPal {}
```

Now you can use these qualifiers to specify which implementation you want to be injected:

```java
public class PaymentProcessor {
    
    @Inject
    @CreditCard
    private PaymentService creditCardPaymentService;
    
    @Inject
    @PayPal
    private PaymentService payPalPaymentService;

    public void processCreditCardPayment(BigDecimal amount) {
        creditCardPaymentService.pay(amount);
    }

    public void processPayPalPayment(BigDecimal amount) {
        payPalPaymentService.pay(amount);
    }
}
```

### 2.5.2 @Named

The @Named annotation in CDI (Context and Dependency Injection) is a special kind of qualifier that allows a bean to be accessed by string name, rather than by type.

```java
import javax.inject.Named;

@Named("creditCardPaymentService")
@ApplicationScoped
public class CreditCardPaymentService implements PaymentService {
    // ...
}

@Named("payPalPaymentService")
@ApplicationScoped
public class PayPalPaymentService implements PaymentService {
    // ...
}
```

Now you can use these qualifiers to specify which implementation you want to be injected:

```java
public class PaymentProcessor {
    
    @Inject
    @Named("creditCardPaymentService")
    private PaymentService creditCardPaymentService;
    
    @Inject
    @Named("payPalPaymentService")
    private PaymentService payPalPaymentService;

    public void processCreditCardPayment(BigDecimal amount) {
        creditCardPaymentService.pay(amount);
    }

    public void processPayPalPayment(BigDecimal amount) {
        payPalPaymentService.pay(amount);
    }
}
```

### 2.5.3 @Typed

The @Typed annotation in CDI is used to restrict the set of bean types a bean has, limiting the types for which the bean is eligible for injection. By default, a bean is eligible for injection for all its types, which include the bean's class and all its superclasses and interfaces.

Suppose you have an interface PaymentService and two classes CreditCardPaymentService and PayPalPaymentService that implement this interface. Additionally, suppose CreditCardPaymentService extends a class called AbstractPaymentService:

```java
public interface PaymentService {
    void pay(BigDecimal amount);
}

public class AbstractPaymentService {
    // Some common payment processing code...
}

@Singleton
public class CreditCardPaymentService extends AbstractPaymentService implements PaymentService {
    public void pay(BigDecimal amount) {
        // Credit card payment implementation
    }
}

@Singleton
public class PayPalPaymentService implements PaymentService {
    public void pay(BigDecimal amount) {
        // PayPal payment implementation
    }
}
```

Now, if you wish to limit the CreditCardPaymentService bean to only be injectable as a CreditCardPaymentService (and not as an AbstractPaymentService or PaymentService), you would use the @Typed annotation like so:

```java
@Typed(CreditCardPaymentService.class)
@Singleton
public class CreditCardPaymentService extends AbstractPaymentService implements PaymentService {
    public void pay(BigDecimal amount) {
        // Credit card payment implementation
    }
}
```

With this @Typed annotation, CreditCardPaymentService will only be eligible for injection at injection points of type CreditCardPaymentService. It won't be eligible for injection at injection points of type AbstractPaymentService or PaymentService. This can be useful when you want to control the types of a bean that are exposed for injection, especially in scenarios where there might be ambiguity or potential conflicts with other beans in the CDI container.

Here's an injection point that would work with the above @Typed annotation:

```java
@Inject
private CreditCardPaymentService creditCardPaymentService;
```

Here's an injection point that would not work with the above @Typed annotation:

```java
@Inject
private PaymentService paymentService;
```

#### 2.5.4 @Alternative
The @Alternative annotation is used to specify alternative implementations of beans.

This feature is particularly useful in scenarios where you have multiple implementations of an interface, and you want to choose at deployment time which implementation should be used. It can also be handy in a development vs. production scenario where you might have a mock or stub implementation for testing and a real implementation for production.

Suppose you have an interface PaymentService and two implementations: CreditCardPaymentService and PayPalPaymentService.

```java
public interface PaymentService {
    void pay(BigDecimal amount);
}

@ApplicationScoped
public class CreditCardPaymentService implements PaymentService {
    public void pay(BigDecimal amount) {
        // Credit card payment implementation
    }
}

@Alternative
@ApplicationScoped
public class PayPalPaymentService implements PaymentService {
    public void pay(BigDecimal amount) {
        // PayPal payment implementation
    }
}
```
In the above example, PayPalPaymentService is marked as an alternative implementation of PaymentService with the @Alternative annotation.

Now, wherever a PaymentService is injected, the PayPalPaymentService will be used:

```java
@Inject
private PaymentService paymentService;  // PayPalPaymentService will be injected
```

This mechanism allows for a great deal of flexibility, enabling you to switch implementations in a type-safe and configuration-driven manner, which can be very handy in a variety of situations such as testing, different deployment scenarios, etc.

### 2.5.5 @Default

The @Default annotation is a built-in qualifier that gets implicitly applied to all beans if no other qualifier is specified. This annotation marks a bean as the default implementation for a certain type, making it the implementation that gets injected when no other qualifier is used at an injection point.

Suppose you have an interface PaymentService and two implementations: CreditCardPaymentService and PayPalPaymentService.

```java
public interface PaymentService {
    void pay(BigDecimal amount);
}

@Default  // This annotation is optional here, as @Default is implied if no other qualifier is specified.
public class CreditCardPaymentService implements PaymentService {
    public void pay(BigDecimal amount) {
        // Credit card payment implementation
    }
}

@PayPalQualifier
public class PayPalPaymentService implements PaymentService {
    public void pay(BigDecimal amount) {
        // PayPal payment implementation
    }
}
```

In this setup, CreditCardPaymentService is the default implementation of PaymentService due to the @Default annotation (or simply because no other qualifier is specified). Whenever you inject a PaymentService without specifying a qualifier, CreditCardPaymentService will be used:

```java
@Inject
private PaymentService paymentService;  // CreditCardPaymentService will be injected
```

This setup allows you to control which implementation is used by default and which implementations require explicit qualifiers, providing a clean and type-safe mechanism to manage dependencies in your application.


## 2.6. Producers

CDI Producers are a powerful feature that allows for custom instantiation of beans. They are methods annotated with the @Produces annotation, and they can be used to programmatically create bean instances, or produce a value to be injected, instead of letting the CDI container do the instantiation automatically. This is especially useful when the bean you want to inject requires some custom initialization logic or when you want to inject an object that isn't a bean.

```java
public class PaymentProducer {

    @Produces
    @ApplicationScoped
    public PaymentService createPaymentService() {
        return new CreditCardPaymentService("my-credentials");
    }
}
```

In the example above, a producer method createPaymentService is defined which creates and returns a new instance of CreditCardPaymentService. The @Produces annotation tells CDI that this method should be used to create instances of PaymentService. Now, whenever a PaymentService is injected elsewhere in the application, this producer method will be called to obtain an instance:

```java
    @Inject
    private PaymentService paymentService;  // CreditCardPaymentService will be injected
```

Producer methods can also be parameterized, taking advantage of CDI’s ability to inject values into methods.

```java
@ApplicationScoped
public class PaymentProducer {

    @Inject
    private PaymentConfig paymentConfig;

    @Produces
    @ApplicationScoped
    public PaymentService createPaymentService() {
        return new CreditCardPaymentService(paymentConfig.getCredentials());
    }
}
```
tip: You can use qualifiers to disambiguate between multiple producer methods that produce the same type of bean.

## 2.7. Events

CDI (Context and Dependency Injection) Events are a part of the CDI that allows for decoupled event handling within an application. They allow different components of an application to communicate with each other in a loosely coupled way. This is done through a publish-subscribe model where beans can fire events and other beans can observe and react to these events.

* Event Objects: These are instances of any Java class or type that serve as a carrier for information related to an event.
* Event Producers: These are the parts of the code that generate or fire events. Events are fired using an Event object, which is injected into the bean that wants to fire the event.
* Event Observers: These are methods in other beans that are annotated with the @Observes annotation. They listen for and react to events of a particular type.

```java
// Define an event object
public class PaymentEvent {
    private BigDecimal amount;

    // constructor, getters, and setters
}

// A bean that fires events
@ApplicationScoped
public class PaymentService {

    @Inject
    private Event<PaymentEvent> paymentEvent;

    public void makePayment(BigDecimal amount) {
        // business logic
        paymentEvent.fire(new PaymentEvent(amount));
    }
}

// A bean that observes events
@ApplicationScoped
public class PaymentAuditService {

    public void onPayment(@Observes PaymentEvent paymentEvent) {
        // logic to audit payment
        BigDecimal amount = paymentEvent.getAmount();
        System.out.println("Payment made: " + amount);
    }
}
```

* PaymentEvent is a simple POJO (Plain Old Java Object) that encapsulates information about a payment.
* PaymentService is a bean that makes payments and fires PaymentEvent events using the injected Event object.
* PaymentAuditService is a bean that observes PaymentEvent events. Its onPayment method is annotated with @Observes and will be called whenever a PaymentEvent is fired, allowing it to react to payments by logging them.

This example demonstrates how CDI Events allow for a clean, decoupled communication between different parts of an application. Beans can fire events without needing to know anything about what other beans might be observing those events, and beans can observe events without needing to know where those events come from.

tip: If PaymentAuditService is @ApplicationScoped/@Singleton and this bean has never been injected, new instance of PaymentAuditService will be created and the onPayment method will be called.

## 2.8 Lifecycle

The lifecycle of a bean includes its instantiation, initialization, use, and destruction.

### 2.8.1 @PostConstruct

@PostConstruct is an annotation in Java used to mark a method for post-construction callback. The method annotated with @PostConstruct is called by the container once the bean has been constructed and dependency injection has been performed, but before the bean is made available for use. This allows the bean to perform any necessary initialization before it is used. The method annotated with @PostConstruct must return void, and take no parameters.
        
```java
@Dependent
public class MyBean {

    private String message;

    @PostConstruct
    private void init() {
        message = "Hello, World!";
    }

    public String getMessage() {
        return message;
    }
}
```

### 2.8.2 @PreDestroy

@PreDestroy is an annotation in Java that marks a method for pre-destruction callback. The method annotated with @PreDestroy is called by the container just before the bean is about to be destroyed. This gives the bean a chance to clean up any resources it has acquired during its lifecycle.

```java
@Dependent
public class MyBean {

    // Some resources
    private SomeResource resource;

    // ... other methods ...

    @PreDestroy
    public void cleanup() {
        resource.release();
    }
}
```

tip: If this bean has dependencies and are have @Dependent scope, they will be destroyed after the @PreDestroy method is called.

### 2.8.3 @Startup

@Startup is an annotation in Java that marks a bean for eager initialization. The bean annotated with @Startup will be initialized when the application starts up, rather than when it is first used. This is useful for beans that need to be initialized before they are used, such as beans that perform some initialization logic or setup some resources that are needed by other beans.


```java
@Startup
@ApplicationScoped
public class MyBean {

    @Inject
    private SomeResource someResource;

    @PostConstruct
    public void init() {
        // Initialize someResource
    }
}
```
A bean, annotated with @Startup, must be a @ApplicationScoped/@Singleton bean. If it is not, the container will throw an exception.

## 2.9 Interceptors

Crysknife supports Jakarta Interceptors, allowing you to intercept method invocations using `@InterceptorBinding` and `@AroundInvoke` annotations. In J2CL mode, interception is implemented via ES6 Proxy `get` traps. In JRE mode (for testing), AspectJ `@Around` advice is generated at compile time.

### 2.9.1 @InterceptorBinding

To create an interceptor, first define a custom interceptor binding annotation:

```java
@InterceptorBinding
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface Logged {}
```

Then create the interceptor class annotated with `@Interceptor` and your binding annotation:

```java
@ApplicationScoped
@Interceptor
@Logged
public class LoggingInterceptor {

    @AroundInvoke
    public Object intercept(InvocationContext ctx) throws Exception {
        System.out.println("Before: " + ctx.getParameters().length + " args");
        Object result = ctx.proceed();
        System.out.println("After: result = " + result);
        return result;
    }
}
```

### 2.9.2 @AroundInvoke

Apply the interceptor binding annotation to individual methods or to the entire class. When applied to a class, all public non-static non-final business methods are intercepted.

Method-level binding:

```java
@ApplicationScoped
public class GreetingService {

    @Logged
    public String greet(String name) {
        return "Hello, " + name + "!";
    }

    // This method is NOT intercepted
    public String notIntercepted() {
        return "No interception here";
    }
}
```

Class-level binding:

```java
@Logged
@ApplicationScoped
public class FullyInterceptedService {

    // All public business methods are intercepted
    public void doWork() { }

    public String process(String input) {
        return input.toUpperCase();
    }
}
```

The `InvocationContext` passed to the `@AroundInvoke` method provides:

* `getTarget()` — the bean instance being intercepted
* `getParameters()` — the method arguments (mutable array)
* `setParameters(Object[])` — replace the method arguments before proceeding
* `proceed()` — invoke the next interceptor in the chain or the original method
* `getContextData()` — a `Map<String, Object>` for passing data between interceptors in the chain

### 2.9.3 Limitations

Because J2CL does not support `java.lang.reflect`, the following `InvocationContext` methods are **not available** and will throw `UnsupportedOperationException`:

* `getMethod()` — cannot return `java.lang.reflect.Method` in J2CL
* `getConstructor()` — cannot return `java.lang.reflect.Constructor` in J2CL

These methods are marked `@GwtIncompatible` in the Jakarta Interceptors API and are stripped by the J2CL transpiler. They work normally in JRE mode (e.g., in unit tests with AspectJ).

Additionally:
* Only `public`, non-`static`, non-`final` methods can be intercepted
* `@PostConstruct` and `@PreDestroy` lifecycle methods are excluded from interception
* Interceptor classes must be managed beans (annotated with a scope such as `@ApplicationScoped`)

## 2.10 Decorators

CDI Decorators allow you to transparently wrap a bean's interface with additional behavior. Unlike interceptors (which are generic and cross-cutting), decorators are aware of the bean's business semantics — they implement the same interface and can selectively override methods.

### 2.10.1 @Decorator + @Delegate

To create a decorator, annotate a class with `@Decorator` and implement the same interface as the bean you want to decorate. Use `@Inject @Delegate` on a field to hold a reference to the original (or next) bean in the chain:

```java
public interface PaymentService {
    void pay(BigDecimal amount);
}

@ApplicationScoped
public class CreditCardPaymentService implements PaymentService {
    public void pay(BigDecimal amount) {
        // process credit card payment
    }
}

@ApplicationScoped
@Decorator
@Priority(1000)
public class LoggingPaymentDecorator implements PaymentService {

    @Inject
    @Delegate
    PaymentService delegate;

    public void pay(BigDecimal amount) {
        System.out.println("Payment of " + amount);
        delegate.pay(amount);
    }
}
```

When you inject `PaymentService`, the container returns the decorator. The decorator's `@Delegate` field receives the original bean:

```java
@Inject
PaymentService paymentService; // returns LoggingPaymentDecorator

@Inject
CreditCardPaymentService directService; // returns the original bean
```

### 2.10.2 Decorator Chains

Multiple decorators for the same interface are ordered by `@Priority` (lowest value = innermost, closest to the original bean):

```java
@Decorator @Priority(1000)
public class LoggingDecorator implements PaymentService { ... }

@Decorator @Priority(2000)
public class SecurityDecorator implements PaymentService { ... }
```

Call chain: `SecurityDecorator → LoggingDecorator → CreditCardPaymentService`

### 2.10.3 Limitations

* Only interface-based decoration is supported — you cannot decorate a concrete class directly
* Decorator classes must be managed beans (annotated with a scope such as `@ApplicationScoped`)
* Each decorator must have exactly one `@Inject @Delegate` field

## 2.11 BeanManager

is a fundamental interface that provides access to the CDI container. It allows developers to programmatically interact with the CDI container to obtain beans or destroy them. This is especially useful in scenarios where dynamic lookup or programmatic interactions with the CDI container are required.

Here some useful cases:

* Obtaining a bean instance by its type and qualifiers
* Obtaining all beans of a certain type and qualifiers
* Destroying a bean instance

```java
@ApplicationScoped
public class MyBean {

    @Inject
    private BeanManager beanManager;

    public void doSomething() {
        // Obtain a bean instance by its type
        MyOtherBean myOtherBean = beanManager.lookupBean(MyOtherBean.class).get();

        // Obtain a bean instance by its type and qualifiers
        MyOtherBean myOtherBean = beanManager.lookupBean(MyOtherBean.class, MyQualifier.class).get();

        // Obtain all beans of a certain type and qualifiers
        Set<MyOtherBean> myOtherBeans = beanManager.lookupBeans(MyOtherBean.class);

        // Destroy a bean instance
        beanManager.destroyBean(myOtherBean);
    }
}
```

### 2.11.1 ManagedInstance and Instance

In the context of CDI Instance<T> is a part of the CDI API that provides a way to obtain instances of a certain bean type T dynamically at runtime. It acts as a programmatic client for beans;

```java
@ApplicationScoped
public class MyBean {

    @Inject
    private Instance<MyOtherBean> myOtherBeanInstance;

    public void doSomething() {
        MyOtherBean myOtherBean = myOtherBeanInstance.get();

    }
}
```

ManagedInstance<T> do pretty much the same thing as Instance<T>, but it also allows you to destroy the bean instance when you are done with it. This is useful in scenarios where you want to obtain a bean instance dynamically, use it, and then destroy it.

Both Instance<T> and ManagedInstance<T> are iterable, allowing you to iterate over all the beans of a certain type.

# 3.0 UI Components

disclaimer: HTML Templated engine is re-implementation of Errai UI templated engine. It's not 100% compatible with Errai UI templated engine.
Some of parts of the documentation are taken from Errai UI documentation. All credits goes to Errai UI team.

A Crysknife component consists of a Java class (the templated bean), an HTML file (the template), and an optional CSS file (the template stylesheet). The template and template stylesheet describe the look of your component. The templated bean uses the @DataField annotation to declare mappings between fields in the templated bean and elements in the template.

## 3.1. Basic templated bean

Here is a basic templated bean with no Java fields mapped to UI elements. Templated bean must implement IsElement interface.

```java
@Templated
@Dependent
public class LoginForm implements IsElement {

}
```
Annotating the type with @Templated and no argument declares that this bean should have a template file LoginForm.html and optionally a stylesheet LoginForm.css in the same package as LoginForm.java

## 3.2. Custom template names
When no argument is provided to @Templated, Crysknife looks in the current package for a template file having the simple class name of the templated bean, suffixed with .html. But @Templated accepts an argument to define an alternatively named or located template, as in the proceeding example.

```java
@Templated("my-template.html")
@Dependent
public class LoginForm implements IsElement {
    /* Looks for my-template.html in LoginForm's package */
}
```
## 3.3. Create an HTML template

Templates in Errai UI may be designed either as an HTML snippet or as a full HTML document. You can even take an existing HTML page and use it as a template. With either approach, the id, class, and data-field attributes in the template identify elements by name. These elements and their children are used in the component to add behavior, and use additional components to add functionality to the template. There is no limit to how many templated beans may share a given HTML template.

### 3.3.1. Using an HTML fragment as a template

Here is a simple HTML fragment for a login form to accompany our @Templated LoginForm bean.

```html
<form>
  <legend>Log in to your account</legend>
  <label for="username">Username</label>
  <input id="username" type="text" placeholder="Username">
  <label for="password">Password</label>
  <input id="password" type="password" placeholder="Password">
  <button>Log in</button>
  <button>Cancel</button>
</form>
```
This fragment can be used with our previous @Templated LoginForm declaration as is.

### 3.3.2. Select a fragment from a larger HTML template

You can also use a full HTML document that is more easily previewed during design. When doing this you must specify the location of the component’s root DOM Element within the template file using a "data-field", id, or class attribute matching the value of the @Templated annotation. There is no limit to how many templated beans may share a single HTML template.

```java
@Templated("my-template.html#login-form")
@Dependent
public class LoginForm implements IsElement {
   /* Specifies that <... id="login-form"> be used as the root Element of this component */
}
```

```html
<!DOCTYPE html>
<html lang="en">
<head>
    <title>A full HTML document</title>
</head>
<body>
    <div>
        <form id="login-form"> 
          <legend>Log in to your account</legend>
          <label for="username">Username</label>
          <input id="username" type="text" placeholder="Username">
          <label for="username">Password</label>
          <input id="password" type="password" placeholder="Password">
          <button>Log in</button>
          <button>Cancel</button>
        </form>
    </div>
    <hr>
    <footer id="theme-footer">
        <p>(c) Company 2023</p>
    </footer>
</body>
</html>
```

The HTML id value of this form Element matches the #login-from part from the @Templated("my-template.html#login-from") declaration. That means that this element will be used as the root of the component defined by LoginForm.java. Note that we could also have used class="login-form" or data-field="login-form" to the same effect.

Multiple components may use the same template, specifying any elements as a their root elements. In particular, note that two or more components may declare the same DOM element of this template file as their root elements; there is no conflict because are each components instantiated with a unique copy of the template DOM rooted at the specified element at runtime (or from the root element if a fragment is not specified.)

For example, the component below also uses the same template file by referencing the template name, and specifying a fragment.
```java
@Templated("my-template.html#theme-footer")
@ApplicationScoped
public class Footer implements IsElement {
   /* Specifies that <... id="theme-footer"> be used as the root Element of this Widget */
}
```

### 3.3.3. Using a single component instance

As with most other features of Crysknife, dependency injection with CDI is the programming model of choice, so when interacting with components defined using Crysknife UI, you should always @Inject references to your components.

In this example we use a single Crysknife UI component by injecting it and adding it to the DOM.

```java
@Application
public class Application {

   @Inject
   private TemplatedBean component;

    @GWT3EntryPoint
    public void onModuleLoad() {
        new ApplicationBootstrap(this).initialize();
    }

   @PostConstruct
   public void init() {
     DomGlobal.body.appendChild(component.getElement());
   }

}
```

### 3.3.4. Adding behaviour to your components

Now that we know how to create a @Templated bean and an HTML template, we can start wiring in functionality and behavior; this is done by annotating fields and methods to replace specific sub-elements of the template DOM. We can even replace portions of the template with other Errai UI components!

#### 3.3.4.1. Annotate Elements in the template with @DataField

In order to substitute elements into the template DOM you must annotate fields in your templated bean with @DataField and mark the HTML template element with a correspondingly named data-field, id, or class attribute. All replacements happen while the component is being constructed; thus, fields annotated with @DataField must either be @Injected or manually initialized when the templated bean is instantiated.

```java
/*
* Here the template file is implicitly LoginForm.html.
* The root element has id, class, or data-field "form".
*/
@Dependent
@Templated("#form")
public class LoginForm implements IsElement {

   // This is the root element of the template, for adding this component to the DOM.
   @Inject
   @DataField
   private HTMLFormElement form;

   // If not otherwise specified, the name to match in the HTML template defaults
   // to the name of the field; in this case, the name would be "username"
   @Inject
   @DataField
   private HTMLInputElement username;

   // The name to reference in the template can also be specified manually
   @Inject
   @DataField("pass")
   private HTMLInputElement password;

   // We can also choose to instantiate our own data fields. Injection is not required.
   @DataField
   private HTMLButtonElement submit = (HTMLButtonElement) document.createElement("button");

   @PostConstruct
   public void init() {
       password.type = "password";
   }
}
```

#### 3.3.4.2. Allowed DataField types

The following types are allowed for @DataField fields:
* HTMLElement and its subtypes
* IsElement and its subtypes

#### 3.3.4.3. Event handlers

It's very handy to bind a user or DOM event handlers to your components. Crysknife UI provides a set of annotations to make this easy.

```java
@Dependent
@Templated
public class WidgetAndElementHandlerComponent implements IsElement<HTMLDivElement> {

   @Inject
   @DataField
   private HTMLDivElement root;

   @EventHandler("input")
   public HTMLDivElement getElement() {
        return root;
   }
   // Handles dblclick events for the element in the
   // template with id/class/data-field="button".
   @EventHandler("button")
   public void onClick(@ForEvent("dblclick") MouseEvent e) {
     // do something
   }

}
```

Note: MouseEvent is a elemental2 class.

## 3.4. Crysknife UI Navigation
Crysknfife offers a system for creating applications that have multiple bookmarkable pages.

### 3.4.1. Declaring a Page

To declare a page, annotate any subclass of IsElement templated component with the @Page annotation:

```java
@Page
@Templated
@Dependent
public class ComponentPage implements IsElement {
  // Anything goes...
}
```

By default, the name of a page is the simple name of the class that declares it. In the above example, ComponentPage will be displayed when the location bar ends with #ComponentPage. If you prefer a different page name, use the @Page annotation’s path attribute:
```java
@Page(path = "my-page")
@Templated
@Dependent
public class ComponentPage implements IsElement {
  // Anything goes...
}
```
### 3.4.2. The Default (Starting) Page
Each application must have exactly one default page. This requirement is enforced at compile time. This default page is displayed when there is no fragment ID present in the browser’s location bar.

Use the role = DefaultPage.class attribute to declare the default starting page, like this:

```java
@Page(role = DefaultPage.class)
@Templated
@ApplicationScoped
public class WelcomePage implements IsElement {
  // Anything goes...
}
```

Pages are looked up as CDI beans, so you can inject other CDI beans into fields or a constructor. Pages can also have @PostConstruct and @PreDestroy CDI methods.

### 3.4.3. Page Roles

DefaultPage is just one example of a page role. A page role is simply an interface used to mark @Page types. The main uses for page roles:

Using the Navigation singleton, you can look up all pages that have a specific role.
If a role is unique (as is the case with DefaultPage) then it should extend UniquePageRole, making it possible to navigate to the page by its role.

### 3.4.4. Page Lifecycle

ere are four annotations related to page lifecycle events: @PageShowing, @PageShown, @PageHiding, and @PageHidden. These annotations designate methods so a page widget can be notified when it is displayed or hidden:

```java
@Page
@Templated
@ApplicationScoped
public class ItemPage implements IsElement {

  @PageShowing
  private void preparePage() {
  }

  @PageHiding
  private void unpreparePage() {
  }
  // Anything goes...
}
```

### 3.4.5. Following a Manual Link

To follow a manual link, simply call the go() method on an injected TransitionTo object. For example:

```java
@Page(role = DefaultPage.class)
@Templated
@ApplicationScoped
public class WelcomePage implements IsElement {

  @Inject TransitionTo<ItemListPage> startButtonClicked;

  public void onStartButtonPressed(ClickEvent e) {
    startButtonClicked.go();
  }

}
```

### 3.4.6. Page State (URL Parameters)

Use the `@PageState` annotation to bind fields in a `@Page` bean to URL query parameters. The navigation framework automatically injects values from the hash fragment before `@PageShowing` is called, and re-injects on `@PageUpdate` when the URL changes while staying on the same page.

#### Supported types

**Scalar types:** `String`, `int`/`Integer`, `long`/`Long`, `boolean`/`Boolean`, `double`/`Double`, `float`/`Float`, `short`/`Short`, `byte`/`Byte`.

**List types** (for multi-valued parameters): `List<String>`, `List<Integer>`, `List<Long>`, `List<Boolean>`, `List<Double>`, `List<Float>`, `List<Short>`, `List<Byte>`.

#### Basic usage

```java
@Page(path = "search")
@Templated
@ApplicationScoped
public class SearchPage implements IsElement<HTMLDivElement> {

    @PageState
    String query;              // #search?query=hello

    @PageState
    int page;                  // #search?page=2

    @PageState
    List<String> tags;         // #search?tags=java&tags=j2cl → ["java", "j2cl"]

    @PageState
    List<Integer> ids;         // #search?ids=1&ids=2&ids=3 → [1, 2, 3]

    @PageShown
    void onShown() {
        // query = "hello", page = 2, tags = ["java", "j2cl"], ids = [1, 2, 3]
    }
}
```

#### Custom parameter names

By default, the query parameter name matches the field name. Use the `value` attribute to map to a different name:

```java
@PageState("q")
String query;  // reads from ?q=...
```

#### Default values

Use `defaultValue` to provide a fallback when the parameter is absent:

```java
@PageState(defaultValue = "1")
int page;  // defaults to 1 when ?page is missing

@PageState(defaultValue = "true")
boolean active;
```

If no default is specified and the parameter is absent, scalar fields are left unmodified and list fields receive an empty list.

**Note:** `defaultValue` is not supported for `List` fields — this is enforced at compile time.

#### Navigating with state

Pass page state through `TransitionTo` using a `Multimap`:

```java
@Inject
TransitionTo<SearchPage> toSearch;

public void search(String query, List<String> tags) {
    Multimap<String, String> state = ImmutableMultimap.of();
    state.put("query", query);
    tags.forEach(tag -> state.put("tags", tag));
    toSearch.go(state);
}
```

### 3.4.7. Page Security

Crysknife integrates Jakarta Security annotations with the navigation system to protect pages based on user roles. Security guards are generated at compile time and check the `SecurityContext` before allowing navigation to a page.

Add the security dependency:

```xml
<dependency>
    <groupId>io.crysknife.ui</groupId>
    <artifactId>crysknife-ui-security-api</artifactId>
    <version>${crysknife.version}</version>
</dependency>
```

#### 3.4.7.1. SecurityContext

`SecurityContext` is an `@ApplicationScoped` bean that holds the current user's identity and roles. Populate it after authentication (e.g., from an OIDC/REST endpoint):

```java
@Inject
SecurityContext securityContext;

void onLoginComplete(String username, String[] roles) {
    securityContext.setUser(new UserImpl(username, roles));
}
```

Key methods:
* `isLoggedIn()` — returns `true` if a non-anonymous user is set
* `isUserInRole(String role)` — checks if the user has a specific role
* `isUserInAllRoles(String... roles)` — checks if the user has all specified roles
* `getUser()` — returns the current `User`
* `setUser(User user)` — sets the authenticated user
* `reset()` — clears the user (logout)
* `addChangeListener(Runnable listener)` — registers a callback invoked whenever `setUser()` is called (useful for reactive UI updates)

#### 3.4.7.2. @RolesAllowed and @DenyAll

Annotate `@Page` classes with Jakarta Security annotations to restrict access:

```java
@Page(path = "admin")
@RolesAllowed("admin")
@Templated
@Singleton
public class AdminPage implements IsElement<HTMLDivElement> {
    // Only accessible to users with the "admin" role
}

@Page(path = "dashboard")
@RolesAllowed({"user", "admin"})
@Templated
@Singleton
public class DashboardPage implements IsElement<HTMLDivElement> {
    // Requires both "user" AND "admin" roles
}

@Page(path = "disabled")
@DenyAll
@Templated
@Singleton
public class DisabledPage implements IsElement<HTMLDivElement> {
    // Always blocked, redirects to SecurityError page
}
```

When a user navigates to a protected page:
1. If not logged in → redirects to the page with `LoginPage` role
2. If logged in but missing required roles → redirects to the page with `SecurityError` role
3. `@DenyAll` → always redirects to `SecurityError`, regardless of authentication

Pages without security annotations (or with `@PermitAll`) are accessible to everyone.

#### 3.4.7.3. LoginPage and SecurityError Roles

You must define pages with `LoginPage` and `SecurityError` roles to handle security redirects:

```java
@Page(role = LoginPage.class)
@Templated
@Singleton
public class MyLoginPage implements IsElement<HTMLDivElement> {
    // Shown when an unauthenticated user tries to access a protected page
}

@Page(role = SecurityError.class)
@Templated
@Singleton
public class AccessDeniedPage implements IsElement<HTMLDivElement> {
    // Shown when a user lacks the required roles
}
```

`LoginPage` and `SecurityError` are `UniquePageRole` markers from `io.crysknife.ui.navigation.client`. Each application using security guards must have exactly one page for each role.

#### 3.4.7.4. @IfRole — Element-Level Security

While `@RolesAllowed` and `@DenyAll` protect entire pages, `@IfRole` provides fine-grained control over individual DOM elements within a page. It hides or shows `@DataField` elements based on the current user's roles.

```java
@Page(path = "home")
@Templated
@Singleton
public class HomePage implements IsElement<HTMLDivElement> {

    @Inject @DataField @IfRole("user")
    HTMLDivElement userSection;

    @Inject @DataField @IfRole("admin")
    HTMLDivElement adminPanel;

    @Inject @DataField @IfRole({"user", "admin"})
    HTMLDivElement superSection;  // requires ALL listed roles
}
```

How it works:
* At compile time, the annotation processor generates a `SecurityContext` role check for each `@IfRole`-annotated `@DataField`
* The element's `style.display` is set to `"none"` when the user lacks the required roles, and restored to `""` when the user has them
* A change listener is registered on `SecurityContext` so visibility updates reactively when the user logs in or out — no page reload required
* Multiple roles use AND semantics (all roles must be present), consistent with `@RolesAllowed`

The `@IfRole` annotation targets fields only:

```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface IfRole {
    String[] value();
}
```

**Why `@IfRole` instead of `@RolesAllowed`?** The Jakarta `@RolesAllowed` annotation can only target `TYPE` and `METHOD`, not `FIELD`. `@IfRole` fills this gap for element-level security in templates.

**Note:** `@IfRole` hides elements via CSS (`display: none`). The element remains in the DOM — this is a UI convenience, not a security boundary. Sensitive data should still be protected server-side.

## 3.5. Internationalization (i18n)

Crysknife provides compile-time internationalization using Closure Compiler's `goog.getMsg()` and XTB translation files. Translations are resolved at compile time — no runtime lookups or Map-based dictionaries. Each locale produces a separate JS bundle with all strings baked in.

### 3.5.1. Defining a Translation Bundle

Create a Java interface annotated with `@TranslationBundle`. Each method represents a translatable message, annotated with `@TranslationKey`:

```java
@TranslationBundle
public interface AppMessages {

  @TranslationKey(defaultValue = "Welcome")
  String welcome();

  @TranslationKey(defaultValue = "Save")
  String save();

  @TranslationKey(defaultValue = "Hello {$name}, you have {$count} messages")
  String greeting(String name, String count);

  @TranslationKey(defaultValue = "Delete", key = "btn_delete")
  String deleteButton();
}
```

- `defaultValue` — the default (English) text returned when no locale is active
- `key` — optional custom message key (defaults to `BundleName.methodName`)
- Parameterized messages use `{$paramName}` placeholders matching method parameter names

### 3.5.2. Providing Translations

Place a `.properties` file alongside the interface with the locale suffix:

```
# AppMessages_ru.properties
welcome=Добро пожаловать
save=Сохранить
greeting=Привет {$name}, у тебя {$count} сообщений
btn_delete=Удалить
```

The annotation processor generates:
- `AppMessagesImpl.java` — a Java stub implementing the interface
- `AppMessagesImpl.native.js` — JavaScript with `goog.getMsg()` calls
- `gwt3_message_bundle_ru.xtb` — XTB file for Closure Compiler

### 3.5.3. Injecting a Bundle

Inject the bundle interface via CDI:

```java
@Inject
AppMessages messages;
```

Call methods to get translated strings:

```java
String text = messages.welcome(); // "Welcome" or "Добро пожаловать"
String greeting = messages.greeting("World", "5");
```

### 3.5.4. Template Translation with data-i18n-key

In `@Templated` HTML, use the `data-i18n-key` attribute to translate element text content at initialization. The element must also have a `data-field` or `id` attribute:

```html
<div>
    <span data-field="welcomeLabel" data-i18n-key="AppMessages.welcome">placeholder</span>
    <span data-field="saveBtn" data-i18n-key="AppMessages.save">placeholder</span>
    <span data-field="deleteBtn" data-i18n-key="AppMessages.deleteButton">placeholder</span>
</div>
```

The format is `BundleName.methodName`. At component initialization, the generated code sets each element's `textContent` to the translated value. Only zero-parameter methods are supported in `data-i18n-key`.

The corresponding component only needs `@DataField` declarations:

```java
@Singleton
@Templated("MyComponent.html")
public class MyComponent implements IsElement<HTMLDivElement> {

  @DataField
  HTMLElement welcomeLabel;

  @DataField
  HTMLElement saveBtn;

  @DataField
  HTMLElement deleteBtn;
}
```

### 3.5.5. Building with a Locale

Each J2CL build targets one locale. Configure the locale and XTB auto-discovery in the j2cl-maven-plugin:

```xml
<plugin>
    <groupId>org.kie.j2cl.tools</groupId>
    <artifactId>j2cl-maven-plugin</artifactId>
    <configuration>
        <defines>
            <goog.LOCALE>ru</goog.LOCALE>
        </defines>
        <translationsFile>
            <auto>true</auto>
        </translationsFile>
    </configuration>
</plugin>
```

Without `goog.LOCALE`, `goog.getMsg()` returns the `defaultValue` strings (English). With a locale and matching XTB file, Closure Compiler substitutes the translated text at compile time.

## 3.6. Data Binding

Crysknife provides a two-way data binding system that automatically synchronizes model properties with UI elements. When a user types into a bound input field, the model updates automatically. When the model changes programmatically, the UI reflects the new values. Inspired by Errai Data Binding.

### 3.6.1. @Bindable Models

To make a model class eligible for data binding, annotate it with `@Bindable`. The annotation processor generates a proxy subclass that intercepts setter calls and fires property change events.

```java
@Bindable
@Dependent
public class Contact {
    private String name = "";
    private String email = "";
    private boolean active = true;
    private Address address = new Address();
    private List<String> tags = new ArrayList<>();

    // Standard getters and setters required
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public Address getAddress() { return address; }
    public void setAddress(Address address) { this.address = address; }

    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }
}
```

Requirements for `@Bindable` classes:
* Must not be `final`
* Must have a default (no-arg) constructor
* Setters must not be `final` or `private`
* Properties follow JavaBean convention (getter/setter pairs)

### 3.6.2. @Bound Fields

Use the `@Bound` annotation on `@DataField` elements in a `@Templated` bean to bind them to model properties. The `DataBinder` is injected and manages the binding lifecycle.

```java
@Singleton
@Templated("contact-form.html")
public class ContactForm implements IsElement<HTMLDivElement> {

    @Inject
    @DataField
    HTMLDivElement root;

    @Inject
    DataBinder<Contact> binder;

    @Inject
    @DataField
    @Bound(property = "name")
    HTMLInputElement nameInput;

    @Inject
    @DataField
    @Bound(property = "email")
    HTMLInputElement emailInput;

    @Inject
    @DataField
    @Bound
    HTMLInputElement active;  // property name defaults to field name "active"

    @EventHandler("saveBtn")
    private void onSave(@ForEvent("click") MouseEvent event) {
        Contact model = binder.getModel();
        // model.getName() already contains the current input value
    }

    @Override
    public HTMLDivElement getElement() { return root; }
}
```

When `property` is not specified in `@Bound`, the field name is used as the property name.

### 3.6.3. Nested Bean Properties (Dot-Notation)

Bind to properties of nested beans using dot-notation. The nested bean must also be annotated with `@Bindable`:

```java
@Bindable
@Dependent
public class Address {
    private String city = "";
    private String zip = "";
    // getters and setters
}
```

```java
@Inject
@DataField
@Bound(property = "address.city")
HTMLInputElement cityInput;

@Inject
@DataField
@Bound(property = "address.zip")
HTMLInputElement zipInput;
```

Changes to `cityInput` will call `getModel().getAddress().setCity(...)` automatically, and calling `getModel().getAddress().setCity("Boston")` will update `cityInput`.

### 3.6.4. Programmatic Binding with DataBinder

In addition to declarative `@Bound` annotations, you can bind elements programmatically:

```java
@Inject
DataBinder<Contact> binder;

@Inject
@DataField
HTMLInputElement phoneInput;

@PostConstruct
public void init() {
    binder.bind(phoneInput, "phone");
}
```

Key `DataBinder` methods:

```java
// Set or replace the model — all bound UI elements update immediately
binder.setModel(contact);

// Get the current (proxied) model — reflects latest UI input values
Contact model = binder.getModel();

// Bind / unbind programmatically
binder.bind(element, "propertyName");
binder.unbind("propertyName");
binder.unbind();  // unbind all
```

### 3.6.5. Property Change Handlers

Listen for property changes on the model. Handlers fire whenever a bound property changes, whether from user input or programmatic updates:

```java
// Listen to all properties
binder.addPropertyChangeHandler(event -> {
    System.out.println(event.getPropertyName() + ": "
        + event.getOldValue() + " -> " + event.getNewValue());
});

// Listen to a specific property
PropertyChangeUnsubscribeHandle handle =
    binder.addPropertyChangeHandler("name", event -> {
        validateName(event.getNewValue());
    });

// Unsubscribe when no longer needed
handle.unsubscribe();
```

### 3.6.6. Collection Binding with ListComponent

`ListComponent<M>` automatically renders list items as child DOM elements. When bound to an `ObservableList` property, it re-renders whenever items are added or removed.

Basic usage with default renderer (uses `toString()`):

```java
@DataField
@Bound(property = "tags")
ListComponent<String> tagsList = ListComponent.simple("ul", "li");
```

Custom renderer:

```java
@DataField
@Bound(property = "tags")
ListComponent<String> tagsList = ListComponent.create("ul", "li",
    (tag, el) -> {
        el.textContent = tag;
        el.classList.add("badge");
    });
```

The `ListComponent` implements `IsElement` so it integrates with `@DataField` as a template element replacement. No `@Inject` is needed — the field initializer creates the component.

Mutations on the list are tracked automatically via `ObservableList`:

```java
// These mutations trigger automatic re-rendering
binder.getModel().getTags().add("new-tag");
binder.getModel().getTags().remove(0);
```

### 3.6.7. Converters

Use a `Converter` to transform values between the model and the UI widget. For example, converting between an integer model property and the string value of an input element:

```java
public class IntegerConverter implements Converter<Integer, String> {
    @Override
    public Integer toModelValue(String widgetValue) {
        return widgetValue.isEmpty() ? 0 : Integer.parseInt(widgetValue);
    }

    @Override
    public String toWidgetValue(Integer modelValue) {
        return String.valueOf(modelValue);
    }
}
```

Apply via annotation:

```java
@Inject
@DataField
@Bound(property = "age", converter = IntegerConverter.class)
HTMLInputElement ageInput;
```

Or programmatically:

```java
binder.bind(ageInput, "age", new IntegerConverter());
```

### 3.6.8. Limitations

* `@Bindable` classes must not be `final`
* Setters must not be `final` or `private`
* Array-typed properties are not supported — use `List` instead
* `ListComponent` re-renders the full list on each mutation (no fine-grained DOM diffing)
* Circular references in `@Bindable` models are not supported

## 3.7. REST Client

Crysknife provides a type-safe REST client built on top of jakarta4g-rest. You define a JAX-RS service interface, and Crysknife generates a `Caller<T>` implementation at compile time. The `Caller` uses `XMLHttpRequest` under the hood and integrates with JSON Mapper for automatic serialization/deserialization.

### 3.7.1. Dependencies

Add the following dependencies to your `pom.xml`:

```xml
<!-- REST client runtime (transitively includes jakarta4g-rest API and JSON Mapper) -->
<dependency>
    <groupId>io.crysknife.ui</groupId>
    <artifactId>crysknife-ui-rest-api</artifactId>
    <version>${crysknife.version}</version>
</dependency>

<!-- JAX-RS annotations (J2CL-compatible) -->
<dependency>
    <groupId>org.treblereel.gwt.jakarta</groupId>
    <artifactId>jax-rs</artifactId>
    <version>0.8</version>
</dependency>
```

Add the generator to your annotation processor paths in `maven-compiler-plugin`. It transitively pulls in the `jakarta4g-rest` and `json-mapper` annotation processors:

```xml
<annotationProcessorPaths>
    <!-- ... other crysknife processors ... -->
    <path>
        <groupId>io.crysknife.ui</groupId>
        <artifactId>crysknife-ui-rest-generator</artifactId>
        <version>${crysknife.version}</version>
    </path>
</annotationProcessorPaths>
```

### 3.7.2. Defining a JAX-RS Service Interface

Define a standard JAX-RS interface. The annotation processor generates a `_RestCaller` implementation and a JSON mapper for each model class used in the interface.

```java
@Path("/posts")
public interface PostService {

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    Post getPost(@PathParam("id") long id);

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    List<Post> listPosts();
}
```

Supported JAX-RS annotations: `@Path`, `@GET`, `@POST`, `@PUT`, `@DELETE`, `@PathParam`, `@QueryParam`, `@Produces`, `@Consumes`.

### 3.7.3. Configuring the REST Endpoint

Use a CDI `@Produces` method to provide a `RestConfig` bean that specifies the base URL for your REST service:

```java
@ApplicationScoped
public class MyRestConfig {

    @Produces
    @Named("myapi")
    public RestConfig myApiConfig() {
        return RestConfig.builder()
                .baseUrl("https://api.example.com")
                .build();
    }
}
```

If no `@Named` qualifier is specified and no custom `RestConfig` producer exists, the default `RestConfig` uses the current domain as the base URL (suitable for same-origin APIs).

### 3.7.4. Injecting and Using the Caller

Inject `Caller<T>` where `T` is your JAX-RS service interface. Use the `@Named` qualifier to match the `RestConfig` producer:

```java
@Inject
@Named("myapi")
Caller<PostService> postServiceCaller;

public void loadPost() {
    postServiceCaller
        .onError((response, throwable) -> {
            // handle error
        })
        .call(response -> {
            Post post = (Post) response;
            // use the post
        })
        .getPost(1);
}
```

The `Caller` API is fluent:
* `.onError(callback)` — register an error handler (optional)
* `.call(callback)` — register a success callback and return the service proxy
* Then call any method on the service proxy — this triggers the actual HTTP request

### 3.7.5. Using Qualifiers for Multiple Endpoints

You can define multiple `RestConfig` producers with different qualifiers to talk to different APIs:

```java
@ApplicationScoped
public class RestConfigs {

    @Produces
    @Named("internal")
    public RestConfig internalApi() {
        return RestConfig.builder()
                .baseUrl("/api")
                .build();
    }

    @Produces
    @Named("external")
    public RestConfig externalApi() {
        return RestConfig.builder()
                .baseUrl("https://external-service.com")
                .build();
    }
}
```

Then inject with the matching qualifier:

```java
@Inject
@Named("internal")
Caller<ItemService> itemCaller;

@Inject
@Named("external")
Caller<PostService> postCaller;
```

You can also use custom `@Qualifier` annotations instead of `@Named`:

```java
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
public @interface ExternalApi {}
```

```java
@Produces
@ExternalApi
public RestConfig externalApiConfig() {
    return RestConfig.builder()
            .baseUrl("https://external-service.com")
            .build();
}

@Inject
@ExternalApi
Caller<PostService> externalPostCaller;
```

## 3.8. WebSocket Client

Crysknife provides a CDI-managed WebSocket client built on top of Jakarta WebSocket annotations. You define a `@ClientEndpoint` bean with `@OnOpen`, `@OnMessage`, `@OnClose`, and `@OnError` lifecycle methods, and Crysknife generates the WebSocket proxy at compile time. Connection is managed via a type-safe `WebSocketConnector<T>` injectable.

### 3.8.1. Dependencies

Add the following dependencies to your `pom.xml`:

```xml
<!-- WebSocket CDI API (WebSocketConnector) -->
<dependency>
    <groupId>io.crysknife.ui</groupId>
    <artifactId>crysknife-ui-websocket-api</artifactId>
    <version>${crysknife.version}</version>
</dependency>

<!-- Jakarta WebSocket client runtime (J2CL-compatible) -->
<dependency>
    <groupId>org.treblereel.gwt.jakarta.websocket</groupId>
    <artifactId>common</artifactId>
    <version>0.1</version>
</dependency>

<!-- WebSocket code generator (compile-time only) -->
<dependency>
    <groupId>io.crysknife.ui</groupId>
    <artifactId>crysknife-ui-websocket-generator</artifactId>
    <version>${crysknife.version}</version>
    <scope>provided</scope>
</dependency>
```

### 3.8.2. Defining a Client Endpoint

Annotate a CDI bean with `@ClientEndpoint` and add lifecycle methods:

```java
@ClientEndpoint
@Singleton
public class ChatEndpoint {

    @Inject
    NotificationService notifications;

    @OnOpen
    public void onOpen(Session session) {
        notifications.show("Connected");
    }

    @OnMessage
    public void onMessage(String message) {
        notifications.show(message);
    }

    @OnClose
    public void onClose(CloseReason reason) {
        notifications.show("Disconnected: " + reason.getReasonPhrase());
    }

    @OnError
    public void onError(Throwable error) {
        notifications.show("Error: " + error.getMessage());
    }
}
```

The endpoint is a regular CDI bean — you can use `@Inject`, `@PostConstruct`, and any scope (`@Singleton`, `@Dependent`, `@ApplicationScoped`).

### 3.8.3. Connecting

Inject `WebSocketConnector<T>` where `T` is your `@ClientEndpoint` bean. Call `baseUri()` to set the WebSocket URL and `connect()` to initiate the connection:

```java
@ApplicationScoped
public class ChatPanel {

    @Inject
    WebSocketConnector<ChatEndpoint> connector;

    public void connect() {
        connector.baseUri("ws://localhost:8080/chat").connect();
    }
}
```

`connect()` is fire-and-forget — it initiates the connection asynchronously. The `jakarta.websocket.Session` is delivered to your `@OnOpen` method, not returned from `connect()`. This matches the browser's async WebSocket model.

### 3.8.4. Sending Messages via Session

Use the `Session` received in `@OnOpen` to send messages:

```java
@ClientEndpoint
@Singleton
public class ChatEndpoint {

    private Session session;

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        session.getBasicRemote().sendText("hello from client");
    }

    public void send(String message) {
        if (session != null) {
            session.getBasicRemote().sendText(message);
        }
    }
}
```

### 3.8.5. Dual @OnMessage — Text and Binary

An endpoint can have two `@OnMessage` methods — one for text (`String`) and one for binary (`byte[]`):

```java
@ClientEndpoint
@Singleton
public class DataEndpoint {

    @OnMessage
    public void onText(String message, Session session) {
        // handle text message
    }

    @OnMessage
    public void onBinary(byte[] data, Session session) {
        // handle binary message
    }
}
```

The generator classifies each method by parameter type and overrides `handleTextMessage` and `handleBinaryMessage` independently.

### 3.8.6. Lifecycle Callbacks

All callback parameters are optional and order-independent. The generator matches by type:

| Annotation | Recognized parameter types |
|------------|--------------------------|
| `@OnOpen` | `Session` |
| `@OnMessage` | `String` or `byte[]`, `Session` |
| `@OnClose` | `CloseReason`, `Session` |
| `@OnError` | `Throwable` (or subclasses), `Session` |

Examples:

```java
@OnOpen
public void onOpen() { }                      // no parameters — valid

@OnOpen
public void onOpen(Session session) { }        // Session only — valid

@OnMessage
public void onMessage(String msg) { }          // payload only — valid

@OnMessage
public void onMessage(String msg, Session s) { } // payload + session — valid

@OnClose
public void onClose(CloseReason reason) { }    // reason only — valid

@OnError
public void onError(Throwable error) { }       // error only — valid
```

### 3.8.7. Limitations

* `connect()` returns `void` — session is delivered asynchronously via `@OnOpen`
* No automatic reconnection (must be implemented manually)
* No Encoder/Decoder framework support
* No `@PathParam` URI template variables
* No partial message handling
* No Ping/Pong (browser WebSocket API limitation)
* At most one text `@OnMessage` and one binary `@OnMessage` per endpoint
* `@ClientEndpoint` subprotocols attribute is not processed (use `WebSocketConnector.subprotocol()` instead)

# 4. Contributing to Crysknife

Crysknife is an open source project and contributions are welcome!

# 5. Crysknife License
Crysknife is distributed under the terms of the Apache License, Version 2.0. See the full Apache license text.

# crysknife

This project is a proof of concept for a CDI-like Dependency Injection framework with a support of some popular features. It’s based on annotation processors code generation and has no GWT2 dependencies.

Demo: https://crysknife.cloud.unispace.io

At this point Crysknife supports:

* @Singleton and @Dependent scopes
* lazy fields and constructor injections
* @PostConstruct
* transitive injections
* @Named qualifiers and @Produces for custom objects like Elemental2 widgets
* HTML templates
* Data binding

Demo shows how Crysknife is used on a simple page compiled with J2CL (with j2cl-maven-plugin).


Please, remember that this is a POC, nothing more at this moment.
Feel free to comment and criticize.

How to build:
1. 'git clone git@github.com:treblereel/crysknife.git'
2. 'mvn clean install'
3. To run demo, run 'mvn clean j2cl:watch' within crysknife/demo folder</br>
4. Open another console and run 'mvn tomcat7:run'
5. Open in browser: http://127.0.0.1:8080/demo
5. To build a .war, run 'mvn clean package -Pbuild' within crysknife/demo folder


Have fun and let J2CL win !

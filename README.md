# crysknife<br/>
This project is a proof of concept for Dependency injection framework CDI (LIKE??) with a support of popular features. At this point it supports @Singleton and @Dependent scopes, fields and constractor injections, @PostContract calls, transitive injections, @Named qualifiers and @Produces for custom objects like elemental2 widgets and GWT.create if needed. It’s based on annotation processors generation and have no gwt 2.8 dependencies. Demo shows usage of a simple page compiled with j2cl (with j2clmavenplugin).


Please, remember that it's a POC, nothing more at this moment.</br>
Feel free to comment and criticize.

How to build
1. 'git clone git@github.com:treblereel/crysknife.git'
2. 'mvn clean install'
3. to run demo run 'mvn clean j2cl:watch' within crysknife/demo folder</br>
4. 
   1 open another console and run 'mvn tomcat7:run'
   2 open browser http://127.0.0.1:8080/demo
5. to build a .war run 'mvn clean package -Pbuild' within crysknife/demo folder


Have fun and let j2cl win !

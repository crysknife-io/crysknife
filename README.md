# crysknife<br/>
This project is a proof of concept for Dependency injection framework CDI (LIKE??) with a support of popular features. At this point it supports @Singleton and @Dependent scopes, fields and constractor injections, @PostContract calls, transitive injections, @Named qualifiers and @Produces for custom objects like elemental2 widgets and GWT.create if needed. It’s based on annotation processors generation and have no gwt 2.8 dependencies. Demo shows usage of a simple page compiled with j2cl (with j2clmavenplugin by @gitgabrio).


Please, remember that it's a POC, nothing more at this moment.</br>
Feel free to comment and criticize.

How to build
1. 'git clone git@github.com:treblereel/crysknife.git'
2. 'mvn clean install'
3. choose another folder and 'git clone https://github.com/gitgabrio/j2clmavenplugin.git'
4. 'git checkout origin/handle-dependencies'
5. 'mvn clean install'
6. to run demo run 'mvn install -Pdevmode' within crysknife/demo folder
7. to build a .war run 'mvn clean package -Pbuild' within crysknife/demo folder


Have fun and let j2cl win !

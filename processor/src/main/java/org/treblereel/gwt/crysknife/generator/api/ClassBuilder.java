package org.treblereel.gwt.crysknife.generator.api;

import javax.inject.Provider;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import org.treblereel.gwt.crysknife.generator.definition.BeanDefinition;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 3/3/19
 */
public class ClassBuilder {

    public final BeanDefinition beanDefinition;

    private CompilationUnit clazz = new CompilationUnit();

    private ClassOrInterfaceDeclaration classDeclaration;

    private ConstructorDeclaration constructorDeclaration;

    private MethodDeclaration getMethodDeclaration;

    private FieldBuilder fields = new FieldBuilder(this);

    private MethodBuilder methods = new MethodBuilder(this);

    private ConstructorBuilder constructor = new ConstructorBuilder(this);

    private InstanceConstructorBuilder instanceConstructorBuilder = new InstanceConstructorBuilder(this);

    private InitBuilder initBuilder = new InitBuilder(this);

    public ClassBuilder(BeanDefinition beanDefinition) {
        this.beanDefinition = beanDefinition;
    }

    public String build() {
        initClassBuilder();

        fields.build();

        constructor.build();
        instanceConstructorBuilder.build();

        initBuilder.build();

        beanDefinition.generate(this);
        methods.build();
        beanDefinition.finish(this);

        return clazz.toString();
    }

    private void initClassBuilder() {
        this.clazz.setPackageDeclaration(beanDefinition.getPackageName());
        this.clazz.addImport("org.treblereel.gwt.crysknife.client.internal.Factory");
        this.clazz.addImport(Provider.class);
        this.clazz.addImport(beanDefinition.getQualifiedName());
        this.classDeclaration = clazz.addClass(beanDefinition.getClassFactoryName());

        ClassOrInterfaceType factory = new ClassOrInterfaceType();
        factory.setName("Factory<" + beanDefinition.getClassName() + ">");
        classDeclaration.getImplementedTypes().add(factory);
    }

    public ClassOrInterfaceDeclaration getClassDeclaration() {
        return classDeclaration;
    }

    public MethodDeclaration getGetMethodDeclaration() {
        return getMethodDeclaration;
    }

    void setGetMethodDeclaration(MethodDeclaration getMethodDeclaration) {
        this.getMethodDeclaration = getMethodDeclaration;
    }
}

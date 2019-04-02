package org.treblereel.gwt.crysknife.generator.api;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import org.treblereel.gwt.crysknife.generator.definition.BeanDefinition;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 3/3/19
 */
public class ClassBuilder {

    public final BeanDefinition beanDefinition;

    private CompilationUnit clazz = new CompilationUnit();

    private ClassOrInterfaceDeclaration classDeclaration;

    private MethodDeclaration getGetMethodDeclaration;

    private ConstructorDeclaration constructorDeclaration;

    public ClassBuilder(BeanDefinition beanDefinition) {
        this.beanDefinition = beanDefinition;
    }

    public void build() {
        beanDefinition.generate(this);
    }

    public String toSourceCode() {
        return clazz.toString();
    }

    public CompilationUnit getClassCompilationUnit() {
        return clazz;
    }

    public ClassOrInterfaceDeclaration getClassDeclaration() {
        return classDeclaration;
    }

    public void setClassName(String className) {
        this.classDeclaration = clazz.addClass(className);
    }

    public MethodDeclaration getGetMethodDeclaration() {
        return getGetMethodDeclaration;
    }

    public void setGetMethodDeclaration(MethodDeclaration getMethodDeclaration) {
        this.getGetMethodDeclaration = getMethodDeclaration;
    }

    public ConstructorDeclaration getConstructorDeclaration() {
        return constructorDeclaration;
    }

    public void addConstructorDeclaration(Modifier.Keyword... modifiers) {
        this.constructorDeclaration = classDeclaration.addConstructor(modifiers);
    }
}

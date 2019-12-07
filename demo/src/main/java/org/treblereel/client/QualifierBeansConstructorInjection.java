package org.treblereel.client;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.treblereel.client.qualifiers.QualifierBean;
import org.treblereel.client.qualifiers.QualifierOne;
import org.treblereel.client.qualifiers.QualifierTwo;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 4/13/19
 */
@Singleton
public class QualifierBeansConstructorInjection {

    private QualifierBean one;

    private QualifierBean two;

    @Inject
    public QualifierBeansConstructorInjection(@QualifierOne QualifierBean one,
                                              @QualifierTwo QualifierBean two) {

        this.one = one;
        this.two = two;
    }

    @PostConstruct
    void init() {
        one.say();
        two.say();
    }
}

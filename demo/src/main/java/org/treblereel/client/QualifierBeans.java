package org.treblereel.client;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Default;
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
public class QualifierBeans {

    @Inject
    @QualifierOne
    private QualifierBean one;

    @Inject
    @QualifierTwo
    private QualifierBean two;

    @Inject
    @Default
    private QualifierBean three;

    @PostConstruct
    void init() {
        one.say();
        two.say();
    }
}

package org.treblereel.client.view.impl;

import javax.inject.Singleton;

import org.treblereel.client.view.SimpleBean;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 6/18/19
 */
//@Singleton
public class SimpleBeanImpl implements SimpleBean {

    @Override
    public String say() {
        return "SimpleBeanImpl";
    }
}

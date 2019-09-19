package org.treblereel.client.inject.cyclic;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 6/20/19
 */
@Singleton
public class BeanThree {


    @Inject
    BeanFour beanFour;
}

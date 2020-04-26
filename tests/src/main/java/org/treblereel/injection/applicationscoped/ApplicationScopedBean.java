package org.treblereel.injection.applicationscoped;

import java.util.Random;

import javax.enterprise.context.ApplicationScoped;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 5/21/19
 */
@ApplicationScoped
public class ApplicationScopedBean {

    private int unique = 0;

    ApplicationScopedBean() {
        this.unique = new Random().nextInt();
    }

    public int say() {
        return this.unique;
    }
}

package org.treblereel;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.treblereel.gwt.crysknife.client.Application;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 3/21/20
 */
@Application
public class App {

    private String testPostConstruct;

    @Inject
    private SimpleBean simpleBean;


    public void onModuleLoad() {
        new AppBootstrap(this).initialize();
    }

    @PostConstruct
    public void init(){
        this.testPostConstruct = "PostConstruct";
    }

    public String getTestPostConstruct() {
        return testPostConstruct;
    }

    public SimpleBean getSimpleBean() {
        return simpleBean;
    }
}

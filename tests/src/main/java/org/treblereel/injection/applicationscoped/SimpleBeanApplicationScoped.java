package org.treblereel.injection.applicationscoped;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 3/21/20
 */
@ApplicationScoped
public class SimpleBeanApplicationScoped {

  private String postConstruct;

  public String getName() {
    return this.getClass().getSimpleName();
  }

  @PostConstruct
  public void init() {
    postConstruct = "done";
  }

  public String getPostConstruct() {
    return postConstruct;
  }
}

package org.treblereel.injection.dependent;

import java.util.Random;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 4/26/20
 */
@Dependent
public class SimpleBeanDependent {

  private String postConstruct;

  private int random;

  public String getName() {
    return this.getClass().getSimpleName();
  }

  @PostConstruct
  public void init() {
    postConstruct = "done";
    random = new Random().nextInt();
  }

  public String getPostConstruct() {
    return postConstruct;
  }

  public int getRandom() {
    return random;
  }
}

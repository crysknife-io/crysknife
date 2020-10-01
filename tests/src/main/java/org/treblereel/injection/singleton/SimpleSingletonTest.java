package org.treblereel.injection.singleton;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 4/26/20
 */
@Singleton
public class SimpleSingletonTest {

  @Inject
  private SingletonBean fieldOne;
  @Inject
  private SingletonBean fieldTwo;
  private SingletonBean constrOne;
  private SingletonBean constrTwo;

  @Inject
  public SimpleSingletonTest(SingletonBean constrOne, SingletonBean constrTwo) {
    this.constrOne = constrOne;
    this.constrTwo = constrTwo;
  }

  public SingletonBean getFieldOne() {
    return fieldOne;
  }

  public SingletonBean getFieldTwo() {
    return fieldTwo;
  }

  public SingletonBean getConstrOne() {
    return constrOne;
  }

  public SingletonBean getConstrTwo() {
    return constrTwo;
  }
}

package org.treblereel.injection.dependent;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 4/26/20
 */
@Singleton
public class SimpleDependentTest {

  @Inject
  private SimpleBeanDependent fieldOne;
  @Inject
  private SimpleBeanDependent fieldTwo;
  private SimpleBeanDependent constrOne;
  private SimpleBeanDependent constrTwo;

  @Inject
  public SimpleDependentTest(SimpleBeanDependent constrOne, SimpleBeanDependent constrTwo) {
    this.constrOne = constrOne;
    this.constrTwo = constrTwo;
  }

  public SimpleBeanDependent getFieldOne() {
    return fieldOne;
  }

  public SimpleBeanDependent getFieldTwo() {
    return fieldTwo;
  }

  public SimpleBeanDependent getConstrOne() {
    return constrOne;
  }

  public SimpleBeanDependent getConstrTwo() {
    return constrTwo;
  }
}

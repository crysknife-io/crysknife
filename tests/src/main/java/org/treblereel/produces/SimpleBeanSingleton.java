package org.treblereel.produces;

import java.util.Objects;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 4/26/20
 */
public class SimpleBeanSingleton {

  private String foo;
  private int bar;
  private int staticValue;

  @Override
  public int hashCode() {
    return Objects.hash(getFoo(), getBar(), getStaticValue());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof SimpleBeanSingleton)) {
      return false;
    }
    SimpleBeanSingleton that = (SimpleBeanSingleton) o;
    return getBar() == that.getBar() && getStaticValue() == that.getStaticValue()
        && Objects.equals(getFoo(), that.getFoo());
  }

  public int getBar() {
    return bar;
  }

  public void setBar(int bar) {
    this.bar = bar;
  }

  public int getStaticValue() {
    return staticValue;
  }

  public String getFoo() {
    return foo;
  }

  public void setFoo(String foo) {
    this.foo = foo;
  }

  public void setStaticValue(int staticValue) {
    this.staticValue = staticValue;
  }
}

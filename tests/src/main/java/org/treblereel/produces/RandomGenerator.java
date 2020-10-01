package org.treblereel.produces;

import java.util.Random;

import javax.inject.Singleton;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 4/26/20
 */
@Singleton
public class RandomGenerator {

  private int random;

  public void init() {
    random = new Random().nextInt();
  }

  public int getRandom() {
    return random;
  }
}

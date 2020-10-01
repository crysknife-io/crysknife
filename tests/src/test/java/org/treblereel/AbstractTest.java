package org.treblereel;

import org.junit.Before;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 4/26/20
 */
public class AbstractTest {

  protected App app = new App();

  @Before
  public void init() {
    new AppBootstrap(app).initialize();
  }

}

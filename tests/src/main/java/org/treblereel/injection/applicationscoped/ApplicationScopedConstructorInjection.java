package org.treblereel.injection.applicationscoped;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 5/21/19
 */
@ApplicationScoped
public class ApplicationScopedConstructorInjection {

  public ApplicationScopedBean bean;

  // @Inject
  public ApplicationScopedBean bean2;

  @Inject
  public ApplicationScopedConstructorInjection(ApplicationScopedBean bean) {
    this.bean = bean;
  }

}

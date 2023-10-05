/*
 * Copyright © 2021 Treblereel
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package org.treblereel.providers.provider;

import io.crysknife.client.ioc.IOCProvider;

import jakarta.inject.Provider;
import jakarta.inject.Singleton;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 12/4/21
 */
@Singleton
@IOCProvider
public class SimpleIOCProvider implements Provider<IOCProviderBean> {

  private IOCProviderBean iocProviderBean;


  @Override
  public IOCProviderBean get() {
    if(iocProviderBean != null) {
      return iocProviderBean;
    }

    iocProviderBean = new IOCProviderBean();
    return iocProviderBean;
  }
}

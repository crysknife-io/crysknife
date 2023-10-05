/*
 * Copyright © 2023 Treblereel
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


package org.treblereel.injection.any;


import io.crysknife.client.ManagedInstance;
import jakarta.enterprise.inject.Any;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
public class AnyTestHolder {


    @Any
    @Inject
    public ManagedInstance<YetAnotherInterface> managedInstance;

    @Any
    @Inject
    public Instance<YetAnotherInterface> instanceHolder;

    public ManagedInstance<YetAnotherInterface> managedInstanceQualifier;

    public Instance<YetAnotherInterface> instanceHolderQualifier;

    @Inject
    public AnyTestHolder(@Any @YetAnotherQualifier ManagedInstance<YetAnotherInterface> managedInstance, @Any @YetAnotherQualifier Instance<YetAnotherInterface> instanceHolder) {
        this.managedInstanceQualifier = managedInstance;
        this.instanceHolderQualifier = instanceHolder;
    }

}

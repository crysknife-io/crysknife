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

package org.treblereel.produces.named;

import io.crysknife.client.internal.QualifierUtil;
import jakarta.enterprise.inject.Default;
import jakarta.inject.Named;
import org.junit.Test;
import org.treblereel.AbstractTest;

import java.lang.annotation.Annotation;

import static org.junit.Assert.assertEquals;

public class ProducesNamedBeanHolderTest extends AbstractTest {

    private final Named namedBeanOne = new Named() {

        public Class<? extends Annotation> annotationType() {
            return jakarta.inject.Named.class;
        }

        public String value() {
            return "nameOne";
        }
    };


    @Test
    public void testBean() {
        assertEquals("ParentBean",
                app.beanManager.lookupBean(ParentBean.class).getInstance().getType());
        assertEquals("ParentBean",
                app.beanManager.lookupBean(ProducesNamedBeanHolder.class).getInstance().getParentBean().getType());
        assertEquals("ParentBean",
                app.beanManager.lookupBean(ProducesNamedBeanHolder.class).getInstance().getParentBeanConstructor().getType());
    }

    @Test
    public void testBeanDefault() {
        assertEquals("ParentBean",
                app.beanManager.lookupBean(ParentBean.class, QualifierUtil.DEFAULT_ANNOTATION).getInstance().getType());
        assertEquals("ParentBean",
                app.beanManager.lookupBean(ProducesNamedBeanHolder.class).getInstance().getParentBeanDefault().getType());
        assertEquals("ParentBean",
                app.beanManager.lookupBean(ProducesNamedBeanHolder.class).getInstance().getParentBeanDefaultConstructor().getType());
    }

    @Test
    public void testBeanNamedOne() {
        assertEquals("ParentBeanNamedOne",
                app.beanManager.lookupBean(ParentBean.class, namedBeanOne).getInstance().getType());
        assertEquals("ParentBeanNamedOne",
                app.beanManager.lookupBean(ProducesNamedBeanHolder.class).getInstance().getParentBeanNamedOne().getType());
        assertEquals("ParentBeanNamedOne",
                app.beanManager.lookupBean(ProducesNamedBeanHolder.class).getInstance().getParentBeanNamedOneConstructor().getType());

    }
}

/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.crysknife.demo.client.databinding.listcomponent;

import io.crysknife.client.IsElement;
import io.crysknife.ui.databinding.client.api.HasModel;
import org.gwtproject.user.client.TakesValue;

public interface RolesListItemWidgetView extends TakesValue<KeyValueRow>,
        HasModel<KeyValueRow>,
        IsElement {

    void init();

    void setParentWidget(final RolesEditorWidgetView parentWidget);

    void notifyModelChanged();

    boolean isDuplicateName(final String name);

    void setReadOnly(final boolean readOnly);
}
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

import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLTableRowElement;
import elemental2.dom.NotificationEvent;
import io.crysknife.client.IsElement;
import io.crysknife.ui.databinding.client.api.AutoBound;
import io.crysknife.ui.databinding.client.api.Bound;
import io.crysknife.ui.databinding.client.api.DataBinder;
import io.crysknife.ui.templates.client.annotation.DataField;
import io.crysknife.ui.templates.client.annotation.Templated;
import org.gwtproject.user.client.ui.Button;
import org.gwtproject.user.client.ui.TextBox;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import java.util.Objects;
import java.util.Optional;

@Templated("RolesEditorWidget.html#tableRow")
@Dependent
public class RolesListItemWidgetViewImpl implements RolesListItemWidgetView,
        IsElement {

    public static final String INVALID_CHARACTERS_MESSAGE = "Invalid characters";
    private static final String DUPLICATE_NAME_ERROR_MESSAGE = "A role with this name already exists";
    private static final String EMPTY_ERROR_MESSAGE = "Role name already cannot be empty";

    @Inject
    @AutoBound
    protected DataBinder<KeyValueRow> row;

    @Inject
    @Bound(property = "key")
    @DataField("roleInput")
    protected TextBox role;

    @Inject
    @Bound(property = "value")
    @DataField("cardinalityInput")
    protected TextBox cardinality;

    private boolean allowDuplicateNames = false;

    private String previousRole;

    private String previousCardinality;

    @Inject
    protected Event<NotificationEvent> notification;

    @Inject
    @DataField
    protected Button deleteButton;

    @Inject
    @DataField("tableRow")
    protected HTMLTableRowElement tableRow;

    /**
     * Required for implementation of Delete button.
     */
    private Optional<RolesEditorWidgetView> parentWidget;

    protected RolesListItemWidgetViewImpl() {
    }

    public void setParentWidget(final RolesEditorWidgetView parentWidget) {
        this.parentWidget = Optional.ofNullable(parentWidget);
    }

    @PostConstruct
    public void init() {
        //role.setRegExp(ALPHA_NUM_REGEXP, INVALID_CHARACTERS_MESSAGE, INVALID_CHARACTERS_MESSAGE);
        role.addChangeHandler((e) -> handleValueChanged());
        cardinality.addChangeHandler((e) -> handleValueChanged());
        cardinality.addFocusHandler((e) -> handleFocus());
        //deleteButton.setIcon(IconType.TRASH);
        deleteButton.addClickHandler((e) -> handleDeleteButton());
        //show the widget that is hidden on the template
        tableRow.hidden = false;
        deleteButton.setText("DELETE");
    }

    private void handleFocus() {
        if (Objects.equals("0", cardinality.getText())) {
        }
    }

    private void handleValueChanged() {
    }

    @Override
    public void setReadOnly(final boolean readOnly) {
        deleteButton.setEnabled(!readOnly);
        role.setEnabled(!readOnly);
        cardinality.setEnabled(!readOnly);
    }

    @Override
    public boolean isDuplicateName(final String name) {
        return parentWidget.map(p -> p.isDuplicateName(name)).orElse(false);
    }

    public void handleDeleteButton() {
        parentWidget.ifPresent(p -> p.remove(getValue()));
    }

    @Override
    public void notifyModelChanged() {
        parentWidget.ifPresent(RolesEditorWidgetView::notifyModelChanged);
    }

    @Override
    public void setValue(KeyValueRow value) {
        //when first setting the value then set as previous as well
        if (Objects.isNull(previousRole)) {
            previousRole = value.getKey();
            previousCardinality = value.getValue();
        }
        row.setModel(value);
    }

    @Override
    public KeyValueRow getValue() {
        return row.getModel();
    }

    @Override
    public KeyValueRow getModel() {
        return getValue();
    }

    @Override
    public void setModel(KeyValueRow model) {
        setValue(model);
    }
}

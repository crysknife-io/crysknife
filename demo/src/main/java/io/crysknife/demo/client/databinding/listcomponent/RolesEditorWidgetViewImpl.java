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
import elemental2.dom.NotificationEvent;
import io.crysknife.client.BeanManager;
import io.crysknife.ui.databinding.client.api.AutoBound;
import io.crysknife.ui.databinding.client.api.Bound;
import io.crysknife.ui.databinding.client.api.Convert;
import io.crysknife.ui.databinding.client.api.DataBinder;
import io.crysknife.ui.databinding.client.api.handler.property.PropertyChangeHandler;
import io.crysknife.ui.databinding.client.components.ListComponent;
import io.crysknife.ui.databinding.client.components.ListContainer;
import io.crysknife.ui.templates.client.annotation.DataField;
import io.crysknife.ui.templates.client.annotation.Templated;
import org.gwtproject.event.logical.shared.ValueChangeEvent;
import org.gwtproject.event.logical.shared.ValueChangeHandler;
import org.gwtproject.event.shared.HandlerRegistration;
import org.gwtproject.user.client.ui.Button;
import org.gwtproject.user.client.ui.Composite;
import org.gwtproject.user.client.ui.HasValue;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Default;
import javax.inject.Inject;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 9/17/21
 */
@Dependent
@Templated("RolesEditorWidget.html")
public class RolesEditorWidgetViewImpl extends Composite implements RolesEditorWidgetView,
        HasValue<String> {

    @Inject
    @DataField("addButton")
    protected Button addButton;
    @Inject
    @AutoBound
    protected DataBinder<List<KeyValueRow>> binder;
    @Inject
    @DataField("list")
    @Bound
    @ListContainer("tbody")
    protected ListComponent<KeyValueRow, RolesListItemWidgetView> list;
    @Inject
    protected Event<NotificationEvent> notification;
    @Inject
    BeanManager beanManager;
    private String serializedRoles;
    private Optional<Presenter> presenter;
    private boolean readOnly = false;

    public RolesEditorWidgetViewImpl() {
        this.presenter = Optional.empty();
    }

    @Override
    public String getValue() {
        return serializedRoles;
    }

    @Override
    public void setValue(final String value) {
        doSetValue(value, false, true);
    }

    @Override
    public void setValue(final String value,
                         final boolean fireEvents) {
        doSetValue(value, fireEvents, false);
    }

    protected void doSetValue(final String value,
                              final boolean fireEvents,
                              final boolean initializeView) {
        final String oldValue = serializedRoles;
        serializedRoles = value;
        if (initializeView) {
            initView();
        }
        if (fireEvents) {
            ValueChangeEvent.fireIfNotEqual(this, oldValue, serializedRoles);
        }
        setReadOnly(readOnly);
    }

    private List<KeyValueRow> removeEmptyRoles(List<KeyValueRow> roles) {
        return roles.stream().filter(row -> !StringUtils.isEmpty(row.getKey())).collect(Collectors.toList());
    }

    protected void initView() {
        setRows(presenter.map(p -> p.deserialize(serializedRoles)).orElse(null));
    }

    @Override
    public HandlerRegistration addValueChangeHandler(final ValueChangeHandler<String> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    @Override
    public void init(final Presenter presenter) {

    }

    @Override
    public void doSave() {
        presenter.map(p -> p.serialize(removeEmptyRoles(getRows()))).ifPresent(newValue -> setValue(newValue, true));
    }

    @Override
    public void notifyModelChanged() {
        doSave();
    }

    @PostConstruct
    public void init2() {
        addButton.addClickHandler((e) -> handleAddVarButton());
        //binder.bind(list, "this", Convert.getConverter(List.class, List.class), null, false);

        binder.addPropertyChangeHandler((PropertyChangeHandler<List<KeyValueRow>>)
                event -> event.getNewValue()
                        .forEach(elm -> DomGlobal.console.log("elm -> " + elm.getKey() + " " + elm.getValue())));
    }

    protected void handleAddVarButton() {
        getRows().add(getRowsCount(), new KeyValueRow());
        final RolesListItemWidgetView widget = getWidget(getRowsCount() - 1);
        widget.setParentWidget(this);
    }

    @Override
    public int getRowsCount() {
        return Optional.ofNullable(getRows()).map(List::size).orElse(0);
    }

    @Override
    public List<KeyValueRow> getRows() {
        return binder.getModel();
    }

    @Override
    public void setRows(final List<KeyValueRow> rows) {
        binder.setModel(rows);
        for (int i = 0; i < getRowsCount(); i++) {
            RolesListItemWidgetView widget = getWidget(i);
            widget.setParentWidget(this);
        }
    }

    @Override
    public RolesListItemWidgetView getWidget(int index) {
        return list.getComponent(index);
    }

    @Override
    public void remove(final KeyValueRow row) {
        getRows().remove(row);
        doSave();
    }

    @Override
    public void setReadOnly(final boolean readOnly) {
        this.readOnly = readOnly;
        addButton.setEnabled(!readOnly);
        for (int i = 0; i < getRowsCount(); i++) {
            getWidget(i).setReadOnly(readOnly);
        }
    }

    @Override
    public boolean isDuplicateName(String name) {
        return getRows().stream().filter(row -> Objects.equals(row.getKey(), name)).count() > 1;
    }
}

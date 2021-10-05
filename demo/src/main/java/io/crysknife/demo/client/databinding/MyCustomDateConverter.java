/*
 * Copyright © 2021
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

package io.crysknife.demo.client.databinding;

import io.crysknife.ui.databinding.client.api.Converter;
import io.crysknife.ui.databinding.client.api.DefaultConverter;
import org.gwtproject.i18n.client.DateTimeFormat;

import java.util.Date;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 9/22/21
 */
@DefaultConverter
public class MyCustomDateConverter implements Converter<Date, String> {

    private static final String DATE_FORMAT = "YY_DD_MM";

    @Override
    public Class<Date> getModelType() {
        return Date.class;
    }

    @Override
    public Class<String> getComponentType() {
        return String.class;
    }

    @Override
    public Date toModelValue(String widgetValue) {

        return DateTimeFormat.getFormat(DATE_FORMAT).parse(widgetValue);

    }

    @Override
    public String toWidgetValue(Date modelValue) {
        return DateTimeFormat.getFormat(DATE_FORMAT).format((Date) modelValue);
    }
}

package org.jboss.gwt.elemento.processor.context;

import java.io.File;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 6/2/19
 */
public class StyleSheet {

    private String style;
    private File file;

    public StyleSheet(String style, File file) {

        this.style = style;
        this.file = file;
    }

    public String getStyle() {
        return style;
    }

    public File getFile() {
        return file;
    }

    public boolean isLess() {
        return style.endsWith(".less");
    }

    @Override
    public String toString() {
        return "StyleSheet{" +
                "style='" + style + '\'' +
                ", file=" + file +
                ", isLess=" + isLess() +
                '}';
    }
}

package org.treblereel.gwt.crysknife.generator;

import javax.inject.Inject;
import javax.inject.Provider;

import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.NameExpr;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLAnchorElement;
import elemental2.dom.HTMLAreaElement;
import elemental2.dom.HTMLAudioElement;
import elemental2.dom.HTMLBRElement;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLCanvasElement;
import elemental2.dom.HTMLDataListElement;
import elemental2.dom.HTMLDetailsElement;
import elemental2.dom.HTMLDialogElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLEmbedElement;
import elemental2.dom.HTMLFieldSetElement;
import elemental2.dom.HTMLFormElement;
import elemental2.dom.HTMLHRElement;
import elemental2.dom.HTMLHeadingElement;
import elemental2.dom.HTMLImageElement;
import elemental2.dom.HTMLInputElement;
import elemental2.dom.HTMLLIElement;
import elemental2.dom.HTMLLabelElement;
import elemental2.dom.HTMLLegendElement;
import elemental2.dom.HTMLMapElement;
import elemental2.dom.HTMLMenuElement;
import elemental2.dom.HTMLMenuItemElement;
import elemental2.dom.HTMLMeterElement;
import elemental2.dom.HTMLOListElement;
import elemental2.dom.HTMLObjectElement;
import elemental2.dom.HTMLOptGroupElement;
import elemental2.dom.HTMLOptionElement;
import elemental2.dom.HTMLOutputElement;
import elemental2.dom.HTMLParagraphElement;
import elemental2.dom.HTMLParamElement;
import elemental2.dom.HTMLPreElement;
import elemental2.dom.HTMLProgressElement;
import elemental2.dom.HTMLQuoteElement;
import elemental2.dom.HTMLScriptElement;
import elemental2.dom.HTMLSelectElement;
import elemental2.dom.HTMLSourceElement;
import elemental2.dom.HTMLTableCaptionElement;
import elemental2.dom.HTMLTableCellElement;
import elemental2.dom.HTMLTableColElement;
import elemental2.dom.HTMLTableElement;
import elemental2.dom.HTMLTableRowElement;
import elemental2.dom.HTMLTextAreaElement;
import elemental2.dom.HTMLTrackElement;
import elemental2.dom.HTMLUListElement;
import elemental2.dom.HTMLVideoElement;
import org.treblereel.gwt.crysknife.annotation.Generator;
import org.treblereel.gwt.crysknife.client.internal.InstanceImpl;
import org.treblereel.gwt.crysknife.generator.api.ClassBuilder;
import org.treblereel.gwt.crysknife.generator.context.IOCContext;
import org.treblereel.gwt.crysknife.generator.definition.BeanDefinition;
import org.treblereel.gwt.crysknife.generator.definition.Definition;
import org.treblereel.gwt.crysknife.generator.point.FieldPoint;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 4/7/19
 */
@Generator(priority = 100000)
public class Elemenatal2FactoryGenerator extends BeanIOCGenerator {

    private static final SetMultimap<Class, String> HTML_ELEMENTS = HashMultimap.create();

    static {
        HTML_ELEMENTS.put(HTMLAnchorElement.class, "a");
        HTML_ELEMENTS.put(HTMLAreaElement.class, "area");
        HTML_ELEMENTS.put(HTMLAudioElement.class, "audio");
        HTML_ELEMENTS.put(HTMLQuoteElement.class, "blockquote");
        HTML_ELEMENTS.put(HTMLBRElement.class, "br");
        HTML_ELEMENTS.put(HTMLButtonElement.class, "button");
        HTML_ELEMENTS.put(HTMLCanvasElement.class, "canvas");
        HTML_ELEMENTS.put(HTMLTableCaptionElement.class, "caption");
        HTML_ELEMENTS.put(HTMLTableColElement.class, "col");
        HTML_ELEMENTS.put(HTMLDataListElement.class, "datalist");
        HTML_ELEMENTS.put(HTMLDetailsElement.class, "details");
        HTML_ELEMENTS.put(HTMLDialogElement.class, "dialog");
        HTML_ELEMENTS.put(HTMLDivElement.class, "div");
        HTML_ELEMENTS.put(HTMLEmbedElement.class, "embed");
        HTML_ELEMENTS.put(HTMLFieldSetElement.class, "fieldset");
        HTML_ELEMENTS.put(HTMLFormElement.class, "form");
        HTML_ELEMENTS.put(HTMLHeadingElement.class, "named");
        HTML_ELEMENTS.put(HTMLElement.class, "named");
        HTML_ELEMENTS.put(HTMLHRElement.class, "hr");
        HTML_ELEMENTS.put(HTMLImageElement.class, "img");
        HTML_ELEMENTS.put(HTMLInputElement.class, "input");
        HTML_ELEMENTS.put(HTMLLabelElement.class, "label");
        HTML_ELEMENTS.put(HTMLLegendElement.class, "legend");
        HTML_ELEMENTS.put(HTMLLIElement.class, "li");
        HTML_ELEMENTS.put(HTMLMapElement.class, "map");
        HTML_ELEMENTS.put(HTMLMenuElement.class, "menu");
        HTML_ELEMENTS.put(HTMLMenuItemElement.class, "menuitem");
        HTML_ELEMENTS.put(HTMLMeterElement.class, "meter");
        HTML_ELEMENTS.put(HTMLObjectElement.class, "object");
        HTML_ELEMENTS.put(HTMLOListElement.class, "ol");
        HTML_ELEMENTS.put(HTMLOptGroupElement.class, "optgroup");
        HTML_ELEMENTS.put(HTMLOptionElement.class, "option");
        HTML_ELEMENTS.put(HTMLOutputElement.class, "output");
        HTML_ELEMENTS.put(HTMLParagraphElement.class, "p");
        HTML_ELEMENTS.put(HTMLParamElement.class, "param");
        HTML_ELEMENTS.put(HTMLPreElement.class, "pre");
        HTML_ELEMENTS.put(HTMLProgressElement.class, "progress");
        HTML_ELEMENTS.put(HTMLQuoteElement.class, "q");
        HTML_ELEMENTS.put(HTMLScriptElement.class, "script");
        HTML_ELEMENTS.put(HTMLSelectElement.class, "select");
        HTML_ELEMENTS.put(HTMLSourceElement.class, "source");
        HTML_ELEMENTS.put(HTMLTableElement.class, "table");
        HTML_ELEMENTS.put(HTMLTableCellElement.class, "td");
        HTML_ELEMENTS.put(HTMLTextAreaElement.class, "textarea");
        HTML_ELEMENTS.put(HTMLTableRowElement.class, "tr");
        HTML_ELEMENTS.put(HTMLTrackElement.class, "track");
        HTML_ELEMENTS.put(HTMLUListElement.class, "ul");
        HTML_ELEMENTS.put(HTMLVideoElement.class, "video");
    }

    @Override
    public void register(IOCContext iocContext) {
        this.iocContext = iocContext;
        HTML_ELEMENTS.keySet().forEach(clazz -> {
            iocContext.register(Inject.class, clazz, WiringElementType.FIELD_TYPE, this);
            iocContext.getBlacklist().add(clazz.getCanonicalName());
        });
    }

    @Override
    public void generateBeanFactory(ClassBuilder classBuilder, Definition definition) {

    }

    @Override
    public Expression generateBeanCall(ClassBuilder classBuilder, FieldPoint fieldPoint, BeanDefinition beanDefinition) {
        classBuilder.getClassCompilationUnit().addImport(DomGlobal.class);
        classBuilder.getClassCompilationUnit().addImport(InstanceImpl.class);
        classBuilder.getClassCompilationUnit().addImport(Provider.class);
        classBuilder.getClassCompilationUnit().addImport(beanDefinition.getType().getQualifiedName().toString());

        return new NameExpr("(" + beanDefinition.getType().getSimpleName() + ")DomGlobal.document.createElement(" + getTagFromType(fieldPoint, beanDefinition) + ")");
    }

    private String getTagFromType(FieldPoint fieldPoint, BeanDefinition beanDefinition) {
        if(fieldPoint.isNamed()) {
            return "\"" + fieldPoint.getNamed() + "\"";
        }

        Class clazz;
        try {
            clazz = Class.forName(beanDefinition.getType().getQualifiedName().toString());
        } catch (ClassNotFoundException e) {
            throw new Error("Unable to process " + beanDefinition.getType().getQualifiedName().toString() + " " + e.getMessage());
        }

        if (!HTML_ELEMENTS.containsKey(clazz)) {
            throw new Error("Unable to process " + beanDefinition.getType().getQualifiedName().toString());
        }

        return "\"" + HTML_ELEMENTS.get(clazz).stream().findFirst().get() + "\"";
    }
}
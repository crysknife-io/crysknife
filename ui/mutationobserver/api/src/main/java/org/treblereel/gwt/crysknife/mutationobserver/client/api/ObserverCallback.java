package org.treblereel.gwt.crysknife.mutationobserver.client.api;

import elemental2.dom.MutationRecord;
import jsinterop.annotations.JsFunction;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 12/9/19
 */
@JsFunction
@FunctionalInterface
public interface ObserverCallback {

    void onAttachOrDetachCallback(MutationRecord mutationRecord);
}

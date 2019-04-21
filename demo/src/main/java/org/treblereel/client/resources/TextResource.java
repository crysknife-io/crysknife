package org.treblereel.client.resources;

import org.gwtproject.resources.client.ClientBundle;
import org.gwtproject.resources.client.ExternalTextResource;
import org.gwtproject.resources.client.Resource;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 4/20/19
 */
@Resource
public interface TextResource {

    @ClientBundle.Source("hello.txt")
    ExternalTextResource helloWorldExternal();

    @ClientBundle.Source("hello.txt")
    org.gwtproject.resources.client.TextResource helloWorldRelative();
}

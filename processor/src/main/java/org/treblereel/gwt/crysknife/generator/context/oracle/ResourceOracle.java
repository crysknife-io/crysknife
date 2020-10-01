/*
 * Copyright 2008 Google Inc.
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
package org.treblereel.gwt.crysknife.generator.context.oracle;

import java.net.URL;
import javax.lang.model.element.ExecutableElement;

import org.treblereel.gwt.crysknife.exception.UnableToCompleteException;
import org.treblereel.gwt.crysknife.logger.TreeLogger;

/**
 * An abstraction for finding and retrieving {@link Resource}s by abstract path name. Intuitively,
 * it works like a jar in that each URL is uniquely located somewhere in an abstract namespace. The
 * abstract names must be constructed from a series of zero or more valid Java identifiers followed
 * by the '/' character and finally ending in a valid filename, for example, <code>
 * com/google/gwt/blah.txt</code>.
 *
 * <p>
 * The identity of the returned sets and maps will change when the underlying module is refreshed.
 */
public interface ResourceOracle {

  /** Returns the resource for the given path name or null if there is no such resource. */
  URL[] findResources(CharSequence packageName, CharSequence[] pathName);

  URL findResource(CharSequence pkg, CharSequence relativeName);

  URL findResource(CharSequence fullPath);

}

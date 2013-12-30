/*
 *  Copyright 2012 by The JFlux Project (www.jflux.org).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jflux.api.core;

/**
 * An interface for taking one object and returning another.
 * Objects do not have to be of the same type.
 * @param <A> Input type
 * @param <B> Output type
 * @author Matthew Stevenson <www.jflux.org>
 */
public interface Adapter<A,B> {
    /**
     * Take one object and return another.
     * @param a input object
     * @return output object
     */
    public B adapt(A a);
}

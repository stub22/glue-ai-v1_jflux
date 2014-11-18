/*
 * Copyright 2012 The JFlux Project (www.jflux.org).
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
package org.jflux.api.core.util;

import org.jflux.api.core.Source;

/**
 * Basic implementation of a Source
 * @author Matthew Stevenson
 * @param <T> output data type
 */
public class DefaultSource<T> implements Source<T> {
    private T myT;

    /**
     * Builds a DefaultSource with an initial value
     * @param t
     */
    public DefaultSource(T t){
        myT = t;
    }

    /**
     * Reports the internal value
     * @return the value
     */
    @Override
    public T getValue() {
        return myT;
    }
    
    /**
     * Sets the internal value
     * @param t new value
     */
    public void setValue(T t){
        myT = t;
    }
}

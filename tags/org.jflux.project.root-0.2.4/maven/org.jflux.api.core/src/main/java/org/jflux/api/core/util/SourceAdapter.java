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

import org.jflux.api.core.Adapter;
import org.jflux.api.core.Source;

/**
 * Adapter to pull output from a Source
 * @author Matthew Stevenson
 * @param <A> input data type
 * @param <B> output data type
 */
public class SourceAdapter<A, B> implements Adapter<A, B> {
    private Source<B> mySource;

    /**
     * Builds a SourceAdapter around a Source
     * @param source
     */
    public SourceAdapter(Source<B> source){
        if(source == null){
            throw new NullPointerException();
        }
        mySource = source;
    }
    
    /**
     * Disregard the input and return the Source's value
     * @param a input data
     * @return Source value
     */
    @Override
    public B adapt(A a) {
        return mySource.getValue();
    }
    
}

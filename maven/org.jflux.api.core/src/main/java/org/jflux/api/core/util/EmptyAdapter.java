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

/**
 * Adapter that does nothing
 * @author Matthew Stevenson
 * @param <B> output data type
 * @param <A> input data type
 */
public class EmptyAdapter<B, A extends B> implements Adapter<A, B> {

    /**
     * Returns the input with no modification
     * @param a input data
     * @return same as input data
     */
    @Override
    public B adapt(A a) {
        return a;
    }
    
    /**
     * Adapter that only casts to the output type
     * @param <A> input data type
     * @param <B> output data type
     */
    public static class UnsafeCastingAdapter<A, B> implements Adapter<A, B> {

        /**
         * Cast data from input type to output type
         * @param a input data
         * @return same as input data, cast to output type
         */
        @Override
        public B adapt(A a) {
            return (B)a;
        }
        
    }
}

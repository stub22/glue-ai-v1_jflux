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
 *
 * @author Matthew Stevenson
 */
public class CompoundAdapter<A,B,C> implements Adapter<A, C> {
    private Adapter<A,B> myFirst;
    private Adapter<B,C> mySecond;

    public CompoundAdapter(Adapter<A, B> first, Adapter<B, C> second) {
        if(first == null || second == null){
            throw new NullPointerException();
        }
        myFirst = first;
        mySecond = second;
    }

    @Override
    public C adapt(A a) {
        B b = myFirst.adapt(a);
        return mySecond.adapt(b);
    }
}

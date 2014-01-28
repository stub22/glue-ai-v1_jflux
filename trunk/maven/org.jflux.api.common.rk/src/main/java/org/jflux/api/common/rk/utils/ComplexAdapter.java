/*
 * Copyright 2014 the JFlux Project.
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
package org.jflux.api.common.rk.utils;

import org.jflux.api.core.Adapter;

/**
 *
 * @author Matthew Stevenson <www.jflux.org>
 */
public final class ComplexAdapter<A,B,C> implements Adapter<A, C> {
    private Adapter<A,B> myFirstAdapter;
    private Adapter<B,C> mySecondAdapter;

    public ComplexAdapter(Adapter<A,B> first, Adapter<B,C> second){
        if(first == null || second == null){
            throw new NullPointerException();
        }
        myFirstAdapter = first;
        mySecondAdapter = second;
    }
    
    @Override
    public final C adapt(A a) {
        if(myFirstAdapter == null || mySecondAdapter == null || a == null){
            throw new NullPointerException();
        }
        B b = myFirstAdapter.adapt(a);
        if(b == null){
            return null;
        }
        return mySecondAdapter.adapt(b);
    }
    
    
}

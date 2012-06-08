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
package org.jflux.api.messaging.encode;

import org.jflux.api.core.Adapter;
import org.jflux.api.core.Source;

/**
 *
 * @author Matthew Stevenson
 */
public class EncodeRequest<In,Out> {
    private In myValue;
    private Out myOutputFactory;
    
    public static <I,O> Adapter<I,EncodeRequest<I,O>>factory(
            Source<O> reusableOutputSource){
        return new EncodeRequestFactory<I, O>(reusableOutputSource);
    }
    
    public static <I,O> Adapter<I,EncodeRequest<I,O>>factory(
            Class<I> clazz, Source<O> reusableOutputSource){
        return new EncodeRequestFactory<I, O>(reusableOutputSource);
    }

    public EncodeRequest(In value, Out reusableOutput){
        myValue = value;
        myOutputFactory = reusableOutput;
    }

    public In getValue(){
        return myValue;
    }

    public Out getStream(){
        return myOutputFactory;
    }
    
    public static class InnerAdapter<A,B,S> 
            implements Adapter<EncodeRequest<A,S>,EncodeRequest<B,S>>{
        private Adapter<A,B> myAdapter;
        
        public InnerAdapter(Adapter<A,B> adapter){
            if(adapter == null){
                throw new NullPointerException();
            }
            myAdapter = adapter;
        }
        
        @Override
        public EncodeRequest<B, S> adapt(EncodeRequest<A, S> a) {
            B b = myAdapter.adapt(a.getValue());
            return new EncodeRequest<B, S>(b, a.getStream());
        }
    
    }
    
    private static class EncodeRequestFactory<In,Out> implements 
            Adapter<In,EncodeRequest<In,Out>>{
        private Source<Out> myStreamSource;

        public EncodeRequestFactory(Source<Out> reusableOutputFactory){
            myStreamSource = reusableOutputFactory;
        }

        @Override
        public EncodeRequest<In, Out> adapt(In a) {
            Out s = myStreamSource == null ? null : myStreamSource.getValue();
            return new EncodeRequest<In, Out>(a, s);
        }
    }
}

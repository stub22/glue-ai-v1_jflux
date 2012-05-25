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
package org.jflux.api.core.event;

import org.jflux.api.core.Adapter;
import org.jflux.api.core.Source;
import org.jflux.api.core.util.SourceAdapter;

/**
 *
 * @author Matthew Stevenson
 */
public class BasicEvent<H,D> implements Event<H,D> {
    private H myHeader;
    private D myData;
    
    public BasicEvent(H header, D data){
        myHeader = header;
        myData = data;
    }
    
    @Override
    public H getHeader() {
        return myHeader;
    }

    @Override
    public D getData() {
        return myData;
    }
    
    public static class BasicEventFactory<H,D> implements Adapter<D,Event<H,D>> {
        private Adapter<D,H> myHeaderFactory;
        
        public BasicEventFactory(Adapter<D,H> headerFactory){
            if(headerFactory == null){
                throw new NullPointerException();
            }
            myHeaderFactory = headerFactory;
        }
        
        public BasicEventFactory(Source<H> headerSource){
            this(new SourceAdapter<D, H>(headerSource));
        }
        
        @Override
        public Event<H, D> adapt(D a) {
            H header = myHeaderFactory.adapt(a);
            return new BasicEvent<H, D>(header, a);
        }
        
    }
}

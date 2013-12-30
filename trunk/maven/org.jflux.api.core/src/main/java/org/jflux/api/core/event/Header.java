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
package org.jflux.api.core.event;

import java.util.Map;
import org.jflux.api.core.Adapter;

/**
 *
 * @author Matthew Stevenson <www.jflux.org>
 */
public interface Header<SourceRef, Time> {
    public SourceRef getSourceReference();
    public Time getTimestamp();
    public String getEventType();
    public Map<String,String> getHeaderProperties();
    
    public static class HeaderTypeAdapter implements Adapter<Header,String> {
        @Override 
        public String adapt(Header a) {
            return a == null ? null : a.getEventType();
        }
    }
    
    public static class HeaderPropertyAdapter implements 
            Adapter<Header,String> {
        private String myPropertyKey;
        public HeaderPropertyAdapter(String propertyKey){
            if(propertyKey == null){
                throw new NullPointerException();
            }
            myPropertyKey = propertyKey;
        }
        
        @Override 
        public String adapt(Header a) {
            if(a == null || a.getHeaderProperties() == null){
                return null;
            }
            Map<String,String> props = a.getHeaderProperties();
            return props.get(myPropertyKey);
        }
    }
}

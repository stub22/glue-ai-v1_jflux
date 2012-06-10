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
package org.jflux.impl.encode.avro;

import org.jflux.api.core.Adapter;
import org.jflux.api.core.event.Header;
import org.jflux.avrogen.HeaderRecord;

/**
 *
 * @author Matthew Stevenson
 */
public class HeaderEncoder implements Adapter<Header<String,Long>,HeaderRecord> {
    @Override
    public HeaderRecord adapt(Header<String,Long> a) {
        if(a instanceof HeaderRecord){
            return (HeaderRecord)a;
        }
        HeaderRecord r = new HeaderRecord();
        r.setSourceReference(a.getSourceReference());
        r.setTimestamp(a.getTimestamp());
        r.setEventType(a.getEventType());
        r.setHeaderProperties(a.getHeaderProperties());
        return r;
    }
    
    public <S,T> HeaderRecord adapt(Header<S,T> a, Adapter<S,String> srcAdapter, Adapter<T,Long> timeAdapter) {
        if(a instanceof HeaderRecord){
            return (HeaderRecord)a;
        }
        HeaderRecord r = new HeaderRecord();
        r.setSourceReference(srcAdapter.adapt(a.getSourceReference()));
        r.setTimestamp(timeAdapter.adapt(a.getTimestamp()));
        r.setEventType(a.getEventType());
        r.setHeaderProperties(a.getHeaderProperties());
        return r;
    }
}

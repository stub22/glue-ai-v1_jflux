/*
 * Copyright 2012 by The JFlux Project (www.jflux.org).
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
package org.jflux.api.data.blend;

import java.util.ArrayList;
import java.util.List;
import org.jflux.api.core.Source;

/**
 *
 * @author Matthew Stevenson <www.jflux.org>
 */
public interface DataCollector<D,L> extends Source<L> {
    
    public void addSource(Source<D> source);
    public void removeSource(Source<D> source);
    
    public static class DefaultCollector<D> implements 
            DataCollector<D, List<D>> {
        private List<Source<D>> myDataSources;

        public DefaultCollector(){
            myDataSources = new ArrayList<Source<D>>();
        }
        
        @Override
        public List<D> getValue() {
            List<D> list = new ArrayList<D>(myDataSources.size());
            for(Source<D> source : myDataSources){
                D data = source.getValue();
                list.add(data);
            }
            return list;
        }
        
        @Override
        public void addSource(Source<D> source){
            if(source == null){
                throw new NullPointerException();
            }
            if(!myDataSources.contains(source)){
                myDataSources.add(source);
            }
        }
        
        @Override
        public void removeSource(Source<D> source){
            if(source == null){
                throw new NullPointerException();
            }
            myDataSources.remove(source);
        }
    }
}

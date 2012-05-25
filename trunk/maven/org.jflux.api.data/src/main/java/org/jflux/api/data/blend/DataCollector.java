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
import org.jflux.api.core.Adapter;

/**
 *
 * @author Matthew Stevenson <www.jflux.org>
 */
public interface DataCollector<M,D,L> extends Adapter<M,L>  {
    public static class DefaultCollector<M,D> implements 
            DataCollector<M, D, List<D>> {
        private List<Adapter<M,D>> myDataSources;

        public DefaultCollector(){
            myDataSources = new ArrayList<Adapter<M, D>>();
        }
        
        @Override
        public List<D> adapt(M msg) {
            List<D> list = new ArrayList<D>(myDataSources.size());
            for(Adapter<M,D> source : myDataSources){
                D data = source.adapt(msg);
                list.add(data);
            }
            return list;
        }
        
        public void addSource(Adapter<M,D> source){
            if(source == null){
                throw new NullPointerException();
            }
            myDataSources.add(source);
        }
        
    }
}

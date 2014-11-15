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

package org.jflux.api.common.rk.types;

import org.jflux.impl.services.rk.utils.HashCodeUtil;

/**
 *
 * @author Matthew Stevenson <www.jflux.org>
 */
public class Temperature implements Comparable {
    private double myDegreesCelsius;
    
    public Temperature(double degCelsius){
        myDegreesCelsius = degCelsius;
    }
    
    public final double getDegreesCelsius(){
        return myDegreesCelsius;
    }
    
    @Override
    public int compareTo(Object o) {
        if(o == null || !Temperature.class.isAssignableFrom(o.getClass())){
            return 1;
        }
        int compare = Double.compare(
                myDegreesCelsius, ((Temperature)o).myDegreesCelsius);
        
        //The values can be the same but if the types are actually different
        //then compareTo needs to be consistent with equals().         
        if(compare == 0 && o.getClass() != this.getClass()){
            return 1;
        }
        return compare;
    }
    
    @Override
    public Object clone(){
        return new Temperature(myDegreesCelsius);
    }
    
    @Override
    public boolean equals(Object obj){
        if(obj == null ||  obj.getClass() != this.getClass()){
            return false;
        }
        return myDegreesCelsius == ((Temperature)obj).myDegreesCelsius;
    }

    @Override
    public int hashCode() {
        return HashCodeUtil.hash(HashCodeUtil.SEED, myDegreesCelsius);
    }
    
    @Override
    public String toString(){
        return Double.toString(myDegreesCelsius);
    }
}

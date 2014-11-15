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
public class Voltage implements Comparable {
    private double myVolts;
    
    public Voltage(double volts){
        myVolts = volts;
    }
    
    public final double getVolts(){
        return myVolts;
    }
    
    @Override
    public int compareTo(Object o) {
        if(o == null || !Voltage.class.isAssignableFrom(o.getClass())){
            return 1;
        }
        int compare = Double.compare(
                myVolts, ((Voltage)o).myVolts);
        
        //The values can be the same but if the types are actually different
        //then compareTo needs to be consistent with equals().         
        if(compare == 0 && o.getClass() != this.getClass()){
            return 1;
        }
        return compare;
    }
    
    @Override
    public Object clone(){
        return new Voltage(myVolts);
    }
    
    @Override
    public boolean equals(Object obj){
        if(obj == null ||  obj.getClass() != this.getClass()){
            return false;
        }
        return myVolts == ((Voltage)obj).myVolts;
    }

    @Override
    public int hashCode() {
        return HashCodeUtil.hash(HashCodeUtil.SEED, myVolts);
    }
    
    @Override
    public String toString(){
        return Double.toString(myVolts);
    }
}

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

package org.jflux.api.common.rk.position;

import org.jflux.impl.services.rk.utils.HashCodeUtil;

/**
 * NormalizedDouble is a value normalized to the range of [0.0, 1.0] 
 * (0.0 &lt;= val &lt;= 1.0).
 * The value is stored as a Double.
 * When creating a NormalizedDouble, the value is checked to ensure it is within 
 * the range of [0.0, 1.0] and throws an IllegalArgumentException if invalid.
 * 
 * @author Matthew Stevenson <www.jflux.org>
 */
public class NormalizedDouble implements Comparable, Cloneable {
    private double myValue;
    
    /**
     * Creates a new NormalizedDouble with the given value
     * @param value the value to set
     * @throws IllegalArgumentException if value is not in the range [0.0, 1.0]
     */
    public NormalizedDouble(double value){
        rangeCheck(value);
        myValue = value;
    }
    
    /**
     * Returns the value of the NormalizeRange.
     * @return the value of the NormalizeRange
     */
    public double getValue(){
        return myValue;
    }
    
    /**
     * Checks that the value is within the range [0.0, 1.0].
     * @param value the value to check
     * @throws IllegalArgumentException if value is not in the range [0.0,1.0]
     */
    protected final void rangeCheck(double value){
        if(!isValid(value)){
            throw new IllegalArgumentException("NormalizedRange must be within "
                    + "the range [0.0, 1.0] (0.0 <= val <= 1.0).");
        }
    }
    
    /**
     * Returns true if the given value can be used to create a NormalizedDouble.
     * A valid value is in the range [0.0, 1.0]
     * @param value the value to test
     * @return true if the given value can be used to create a NormalizedDouble
     */
    public static boolean isValid(double value){
        return value >= 0.0 && value <= 1.0;
    }
    
    @Override
    public int compareTo(Object o) {
        if(o == null || !NormalizedDouble.class.isAssignableFrom(o.getClass())){
            return 1;
        }
        int compare = Double.compare(
                myValue, ((NormalizedDouble)o).myValue);
        
        /* The values can be the same but if the types are actually different
         * then compareTo needs to be consistent with equals(). 
         * The check above has looser type requirements so different typed 
         * values can be properly sorted together.*/
        if(compare == 0 && o.getClass() != this.getClass()){
            return 1;
        }
        return compare;
    }
    
    @Override
    public Object clone(){
        return new NormalizedDouble(myValue);
    }
    
    @Override
    public String toString(){
        return Double.toString(myValue);
    }
    
    @Override
    public boolean equals(Object obj){
        if(obj == null ||  obj.getClass() != this.getClass()){
            return false;
        }
        return myValue == ((NormalizedDouble)obj).myValue;
    }

    @Override
    public int hashCode() {
        return HashCodeUtil.hash(HashCodeUtil.SEED, myValue);
    }
}

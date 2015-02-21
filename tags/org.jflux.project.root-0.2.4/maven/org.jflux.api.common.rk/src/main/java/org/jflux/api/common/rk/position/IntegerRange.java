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

/**
 * Specifies a simple NormalizableRange of Numbers defined by a min and max.
 * The min is specifies the number corresponding to 0.0 when normalized.  The 
 * min is not always less than the max (using compareTo()).  If the min is
 * larger than the max, it results is reversing the direction of the range.
 * 
 * @author Matthew Stevenson <www.jflux.org>
 */
public class IntegerRange implements NormalizableRange<Integer> {
    private int myMin;
    private double myRange;
    private int myAMin; // arithmetic min
    private int myAMax; // arithmetic max
    
    public IntegerRange(int min, int max){
        myMin = min;
        myRange = max - min;
        if(myRange == 0){
            throw new IllegalArgumentException("Range cannot be zero.");
        }
        myAMin = min < max ? min : max;
        myAMax = min > max ? min : max;
    }
    
    @Override
    public boolean isValid(Integer val) {
        if(val == null){
            throw new NullPointerException();
        }
        return myAMin <= val && val <= myAMax;
    }

    @Override
    public NormalizedDouble normalizeValue(Integer val) {
        if(!isValid(val)){
            return null;
        }
        double norm = (val - myMin)/myRange;
        return new NormalizedDouble(norm);
    }

    @Override
    public Integer denormalizeValue(NormalizedDouble v) {
        return (int)(v.getValue()*myRange + myMin);
    }
    
    @Override
    public Integer getMin() {
        return myMin;
    }
    
    @Override
    public Integer getMax() {
        return (int)myRange + myMin;
    }
}

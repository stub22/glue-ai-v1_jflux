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
 * @author Matthew Stevenson <www.jflux.org>
 */
public class BooleanRange implements NormalizableRange<Boolean> {
    public final static BooleanRange DEFAULT_RANGE = 
            new BooleanRange(new NormalizedDouble(0.5));
    
    private NormalizedDouble myThreshold;
    
    public BooleanRange(NormalizedDouble threshold){
        if(threshold == null){
            throw new NullPointerException();
        }
        myThreshold = threshold;
    }
    
    @Override
    public boolean isValid(Boolean val) {
        return val != null;
    }

    @Override
    public NormalizedDouble normalizeValue(Boolean val) {
        return new NormalizedDouble(val ? 1 : 0);
    }

    @Override
    public Boolean denormalizeValue(NormalizedDouble v) {
        return v.getValue() >= myThreshold.getValue();
    }
    
    @Override
    public Boolean getMin() {
        return false;
    }
    
    @Override
    public Boolean getMax() {
        return true;
    }
}

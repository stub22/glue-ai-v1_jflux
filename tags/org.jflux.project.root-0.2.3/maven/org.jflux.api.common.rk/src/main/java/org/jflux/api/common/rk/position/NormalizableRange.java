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
 *
 * @author Matthew Stevenson <www.jflux.org>
 */
public interface NormalizableRange<T> {
    
    public final static DefaultRange NORMALIZED_RANGE = new DefaultRange();
    /**
     * Returns true if t is within the NormalizableRange.
     * @param t value to check
     * @return true if t is within the NormalizableRange
     */
    public boolean isValid(T t);
    /**
     * Normalizes a value within the NormalizableRange.
     * @param t value to normalize
     * @return value within the NormalizableRange, null if t is out of range
     */
    public NormalizedDouble normalizeValue(T t);
    /**
     * Denormalizes a NormalizedDouble to a value within this NormalizableRange.
     * @param v value to denormalize
     * @return non-normalized value within the NormalizableRange
     */
    public T denormalizeValue(NormalizedDouble v);
    
    public T getMin();
    /**
     * Returns the minimum value of the NormalizableRange.
     * @return minimum value of the NormalizableRange
     */
    
    public T getMax();
    /**
     * Returns the maximum value of the NormalizableRange.
     * @return maximum value of the NormalizableRange
     */
    
    public static class DefaultRange implements NormalizableRange<NormalizedDouble> {
        private final static NormalizedDouble theMin = new NormalizedDouble(0);
        private final static NormalizedDouble theMax = new NormalizedDouble(1);

        @Override
        public boolean isValid(NormalizedDouble t) {
            return t != null;
        }

        @Override
        public NormalizedDouble normalizeValue(NormalizedDouble t) {
            return t;
        }

        @Override
        public NormalizedDouble denormalizeValue(NormalizedDouble v) {
            return v;
        }

        @Override
        public NormalizedDouble getMin() {
            return theMin;
        }

        @Override
        public NormalizedDouble getMax() {
            return theMax;
        }
        
    }
}

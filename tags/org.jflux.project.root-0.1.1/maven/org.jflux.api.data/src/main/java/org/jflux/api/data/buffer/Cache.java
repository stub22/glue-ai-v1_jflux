/*
 * Copyright 2011 Hanson Robokind LLC.
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
package org.jflux.api.data.buffer;

import org.jflux.api.core.util.Listener;
import org.jflux.api.core.util.Source;

/**
 *
 * @author Matthew Stevenson <www.robokind.org>
 */
public interface Cache<T> extends Listener<T>, Source<T> {
    public static class SingleValueCache<T> implements Cache<T> {
        private T myValue;
        @Override
        public void handleEvent(T event) {
            myValue = event;
        }
        
        @Override
        public T getValue() {
            return myValue;
        }        
    }
}

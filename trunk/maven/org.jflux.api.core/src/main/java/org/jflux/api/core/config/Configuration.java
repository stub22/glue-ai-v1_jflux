/*
 * Copyright 2012 The Cogchar Project (www.cogchar.org).
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
package org.jflux.api.core.config;

import java.util.Set;
import org.jflux.api.core.Listener;
import org.jflux.api.core.Notifier;
import org.jflux.api.core.Source;
import org.jflux.api.core.event.ValueChange;

/**
 * Allows access to configuration values.
 * 
 * @param <K> type of key used by this configuration.
 * @author Matthew Stevenson
 */
public interface Configuration<K> {
    /**
     * Returns the set of keys contained in this configuration.
     * @return set of keys contained in this configuration
     */
    public Set<K> getKeySet();
    
    /**
     * Returns an untyped Source for retrieving a property value.
     * @param key property key
     * @return untyped Source for retrieving a property value, returns null if 
     * the property is not available
     */
    public Source getPropertySource(K key);
    /**
     * Returns a typed Source for retrieving a property value.
     * @param <T> type of the property value
     * @param propertyClass class of the property value
     * @param key property key
     * @return typed Source for retrieving a property value, returns null if the
     * property is not available
     */
    public <T> Source<T> getPropertySource(Class<T> propertyClass, K key);
    
    /**
     * Returns a Notifier to notify listeners of changes to the property.
     * @param key property key
     * @return Notifier to notify listeners of changes to the property, returns 
     * null if the property is not available
     */
    public Notifier<ValueChange> getPropertyNotifier(K key);
    /**
     * Returns a typed Notifier to notify listeners of changes to the property.
     * @param <T> type of the property value
     * @param propertyClass class of the property value
     * @param key property key
     * @return typed Notifier to notify listeners of changes to the property, 
     * returns null if the property is not available
     */
    public <T> Notifier<ValueChange<T>> getPropertyNotifier(
            Class<T> propertyClass, K key);
    
    /**
     * Returns a Listener which sets the property value.
     * @param key property key
     * @return Listener which sets the property value, returns null if the
     * property is not available
     */
    public Listener getPropertySetter(K key);
    /**
     * Returns a typed Listener which sets the property value.
     * @param <T> type of the property value
     * @param propertyClass class of the property value
     * @param key property key
     * @return typed Listener which sets the property value, returns null if the
     * property is not available
     */
    public <T> Listener<T> getPropertySetter(Class<T> propertyClass, K key);
}

/*
 * Copyright 2012 The JFlux Project (www.jflux.org).
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

import org.jflux.api.core.Listener;
import org.jflux.api.core.Notifier;
import org.jflux.api.core.Source;
import org.jflux.api.core.event.ValueChange;

/**
 * Holds a property from a configuration.
 * 
 * @author Matthew Stevenson
 */
public interface ConfigProperty<V> {
    /**
     * Returns a Source for retrieving the property value.
     * @return Source for retrieving the property value
     */
    public Source<V> getSource();
    /**
     * Returns a Notifier to notify listeners of changes to the property.
     * @return Notifier to notify listeners of changes to the property
     */
    public Notifier<ValueChange<V>> getNotifier();
    /**
     * Returns a Listener which sets the property value.
     * @return Listener which sets the property value
     */
    public Listener<V> getSetter();
    /**
     * Returns the class of the property value.
     * @return class of the property value
     */
    public Class<V> getPropertyClass();
}

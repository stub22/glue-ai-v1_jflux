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
package org.jflux.api.core.util;

import java.util.Set;
import org.jflux.api.core.Listener;
import org.jflux.api.core.Notifier;
import org.jflux.api.core.Source;

/**
 *
 * @author Matthew Stevenson
 */
public interface Configuration<K> {
    public Set<K> getKeySet();
    
    public Source<?> getPropertySource(K key);
    public <T> Source<T> getPropertySource(Class<T> propertyClass, K key);
    
    public Notifier<?> getPropertyNotifier(K key);
    public <T> Notifier<T> getPropertyNotifier(Class<T> propertyClass, K key);
    
    public Listener<?> getPropertySetter(K key);
    public <T> Listener<T> getPropertySetter(Class<T> propertyClass, K key);
}

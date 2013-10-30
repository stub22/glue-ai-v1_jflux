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
import org.jflux.api.core.event.ValueChange.DefaultValueChange;
import org.jflux.api.core.util.DefaultNotifier;

/**
 *
 * @author Matthew Stevenson
 */
public class DefaultConfigProperty<V> implements ConfigProperty<V> {
    private V myValue;
    private Source<V> mySource;
    private Listener<V> myListener;
    private Notifier<ValueChange<V>> myNotifier;
    private Class<V> myValueClass;

    public DefaultConfigProperty(Class<V> valueClass, V value) {
        myValue = value;
        myValueClass = valueClass;
        myNotifier = new DefaultNotifier<ValueChange<V>>();
        myListener = new PropertySetter();
        mySource = new Source<V>() {
            @Override
            public V getValue() {
                return myValue;
            }
        };
    }

    @Override
    public Source<V> getSource(){
       return mySource; 
    }

    @Override
    public Notifier<ValueChange<V>> getNotifier(){
        return myNotifier;
    }

    @Override
    public Listener<V> getSetter() {
        return myListener;
    }

    @Override
    public Class<V> getPropertyClass(){
        return myValueClass;
    }

    private class PropertySetter implements Listener<V> {
        @Override
        public void handleEvent(V event) {
            V old = myValue;
            myValue = event;
            ValueChange<V> change = new DefaultValueChange<V>(old, event);
            myNotifier.notifyListeners(change);
        }
    }
}

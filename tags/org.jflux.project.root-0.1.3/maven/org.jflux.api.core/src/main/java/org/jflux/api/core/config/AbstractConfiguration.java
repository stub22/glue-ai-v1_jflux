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
 *
 * @author Matthew Stevenson
 */
public abstract class AbstractConfiguration<K> implements Configuration<K> {

    protected abstract <T> ConfigProperty<T> getConfigProperty(K key);

    @Override
    public boolean containsKey(K key) {
        return getConfigProperty(key) != null;
    }

    @Override
    public boolean containsKey(Class clazz, K key) {
        return getConfigProperty(clazz, key) != null;
    }
    
    protected <T> ConfigProperty<T> getConfigProperty(Class<T> propertyClass, K key){
        if(propertyClass == null || key == null){
            throw new NullPointerException();
        }
        ConfigProperty prop = getConfigProperty(key);
        Class actualClass = prop.getPropertyClass();
        if(actualClass == null || !propertyClass.isAssignableFrom(actualClass)){
            return null;
        }
        return prop;
    }
    
    @Override
    public <T> T getPropertyValue(K key) {
        Source<T> src = getPropertySource(key);
        return src == null ? null : src.getValue();
    }

    @Override
    public <T> T getPropertyValue(Class<T> propertyClass, K key) {
        Source<T> src = getPropertySource(propertyClass, key);
        return src == null ? null : src.getValue();
    }

    
    @Override
    public <T> Source<T> getPropertySource(K key) {
        ConfigProperty prop = getConfigProperty(key);
        if(prop == null){
            return null;
        }
        return prop.getSource();
    }

    @Override
    public <T> Source<T> getPropertySource(Class<T> propClass, K key) {
        ConfigProperty prop = getConfigProperty(key);
        if(prop == null){
            return null;
        }
        return prop.getSource();
    }

    @Override
    public <T> Notifier<ValueChange<T>> getPropertyNotifier(K key) {
        ConfigProperty prop = getConfigProperty(key);
        if(prop == null){
            return null;
        }
        return prop.getNotifier();
    }

    @Override
    public <T> Notifier<ValueChange<T>> getPropertyNotifier(
            Class<T> propClass, K key) {
        ConfigProperty prop = getConfigProperty(propClass, key);
        if(prop == null){
            return null;
        }
        return prop.getNotifier();
    }

    @Override
    public <T> Listener<T> getPropertySetter(K key) {
        ConfigProperty prop = getConfigProperty(key);
        if(prop == null){
            return null;
        }
        return prop.getSetter();
    }

    @Override
    public <T> Listener<T> getPropertySetter(Class<T> propClass, K key) {
        ConfigProperty prop = getConfigProperty(propClass, key);
        if(prop == null){
            return null;
        }
        return prop.getSetter();
    }

    @Override
    public <T> Class<T> getPropertyClass(K key) {
        ConfigProperty prop = getConfigProperty(key);
        if(prop == null){
            return null;
        }
        return prop.getPropertyClass();
    }
}

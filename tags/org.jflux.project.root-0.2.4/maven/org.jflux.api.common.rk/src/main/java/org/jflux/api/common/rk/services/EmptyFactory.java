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

package org.jflux.api.common.rk.services;

import org.jflux.api.common.rk.config.VersionProperty;

/**
 *
 * @author Matthew Stevenson <www.jflux.org>
 */
public class EmptyFactory<T> implements ServiceFactory<T, T> {
    private Class<T> myServiceClass;
    private VersionProperty myServiceVersion;
    
    public EmptyFactory(Class<T> clazz, VersionProperty serviceVersion){
        myServiceClass = clazz;
        myServiceVersion = serviceVersion;
    }
    
    @Override
    public VersionProperty getServiceVersion() {
        return myServiceVersion;
    }

    @Override
    public T build(T config) {
        return config;
    }

    @Override
    public Class<T> getServiceClass() {
        return myServiceClass;
    }

    @Override
    public Class<T> getConfigurationClass() {
        return myServiceClass;
    }
    
}

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
package org.jflux.api.common.rk.osgi.lifecycle;

import org.jflux.api.common.rk.config.VersionProperty;

/**
 *
 * @author Matthew Stevenson <www.jflux.org>
 */
public class ConfiguredServiceParams<T,C,P> {
    private VersionProperty myServiceVersion;
    private VersionProperty myConfigFormat;
    private C myConfig;
    private P myParam;
    private String myParamId;
    private Class<T> myServiceClass;
    private Class<C> myConfigClass;
    private Class<P> myParamClass;
    
    public ConfiguredServiceParams(
            Class<T> serviceClass, Class<C> configClass, 
            Class<P> paramClass, C config, P param, String paramId,
            VersionProperty serviceVersion, VersionProperty configFormat){
        if(serviceClass == null|| 
                serviceVersion == null || configFormat == null){
            throw new NullPointerException();
        }
        myServiceVersion = serviceVersion;
        myConfigFormat = configFormat;
        myConfig = config;
        myParam = param;
        myParamId = paramId;
        myServiceClass = serviceClass;
        myConfigClass = configClass;
        myParamClass = paramClass;
        if(myConfig == null && myParamClass == null){
            if(myParam == null){
                throw new NullPointerException();
            }else{
                myParamClass = (Class<P>)myParam.getClass();
            }
        }
    }
    
    public VersionProperty getServiceVersion(){
        return myServiceVersion;
    }
    public VersionProperty getConfigFormat(){
        return myConfigFormat;
    }
    public C getConfig(){
        return myConfig;
    }
    public P getParam(){
        return myParam;
    }
    public String getParamId(){
        return myParamId;
    }
    public Class<T> getServiceClass(){
        return myServiceClass;
    }
    public Class<C> getConfigClass(){
        return myConfigClass;
    }
    public Class<P> getParamClass(){
        return myParamClass;
    }
}

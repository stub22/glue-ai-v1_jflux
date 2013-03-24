/*
 * Copyright 2013 The JFlux Project (www.jflux.org).
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
package org.jflux.api.service.binding;

import org.jflux.api.registry.Descriptor;
import org.jflux.api.service.DependencySpec;
import org.jflux.api.service.DependencySpec.UpdateStrategy;

/**
 *
 * @author matt
 */
public class BindingSpec {
    public static enum BindingStrategy {EAGER, LAZY}
    
    private final String myName;
    private final DependencySpec mySpec;
    private final Descriptor myDescriptor;
    private final BindingStrategy myBindingStrategy;
    private final UpdateStrategy myUpdateStrategy;
    
    public BindingSpec(DependencySpec spec, Descriptor desc, 
            BindingStrategy bindStrat, UpdateStrategy updateStrat){
        if(spec == null || desc == null || bindStrat == null){
            throw new NullPointerException();
        }
        myName = spec.getDependencyName();
        mySpec = spec;
        myDescriptor = desc;
        myBindingStrategy = bindStrat;
        myUpdateStrategy = updateStrat == null 
                ? mySpec.getUpdateStrategy() : updateStrat;
    }
    
    public BindingSpec(
            DependencySpec spec, Descriptor desc, BindingStrategy bindStrat){
        this(spec, desc, bindStrat, spec.getUpdateStrategy());
    }
    
    public String getDependencyName(){
        return myName;
    }
    public DependencySpec getDependencySpec(){
        return mySpec;
    }
    public Descriptor getDescriptor(){
        return myDescriptor;
    }
    public BindingStrategy getBindingStrategy(){
        return myBindingStrategy;
    }
    public UpdateStrategy getUpdateStrategy(){
        return myUpdateStrategy;
    }
}

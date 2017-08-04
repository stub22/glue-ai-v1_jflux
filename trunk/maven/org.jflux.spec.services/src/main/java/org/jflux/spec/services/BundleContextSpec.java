/*
 *  Copyright 2012 by The Appdapter Project (www.appdapter.org).
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.jflux.spec.services;

import org.osgi.framework.BundleContext;
import org.appdapter.bind.rdf.jena.assembly.KnownComponentImpl;
import org.jflux.api.service.ServiceManager;
import org.osgi.framework.FrameworkUtil;

public class BundleContextSpec extends KnownComponentImpl {

    private BundleContext context;

    public BundleContextSpec() {
    }

    public void setContext(BundleContext context) {
        this.context = context;
    }

    public BundleContext getContext() {
        if(context == null){
            context = FrameworkUtil.getBundle(ServiceManager.class).getBundleContext();//.getBundles()[0].getBundleContext();
        }
        return this.context;
    }
}

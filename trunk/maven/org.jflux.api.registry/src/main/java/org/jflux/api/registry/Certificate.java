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
package org.jflux.api.registry.opt;

/**
 * A Certificate is produced when an item is added to a Registry.
 * A Certificate provides permissions for modifying or removing the 
 * registration.
 * 
 * @author Matthew Stevenson
 */
public interface Certificate<Ref> {
    /**
     * Gets the reference to the service the certificate is for.
     * @return the reference
     */
    public Ref getReference();
}

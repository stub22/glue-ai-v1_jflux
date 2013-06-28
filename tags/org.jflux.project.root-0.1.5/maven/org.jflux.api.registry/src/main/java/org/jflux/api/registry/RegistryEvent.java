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
package org.jflux.api.registry;

/**
 *
 * @author matt
 */
public interface RegistryEvent {
    public static final int REGISTERED = 1;
    public static final int MODIFIED = 2;
    public static final int UNREGISTERING = 4;
    public static final int MODIFIED_ENDMATCH = 8;
    public int getEventType();
    public Reference getReference();
}

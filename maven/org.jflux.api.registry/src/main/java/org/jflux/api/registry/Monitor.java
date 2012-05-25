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
package org.jflux.api.registry;

import org.jflux.api.core.Notifier;

/**
 * Provides notifications of registry changes.
 *
 * @param <Desc> Descriptor for filtering events
 * @param <Evt> Event type used
 * 
 * @author Matthew Stevenson
 */
public interface Monitor<Desc, Evt> {
    /**
     * Returns a Notifier which filters registry events.
     * @param desc event filter
     * @return Notifier which filters registry events
     */
    public Notifier<Evt> getNotifier(Desc desc);
    /**
     * Stops the notifier for the given descriptor.
     * @param desc event filter
     */
    public void releaseNotifier(Desc desc);
}

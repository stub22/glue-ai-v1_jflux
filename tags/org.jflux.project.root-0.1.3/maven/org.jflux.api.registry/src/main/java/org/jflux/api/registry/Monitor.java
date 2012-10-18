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

import org.jflux.api.core.Listener;
import org.jflux.api.core.playable.PlayableNotifier;

/**
 * Provides notifications of registry changes.
 *
 * @param <Desc> Descriptor for filtering events
 * @param <Evt> Event type used
 * @param <N> Notifier for delivering events
 * 
 * @author Matthew Stevenson
 */
public interface Monitor<Desc, Evt, N extends PlayableNotifier<Evt>> {
    /**
     * Returns a Notifier which filters registry events.
     * @param desc event filter
     * @return Notifier which filters registry events
     */
    public N getNotifier(Desc desc);
    /**
     * Returns a Listener for completely releasing a notifier.
     * @return Listener for completely releasing a notifier
     */
    public Listener<N> releaseNotifier();
}

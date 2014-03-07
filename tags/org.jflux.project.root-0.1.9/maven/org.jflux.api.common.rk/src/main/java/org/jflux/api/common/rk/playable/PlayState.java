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

package org.jflux.api.common.rk.playable;

/**
 * States of a Playable.
 * 
 * @author Matthew Stevenson <www.jflux.org>
 */
public enum PlayState {
    /**
     * A Playable which has not been started.
     */
    PENDING,
    /**
     * A Playable which is currently running.
     */
    RUNNING,
    /**
     * A Playable which has been paused.
     */
    PAUSED,
    /**
     * A Playable which has been stopped before completion.
     */
    STOPPED,
    /**
     * A Playable which is completed and no longer running.
     */
    COMPLETED
}

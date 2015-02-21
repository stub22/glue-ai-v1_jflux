/*
 *  Copyright 2012 by The JFlux Project (www.jflux.org).
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
package org.jflux.api.core.playable;

/**
 * Simple implementation of Playable
 * @author Matthew Stevenson <www.jflux.org>
 */
public class BasicPlayable implements Playable{
    private PlayState myPlayState;
    
    /**
     * Create a new BasicPlayable in INITIALIZING state
     */
    public BasicPlayable(){
        myPlayState = PlayState.INITIALIZING;
    }
    
    /**
     * Starts the object
     * @return success
     */
    @Override
    public boolean start() {
        myPlayState = PlayState.RUNNING;
        return true;
    }

    /**
     * Pauses the object
     * @return success
     */
    @Override
    public boolean pause() {
        myPlayState = PlayState.PAUSED;
        return true;
    }

    /**
     * Resumes the object
     * @return success
     */
    @Override
    public boolean resume() {
        myPlayState = PlayState.RUNNING;
        return true;
    }

    /**
     * Stops the object
     * @return success
     */
    @Override
    public boolean stop() {
        myPlayState = PlayState.ABORTED;
        return true;
    }
    
    /**
     * Complete's the object's operation
     * @return success
     */
    protected boolean complete() {
        myPlayState = PlayState.COMPLETED;
        return true;
    }

    /**
     * Gets the object's state
     * @return PlayState for the object
     */
    @Override
    public PlayState getPlayState() {
        return myPlayState;
    }
    
}

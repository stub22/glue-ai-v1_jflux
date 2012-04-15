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
package org.jflux.api.core.util.playable;

/**
 *
 * @author Matthew Stevenson <www.robokind.org>
 */
public class BasicPlayable implements Playable{
    private PlayState myPlayState;
    
    public BasicPlayable(){
        myPlayState = PlayState.INITIALIZING;
    }
    
    @Override
    public boolean start() {
        myPlayState = PlayState.RUNNING;
        return true;
    }

    @Override
    public boolean pause() {
        myPlayState = PlayState.PAUSED;
        return true;
    }

    @Override
    public boolean resume() {
        myPlayState = PlayState.RUNNING;
        return true;
    }

    @Override
    public boolean stop() {
        myPlayState = PlayState.ABORTED;
        return true;
    }

    @Override
    public PlayState getPlayState() {
        return myPlayState;
    }
    
}

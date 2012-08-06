/*
 * Copyright 2012 by The JFlux Project (www.jflux.org).
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
 *
 * @author Matthew Stevenson <www.robokind.org>
 */
public abstract class PlayableGroup implements Playable{
    private PlayState myPlayState;
    
    protected abstract Iterable<Playable> getPlayables();

    @Override
    public PlayState getPlayState() {
        return myPlayState;
    }
    
    protected void setPlayState(PlayState state){
        myPlayState = state;
    }

    @Override
    public boolean start() {
        boolean ret = true;
        for(Playable node : getPlayables()){
            if(!node.start()){
                ret = false;
            }
        }
        return setState(ret, PlayState.RUNNING);
    }

    @Override
    public boolean pause() {
        boolean ret = true;
        for(Playable node : getPlayables()){
            if(!node.pause()){
                ret = false;
            }
        }
        return setState(ret, PlayState.PAUSED);
    }

    @Override
    public boolean resume() {
        boolean ret = true;
        for(Playable node : getPlayables()){
            if(!node.resume()){
                ret = false;
            }
        }
        return setState(ret, PlayState.RUNNING);
    }

    @Override
    public boolean stop() {
        boolean ret = true;
        for(Playable node : getPlayables()){
            if(!node.stop()){
                ret = false;
            }
        }
        return setState(ret, PlayState.ABORTED);
    }

    private boolean setState(boolean ret, PlayState state) {
        if(ret){
            setPlayState(state);
        }else{
            setPlayState(PlayState.ERROR);
        }
        return ret;
    }
}
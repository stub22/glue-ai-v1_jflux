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

import java.util.ArrayList;
import java.util.List;

/**
 * A basic implementation of Playable.  Useful as a base class.
 * 
 * @author Matthew Stevenson <www.jflux.org>
 */
public abstract class AbstractPlayable implements Playable{
    /**
     * The current PlayState.
     */
    protected PlayState myPlayState;
    /**
     * The time given when starting.
     */
    protected Long myStartTime;
    /**
     * The time given when last being paused.
     */
    protected Long myPauseTime;
    /**
     * The time given when last being resumed.
     */
    protected Long myResumeTime;
    /**
     * The time given when last being stopped.
     */
    protected Long myStopTime;
    /**
     * Listeners for PlayState changes
     */
    protected List<PlayableListener> myPlayableListeners;

    /**
     * The total elapsed time while paused.
     */
    protected Long myElapsedPauseTime;

    /**
     * Creates a new Playable in a pending state.
     */
    public AbstractPlayable(){
        myElapsedPauseTime = 0L;
        myPlayState = PlayState.PENDING;
    }

    @Override
    public void addPlayableListener(PlayableListener listener) {
        if(myPlayableListeners == null){
            myPlayableListeners = new ArrayList<PlayableListener>();
        }
        if(!myPlayableListeners.contains(listener)){
            myPlayableListeners.add(listener);
        }
    }

    @Override
    public void removePlayableListener(PlayableListener listener) {
        if(myPlayableListeners == null){
            return;
        }
        myPlayableListeners.remove(listener);
    }

    @Override
    public PlayState getPlayState() {
        return myPlayState;
    }
    
    /**
     * Called when the Playable is started.
     * @param time time the Playable is started, usually current system time UTC
     * @return true if start is successful, false to cancel
     */
    protected abstract boolean onStart(long time);
    /**
     * Called when the Playable is paused
     * @param time time the Playable is paused, usually current system time UTC
     * @return true if pause is successful, false to cancel
     */
    protected abstract boolean onPause(long time);
    /**
     * Called when the Playable is resumed
     * @param time time the Playable is resumed, usually current system time UTC
     * @return true if resume is successful, false to cancel
     */
    protected abstract boolean onResume(long time);
    /**
     * Called when the Playable is stopped
     * @param time time the Playable is stopped, usually current system time UTC
     * @return true if stop is successful, false to cancel
     */
    protected abstract boolean onStop(long time);
    /**
     * Called when the Playable is completed
     * @param time time the Playable is completed, usually current system time 
     * UTC
     * @return true if completion is successful, false to cancel
     */
    protected abstract boolean onComplete(long time);

    @Override
    public boolean start(long time) {
        if(myPlayState == PlayState.RUNNING){
            return false;
        }
        if(!onStart(time)){
            return false;
        }
        myStartTime = time;
        myElapsedPauseTime = 0L;
        changeState(PlayState.RUNNING, time);
        return true;
    }

    @Override
    public boolean pause(long time) {
        if(myPlayState != PlayState.RUNNING){
            return false;
        }
        if(!onPause(time)){
            return false;
        }
        myPauseTime = time;
        changeState(PlayState.PAUSED, time);
        return true;
    }
    
    @Override
    public boolean complete(long time){
        if(myPlayState == PlayState.COMPLETED){
            return true;
        }
        if(!onComplete(time)){
            return false;
        }
        if(myPlayState == PlayState.PAUSED){
            long elapsed = time - myPauseTime;
            myElapsedPauseTime += elapsed;
        }
        myStopTime = time;
        changeState(PlayState.COMPLETED, time);
        afterComplete(time);
        return true;
    }
    /**
     * Called after the Playable is marked Completed.
     * @param time time of completion
     */
    protected void afterComplete(long time){}

    @Override
    public boolean resume(long time) {
        if(myPlayState != PlayState.PAUSED){
            return false;
        }
        if(!onResume(time)){
            return false;
        }
        myResumeTime = time;
        long elapsed = time - myPauseTime;
        myElapsedPauseTime += elapsed;
        changeState(PlayState.RUNNING, time);
        return true;
    }

    @Override
    public boolean stop(long time) {
        if(myPlayState == PlayState.STOPPED || myPlayState == PlayState.COMPLETED){
            return false;
        }
        if(!onStop(time)){
            return false;
        }
        changeState(PlayState.STOPPED, time);
        return true;
    }

    @Override
    public Long getStartTime() {
        return myStartTime;
    }

    @Override
    public Long getPauseTime() {
        return myPauseTime;
    }

    @Override
    public Long getResumeTime() {
        return myResumeTime;
    }

    @Override
    public Long getStopTime() {
        return myStopTime;
    }

    @Override
    public Long getElapsedPlayTime(long time){
        if(myStartTime == null){
            return 0L;
        }
        if(myPlayState == PlayState.RUNNING){
            return time - (myStartTime + myElapsedPauseTime);
        }else if(myPlayState == PlayState.PAUSED){
            long ret = time - (myStartTime + myElapsedPauseTime);
            if(myPauseTime != null){
                ret -= time - myPauseTime;
            }
            return ret;
        }else if(myPlayState == PlayState.COMPLETED){
            return myStopTime - (myStartTime + myElapsedPauseTime);
        }else if(myPlayState == PlayState.STOPPED){
            return myStopTime - (myStartTime + myElapsedPauseTime);
        }else if(myPlayState == PlayState.PENDING){
            return 0L;
        }
        return 0L;
    }

    @Override
    public Long getElapsedPauseTime(long time){
        return myElapsedPauseTime;
    }

    private void changeState(PlayState state, long time){
        PlayState prev = myPlayState;
        myStopTime = time;
        myPlayState = state;
        firePlayStateChanged(prev, myPlayState, myStopTime);
    }

    /**
     * Notifies listeners of a change in PlayState.
     * @param prev previous PlayState
     * @param state new PlayState
     * @param time PlayState change time
     */
    protected void firePlayStateChanged(PlayState prev, PlayState state, long time){
        if(myPlayableListeners == null){
            return;
        }
        for(PlayableListener listener : myPlayableListeners){
            listener.playStateChanged(prev, state, time);
        }
    }
}

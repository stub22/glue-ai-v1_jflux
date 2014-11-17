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
 * An interface defining standard play controls: start, pause, resume, and stop.
 * 
 * @author Matthew Stevenson <www.jflux.org>
 */
public interface Playable {
    /**
     * Adds a PlayableListener to be notified when the PlayState changes.
     * @param listener PlayableListener to be added
     */
    public void addPlayableListener(PlayableListener listener);
    /**
     * Removes a PlayableListener from being notified
     * @param listener PlayableListener to remove
     */
    public void removePlayableListener(PlayableListener listener);

    /**
     * Return the current PlayState of the Playable.
     * @return the current PlayState of the Playable
     */
    public PlayState getPlayState();
    
    /**
     * Starts the Playable from its initial state.  If successful, the resulting
     * PlayState is RUNNING.
     * @param time when the Playable is started (used to synchronize actions)
     * the time is usually the current system time UTC.
     * @return true if successful
     */
    public boolean start(long time);
    /**
     * Pauses the Playable in its current state.  If successful, the resulting 
     * PlayState is PAUSED.
     * @param time when the Playable is paused (used to synchronize actions)
     * @return true if successful
     */
    public boolean pause(long time);
    /**
     * Resumes a paused Playable.  If successful, the resulting 
     * PlayState is RUNNING.
     * @param time when the Playable is resumed (used to synchronize actions)
     * @return true if successful
     */
    public boolean resume(long time);
    /**
     * Stops a Playable, returning to the initial state.  If successful, the resulting 
     * PlayState is STOPPED.
     * @param time when the Playable is stopped (used to synchronize actions)
     * @return true if successful
     */
    public boolean stop(long time);
    /**
     * Stops the Playable, marking it complete.  If successful, the resulting 
     * PlayState is COMPLETED.
     * @param time when the Playable is complete (used to synchronize actions)
     * @return true if successful
     */
    public boolean complete(long time);
    /**
     * Returns the time given when the Playable was started.  Returns null if the
     * Playable has not been started.
     * @return the time given when the Playable was started.  Returns null if the
     * Playable has not been started
     */
    public Long getStartTime();
    /**
     * Returns the time given when the Playable was last paused.  Returns null if 
     * the Playable has not been paused.
     * @return the time given when the Playable was last paused.  Returns null 
     * if the Playable has not been paused
     */
    public Long getPauseTime();
    /**
     * Returns the time given when the Playable was last resumed.  Returns null 
     * if the Playable has not been resumed.
     * @return the time given when the Playable was last resumed.  Returns null 
     * if the Playable has not been resumed
     */
    public Long getResumeTime();
    /**
     * Returns the time given when the Playable was last stopped.  Returns null 
     * if the Playable has not been stopped.
     * @return the time given when the Playable was last stopped.  Returns null 
     * if the Playable has not been stopped
     */
    public Long getStopTime();

    /**
     * Returns the elapsed time between the start time and the given time.
     * @param time the time to check
     * @return the elapsed time between the start time and the given time
     */
    public Long getElapsedPlayTime(long time);
    
    /**
     * Returns the elapsed time between the last pause time and the given time.
     * @param time the time to check
     * @return the elapsed time between the last pause time and the given time
     */
    public Long getElapsedPauseTime(long time);
}

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

package org.jflux.api.common.rk.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract class for performing an action at regular intervals.
 *
 * @author Matthew Stevenson <www.jflux.org>
 */
//TODO Uses ScheduledExecutor to handle TimerLoop
public abstract class TimerLoop {
	private static final Logger theLogger = LoggerFactory.getLogger(TimerLoop.class);
	private long myInterval;
	private boolean myStop;

	/**
	 * Creates a new TimerLoop with the given interval in milliseconds.
	 *
	 * @param interval number of milliseconds between performing the action
	 */
	public TimerLoop(long interval) {
		myStop = true;
		myInterval = interval;
	}

	/**
	 * Sets the interval length.
	 *
	 * @param interval number of milliseconds between performing the action
	 */
	public void setIntervalLength(long interval) {
		myInterval = interval;
	}

	/**
	 * Returns the interval length.
	 *
	 * @return interval length
	 */
	public long getIntervalLength() {
		return myInterval;
	}

	/**
	 * Action to perform at regular intervals.
	 *
	 * @param time     current time
	 * @param interval length of the timer interval
	 */
	protected abstract void timerTick(long time, long interval);

	/**
	 * Start performing the action at intervals.
	 */
	public void start() {
		if (!myStop) {
			return;
		}
		myStop = false;
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (!myStop) {
					timerStep();
				}
			}
		}).start();
	}

	/**
	 * Stop the timer.
	 */
	public void stop() {
		myStop = true;
	}

	private void timerStep() {
		long start = TimeUtils.now();
		timerTick(start, myInterval);
		long elapsed = TimeUtils.now() - start;
		long sleep = myInterval - elapsed;
		if (sleep >= 0) {
			TimeUtils.sleep(sleep);
		}
	}
}

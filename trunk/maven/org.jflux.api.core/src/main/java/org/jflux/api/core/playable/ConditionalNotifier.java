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

import org.jflux.api.core.Listener;
import org.jflux.api.core.Notifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Notifier that only works if a Playable is running
 *
 * @param <E> input data type
 * @author Matthew Stevenson <www.jflux.org>
 */
public class ConditionalNotifier<E> implements Notifier<E> {
	private static final Logger theLogger = LoggerFactory.getLogger(ConditionalNotifier.class);
	private Playable myPlayable;
	private Notifier<E> myNotifier;

	/**
	 * Builds a ConditionalNotifier from a Playable and a Notifier
	 *
	 * @param p the Playable
	 * @param n the Notifier
	 */
	public ConditionalNotifier(Playable p, Notifier<E> n) {
		if (p == null || n == null) {
			throw new NullPointerException();
		}
		myPlayable = p;
		myNotifier = n;
	}

	/**
	 * Notifies Listeners if and only if in RUNNING state
	 *
	 * @param e event data to send
	 */
	@Override
	public void notifyListeners(E e) {
		if (myPlayable.getPlayState() == Playable.PlayState.RUNNING) {
			myNotifier.notifyListeners(e);
		} else {
			theLogger.info("PlayState is {} for Playable: {}, \nIgnoring event: {}",
					myPlayable.getPlayState(), myPlayable.toString(), e);
		}
	}

	/**
	 * Adds a Listener
	 *
	 * @param listener Listener to add
	 */
	@Override
	public void addListener(Listener<E> listener) {
		myNotifier.addListener(listener);
	}

	/**
	 * Removes a Listener
	 *
	 * @param listener Listener to remove
	 */
	@Override
	public void removeListener(Listener<E> listener) {
		myNotifier.removeListener(listener);
	}
}

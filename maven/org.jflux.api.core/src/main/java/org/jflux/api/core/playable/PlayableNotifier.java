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
package org.jflux.api.core.playable;

import org.jflux.api.core.Listener;
import org.jflux.api.core.Notifier;
import org.jflux.api.core.util.DefaultNotifier;
import org.slf4j.LoggerFactory;

/**
 * Notifier whose operation can be controlled as a Playable
 *
 * @param <T> input data type
 * @author Matthew Stevenson
 */
public interface PlayableNotifier<T> extends Notifier<T>, Playable {

	/**
	 * Basic implementation of a PlayableNotifier
	 *
	 * @param <T> input data type
	 */
	public static class DefaultPlayableNotifier<T> extends
			BasicPlayable implements PlayableNotifier<T> {
		private static final org.slf4j.Logger theLogger = LoggerFactory.getLogger(DefaultPlayableNotifier.class);

		private Notifier<T> myNotifier;

		/**
		 * Builds a DefaultPlayableNotifier from a regular Notifier
		 *
		 * @param notifier base Notifier
		 */
		public DefaultPlayableNotifier(Notifier<T> notifier) {
			if (notifier == null) {
				throw new NullPointerException();
			}
			myNotifier = notifier;
		}

		/**
		 * Builds a fresh DefaultPlayableNotifier
		 */
		public DefaultPlayableNotifier() {
			myNotifier = new DefaultNotifier<>();
		}

		/**
		 * Gets the unmanaged Notifier
		 *
		 * @return unmanaged Notifier
		 */
		public Notifier<T> getNotifier() {
			return myNotifier;
		}

		/**
		 * Adds a Listener
		 *
		 * @param listener Listener to add
		 */
		@Override
		public void addListener(Listener<T> listener) {
			myNotifier.addListener(listener);
		}

		/**
		 * Removes a Listener
		 *
		 * @param listener Listener to remove
		 */
		@Override
		public void removeListener(Listener<T> listener) {
			myNotifier.removeListener(listener);
		}

		/**
		 * Notify Listeners if RUNNING
		 *
		 * @param e input data event
		 */
		@Override
		public void notifyListeners(T e) {
			if (getPlayState() == Playable.PlayState.RUNNING) {
				myNotifier.notifyListeners(e);
			} else {
				theLogger.info("PlayState is: {}, ignoring event: {}",
						getPlayState(), e);
			}
			myNotifier.notifyListeners(e);
		}
	}
}

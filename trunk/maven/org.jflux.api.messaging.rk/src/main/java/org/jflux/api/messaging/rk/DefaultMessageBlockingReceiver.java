/*
 * Copyright 2011 Hanson Robokind LLC.
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
package org.jflux.api.messaging.rk;

import org.jflux.api.core.Adapter;
import org.jflux.api.core.Listener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Default implementation of the MessageBlockingReceiver.
 * Blocks until a Record is received.  Uses an Adapter to adapt Records to
 * Messages and notifies Listeners.
 *
 * @param <Msg> type of message to return
 * @param <Rec> type of record to receive
 * @author Matthew Stevenson <www.robokind.org>
 */
public class DefaultMessageBlockingReceiver<Msg, Rec>
		implements MessageBlockingReceiver<Msg> {
	private static final Logger theLogger = LoggerFactory.getLogger(DefaultMessageBlockingReceiver.class);
	private RecordBlockingReceiver<Rec> myRecordReceiver;
	private Adapter<Rec, Msg> myAdapter;
	private List<Listener<Msg>> myListeners;
	private long myTimeout;

	/**
	 * Creates an empty DefaultMessageBlockingReceiver.
	 */
	public DefaultMessageBlockingReceiver() {
		myTimeout = MessageBlockingReceiver.DEFAULT_TIMEOUT_LENGTH;
		myListeners = new ArrayList<>();
	}

	/**
	 * Creates an empty DefaultMessageBlockingReceiver.
	 *
	 * @param timeout timeout length for receiving messages
	 */
	public DefaultMessageBlockingReceiver(long timeout) {
		myTimeout = timeout;
		myListeners = new ArrayList<>();
	}

	/**
	 * Sets the RecordAsyncReceiver used to receive Records.
	 *
	 * @param receiver theRecordReceiver to set
	 */
	public void setRecordReceiver(RecordBlockingReceiver<Rec> receiver) {
		myRecordReceiver = receiver;
	}

	/**
	 * Sets the Adapter used to convert Records to Messages.
	 *
	 * @param adapter the Adapter to set
	 */
	public void setAdapter(Adapter<Rec, Msg> adapter) {
		myAdapter = adapter;
	}

	@Override
	public void setTimeout(long timeout) {
		if (timeout < 0) {
			throw new IllegalArgumentException("timeout must be positive.");
		}
		myTimeout = timeout;
	}

	@Override
	public long getTimeout() {
		return myTimeout;
	}

	@Override
	public void start() {
		if (myRecordReceiver == null) {
			theLogger.warn("No Record PollingService, unable to receive.");
			return;
		} else if (myAdapter == null) {
			theLogger.warn("No Record Adapter, unable to send receive.");
			return;
		}
	}

	@Override
	public void stop() {
	}

	@Override
	public Msg getValue() {
		if (myRecordReceiver == null || myAdapter == null) {
			throw new NullPointerException();
		}
		Rec rec = myRecordReceiver.fetchRecord(myTimeout);
		if (rec == null) {
			return null;
		}
		Msg msg = myAdapter.adapt(rec);
		if (msg != null) {
			fireMessageEvent(msg);
		}
		return msg;
	}

	/**
	 * Notifies listeners of a Message
	 */
	protected void fireMessageEvent(Msg message) {
		for (Listener<Msg> listener : myListeners) {
			listener.handleEvent(message);
		}
	}

	@Override
	public int clearMessages() {
		return myRecordReceiver.clearRecords();
	}

	@Override
	public void addMessageListener(Listener<Msg> listener) {
		if (listener == null) {
			return;
		}
		if (!myListeners.contains(listener)) {
			myListeners.add(listener);
		}
	}

	@Override
	public void removeMessageListener(Listener<Msg> listener) {
		if (listener == null) {
			return;
		}
		myListeners.remove(listener);
	}
}

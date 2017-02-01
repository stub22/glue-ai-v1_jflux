/*
 * Copyright 2012 Hanson Robokind LLC.
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
package org.jflux.impl.messaging.rk;

import org.jflux.api.messaging.rk.RecordSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.BytesMessage;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;

/**
 * @author Matthew Stevenson <www.robokind.org>
 */
public class JMSBytesMessageSender implements RecordSender<BytesMessage> {
	private static final Logger theLogger = LoggerFactory.getLogger(JMSBytesMessageSender.class);
	private Session mySession;
	private Destination myDestination;
	private MessageProducer myMessageProducer;

	/**
	 * Creates an empty JMSBytesSender.
	 */
	public JMSBytesMessageSender() {
	}

	/**
	 * Sets the JMS Session to use for sending
	 */
	public void setSession(Session session) {
		mySession = session;
	}

	/**
	 * Sets the JMS Destination to send to.
	 *
	 * @param dest JMS Destination to send to
	 */
	public void setDestination(Destination dest) {
		myDestination = dest;
	}

	/**
	 * Closes the underlying JMS MessageProducer making it unable to send
	 * Records.
	 */
	public void closeProducer() {
		if (myMessageProducer == null) {
			return;
		}
		try {
			myMessageProducer.close();
		} catch (JMSException ex) {
			theLogger.warn("Error closing MessageProducer.", ex);
		}
	}

	/**
	 * Opens the underlying JMS MessageProducer, allowing it to begin sending
	 * Records.  This must be called before Records can be sent.
	 */
	public void openProducer() {
		if (mySession == null || myDestination == null) {
			return;
		}
		if (myMessageProducer != null) {
			theLogger.warn("Cannot open MessageProducer.  "
					+ "MessageProducer already open.");
			return;
		}
		try {
			myMessageProducer = mySession.createProducer(myDestination);
		} catch (JMSException ex) {
			theLogger.warn("Error opening MessageProducer.", ex);
		}
	}

	BytesMessage createBytesMessage() {
		if (mySession == null) {
			return null;
		}
		try {
			return mySession.createBytesMessage();
		} catch (JMSException ex) {
			return null;
		}
	}

	/**
	 * Sends a BytesMessage
	 *
	 * @param message byte array to send from
	 */
	@Override
	public void sendRecord(BytesMessage message) {
		if (message == null) {
			throw new NullPointerException();
		}
		if (myMessageProducer == null) {
			theLogger.warn("Not connected, unable to send message.");
			return;
		}
		try {
			myMessageProducer.send(message);
		} catch (Exception ex) {
			theLogger.warn("Error sending message.", ex);
		}
	}
}

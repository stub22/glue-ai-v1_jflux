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
package org.jflux.impl.messaging.rk.lifecycle;

import org.jflux.api.core.Adapter;
import org.jflux.api.messaging.rk.Constants;
import org.jflux.api.messaging.rk.DefaultMessageBlockingReceiver;
import org.jflux.api.messaging.rk.MessageBlockingReceiver;
import org.jflux.impl.messaging.rk.JMSBytesRecordBlockingReceiver;
import org.jflux.impl.messaging.rk.utils.ConnectionManager;
import org.jflux.impl.services.rk.lifecycle.AbstractLifecycleProvider;
import org.jflux.impl.services.rk.lifecycle.utils.DescriptorListBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Properties;

import javax.jms.BytesMessage;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Session;

/**
 * @author Matthew Stevenson <www.robokind.org>
 */
public class BytesMessageBlockingReceiverLifecycle<Msg>
		extends AbstractLifecycleProvider<
		MessageBlockingReceiver,
		DefaultMessageBlockingReceiver<Msg, BytesMessage>> {
	private static final Logger theLogger = LoggerFactory.getLogger(BytesMessageBlockingReceiverLifecycle.class);
	private final static String theSession = "session";
	private final static String theDestination = "destination";

	private Adapter<BytesMessage, Msg> myAdapter;

	public BytesMessageBlockingReceiverLifecycle(Adapter<BytesMessage, Msg> adapter,
												 Class<Msg> messageClass, String receiverId,
												 String sessionId, String destinationId) {
		super(new DescriptorListBuilder()
				.dependency(theSession, Session.class)
				.with(ConnectionManager.PROP_CONNECTION_ID, sessionId)
				.dependency(theDestination, Destination.class)
				.with(ConnectionManager.PROP_DESTINATION_ID, destinationId)
				.getDescriptors());
		if (adapter == null) {
			throw new NullPointerException();
		}
		myAdapter = adapter;
		if (myRegistrationProperties == null) {
			myRegistrationProperties = new Properties();
		}
		myRegistrationProperties.put(
				Constants.PROP_MESSAGE_TYPE, messageClass.getName());
		myRegistrationProperties.put(
				Constants.PROP_RECORD_TYPE, BytesMessage.class.getName());
		myRegistrationProperties.put(Constants.PROP_MESSAGE_RECEIVER_ID,
				receiverId);
	}

	@Override
	protected DefaultMessageBlockingReceiver<Msg, BytesMessage> create(
			Map<String, Object> dependencies) {
		Session session = (Session) dependencies.get(theSession);
		Destination destination = (Destination) dependencies.get(theDestination);
		MessageConsumer consumer;
		try {
			consumer = session.createConsumer(destination);
		} catch (JMSException ex) {
			theLogger.warn("Error creating JMS Consumer.", ex);
			return null;
		}
		DefaultMessageBlockingReceiver<Msg, BytesMessage> receiver =
				new DefaultMessageBlockingReceiver<>();

		JMSBytesRecordBlockingReceiver recReceiver =
				new JMSBytesRecordBlockingReceiver(consumer);
		receiver.setRecordReceiver(recReceiver);
		receiver.setAdapter(myAdapter);
		return receiver;
	}

	@Override
	protected void handleChange(String dependencyId, Object service,
								Map<String, Object> dependencies) {
		if (myService == null) {
			if (isSatisfied()) {
				myService = create(dependencies);
			}
			return;
		}
		myService.stop();
		if (isSatisfied()) {
			myService = create(dependencies);
		} else {
			myService = null;
		}
	}

	@Override
	public Class<MessageBlockingReceiver> getServiceClass() {
		return MessageBlockingReceiver.class;
	}
}

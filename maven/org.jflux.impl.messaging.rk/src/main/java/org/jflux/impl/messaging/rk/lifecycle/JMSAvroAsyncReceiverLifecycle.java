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

import org.apache.avro.Schema;
import org.apache.avro.generic.IndexedRecord;
import org.jflux.api.core.Adapter;
import org.jflux.api.messaging.rk.Constants;
import org.jflux.api.messaging.rk.DefaultMessageAsyncReceiver;
import org.jflux.api.messaging.rk.MessageAsyncReceiver;
import org.jflux.impl.messaging.rk.JMSAvroRecordAsyncReceiver;
import org.jflux.impl.messaging.rk.utils.ConnectionManager;
import org.jflux.impl.services.rk.lifecycle.AbstractLifecycleProvider;
import org.jflux.impl.services.rk.lifecycle.utils.DescriptorListBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Properties;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Session;

/**
 * @author Matthew Stevenson <www.robokind.org>
 */
public class JMSAvroAsyncReceiverLifecycle<Msg, Rec extends IndexedRecord>
		extends AbstractLifecycleProvider<
		MessageAsyncReceiver,
		DefaultMessageAsyncReceiver<Msg, Rec>> {
	private static final Logger theLogger = LoggerFactory.getLogger(JMSAvroAsyncReceiverLifecycle.class);
	private final static String theSession = "session";
	private final static String theDestination = "destination";

	private Adapter<Rec, Msg> myAdapter;
	private Class<Rec> myRecordClass;
	private Schema myRecordSchema;

	/**
	 * Asynchronously receives Avro Records over JMS and adapts them to a
	 * Message.
	 *
	 * @param adapter       adapts Records to Messages
	 * @param messageClass  class of the Message being received
	 * @param recordClass   class of the Record being received and adapted
	 * @param recordSchema  record avro schema
	 * @param receiverId    id to use for this message receiver
	 * @param sessionId     id of the Connection dependency
	 * @param destinationId id of the destination dependency
	 */
	public JMSAvroAsyncReceiverLifecycle(Adapter<Rec, Msg> adapter,
										 Class<Msg> messageClass, Class<Rec> recordClass,
										 Schema recordSchema, String receiverId,
										 String sessionId, String destinationId) {
		super(new DescriptorListBuilder()
				.dependency(theSession, Session.class)
				.with(ConnectionManager.PROP_CONNECTION_ID, sessionId)
				.dependency(theDestination, Destination.class)
				.with(ConnectionManager.PROP_DESTINATION_ID, destinationId)
				.getDescriptors());
		if (adapter == null || messageClass == null ||
				recordClass == null || recordSchema == null) {
			throw new NullPointerException();
		}
		myAdapter = adapter;
		myRecordClass = recordClass;
		myRecordSchema = recordSchema;
		if (myRegistrationProperties == null) {
			myRegistrationProperties = new Properties();
		}
		myRegistrationProperties.put(
				Constants.PROP_MESSAGE_TYPE, messageClass.getName());
		myRegistrationProperties.put(
				Constants.PROP_RECORD_TYPE, recordClass.getName());
		myRegistrationProperties.put(Constants.PROP_MESSAGE_RECEIVER_ID,
				receiverId);
	}

	@Override
	protected DefaultMessageAsyncReceiver<Msg, Rec> create(
			Map<String, Object> dependencies) {
		Session session = (Session) dependencies.get(theSession);
		Destination dest = (Destination) dependencies.get(theDestination);
		MessageConsumer consumer;
		try {
			consumer = session.createConsumer(dest);
		} catch (JMSException ex) {
			theLogger.warn("Error creating JMS Consumer.", ex);
			return null;
		}
		DefaultMessageAsyncReceiver<Msg, Rec> receiver =
				new DefaultMessageAsyncReceiver<>();
		JMSAvroRecordAsyncReceiver<Rec> pollingService =
				new JMSAvroRecordAsyncReceiver<>(
						myRecordClass, myRecordSchema, consumer);
		receiver.setAdapter(myAdapter);
		receiver.setRecordReceiver(pollingService);
		try {
			receiver.start();
		} catch (Exception ex) {
			theLogger.warn("Error starting MessageReciever.", ex);
			return null;
		}
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
	public Class<MessageAsyncReceiver> getServiceClass() {
		return MessageAsyncReceiver.class;
	}
}

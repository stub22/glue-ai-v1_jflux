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
package org.jflux.impl.messaging.rk;

import org.apache.avro.Schema;
import org.apache.avro.generic.IndexedRecord;
import org.jflux.api.messaging.rk.DefaultMessageAsyncReceiver;
import org.jflux.api.messaging.rk.RecordAsyncReceiver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Session;

/**
 * A DefaultMessageAsyncReceiver for asynchronously receiving Avro Records over
 * JMS (Qpid).
 *
 * @param <Msg> type of Message to be handled and send to listeners
 * @param <Rec> type of Avro Record to receive
 * @author Matthew Stevenson <www.robokind.org>
 */
public class JMSAvroMessageAsyncReceiver<Msg, Rec extends IndexedRecord>
		extends DefaultMessageAsyncReceiver<Msg, Rec> {
	private static final Logger theLogger = LoggerFactory.getLogger(JMSAvroMessageAsyncReceiver.class);
	private Session mySession;
	private Destination myDestination;
	private Class<Rec> myRecordClass;
	private Schema myRecordSchema;

	/**
	 * Creates a new JMSAvroMessageAsyncReceiver
	 *
	 * @param session      the JMS Session to use for receiving
	 * @param destination  the JMS Destination to receive from
	 * @param recordClass  the Class of the Avro Record being received
	 * @param recordSchema the Avro Schema of the Record being received
	 */
	public JMSAvroMessageAsyncReceiver(Session session, Destination destination,
									   Class<Rec> recordClass, Schema recordSchema) {
		if (session == null || destination == null || recordClass == null) {
			throw new NullPointerException();
		}
		mySession = session;
		myDestination = destination;
		myRecordClass = recordClass;
		myRecordSchema = recordSchema;
	}

	/**
	 * Start the JMSAvroMessageAsyncReceiver receiving Message.
	 * Creates and starts a JMSAvroRecordAsyncReceiver to receive Records.
	 *
	 * @throws JMSException if there is an error creating a JMS MessageConsumer or starting the JMS
	 *                      Polling Service.
	 * @throws Exception    inherited throws statement, should not throw Exception
	 */
	@Override
	public void start() throws JMSException, Exception {
		MessageConsumer consumer = mySession.createConsumer(myDestination);
		RecordAsyncReceiver<Rec> pollingService =
				new JMSAvroRecordAsyncReceiver<>(
						myRecordClass, myRecordSchema, consumer);
		setRecordReceiver(pollingService);
		super.start();
	}
}

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

import org.apache.avro.generic.IndexedRecord;
import org.apache.qpid.client.message.JMSBytesMessage;
import org.jflux.api.messaging.rk.RecordSender;
import org.jflux.impl.messaging.rk.common.QpidUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.BytesMessage;

/**
 * Sends Avro Records over JMS (Qpid).
 *
 * @param <T> type of Avro Record to send
 * @author Matthew Stevenson <www.robokind.org>
 */
public class JMSAvroRecordSender<T extends IndexedRecord>
		implements RecordSender<T> {
	private static final Logger theLogger = LoggerFactory.getLogger(JMSAvroRecordSender.class);
	private JMSBytesMessageSender myBytesSender;

	/**
	 * Creates a new JMSAvroRecordSender.
	 *
	 * @param sender JMSBytesSender to send Records
	 */
	public JMSAvroRecordSender(JMSBytesMessageSender sender) {
		if (sender == null) {
			throw new NullPointerException();
		}
		myBytesSender = sender;
	}

	/**
	 * Serializes the given Avro Record to a byte array, packs it into a JMS
	 * BytesMessage, and sends it to the JMS Destination.
	 *
	 * @param record the Avro Record to send
	 */
	@Override
	public void sendRecord(T record) {
		sendRecord(record, null);
	}

	/**
	 * Packs the given Avro Record into a JMS BytesMessage and sends it.
	 *
	 * @param record      Record to send
	 * @param contentType optional content type for the JMS header
	 */
	public void sendRecord(T record, String contentType) {
		if (record == null) {
			throw new NullPointerException();
		}
		BytesMessage msg = myBytesSender.createBytesMessage();
		if (msg == null) {
			theLogger.warn("Error creating BytesMessage.");
			return;
		}
		try {
			QpidUtils.packAvroMessage(record, msg);
		} catch (Exception ex) {
			theLogger.warn("Error serializing record.", ex);
			return;
		}
		if (contentType != null) {
			((JMSBytesMessage) msg).setContentType(contentType);
			((JMSBytesMessage) msg).setEncoding(contentType);
		}
		myBytesSender.sendRecord(msg);
	}
}

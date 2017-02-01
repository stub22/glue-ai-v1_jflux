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
package org.jflux.impl.messaging.rk.utils;

import org.apache.avro.Schema;
import org.apache.avro.generic.IndexedRecord;
import org.apache.qpid.client.message.JMSBytesMessage;
import org.jflux.api.core.Adapter;
import org.jflux.impl.messaging.rk.common.ComplexAdapter;
import org.jflux.impl.messaging.rk.common.PolymorphicAdapter;
import org.jflux.impl.messaging.rk.common.PolymorphicAdapter.AdapterKeyMap;
import org.jflux.impl.messaging.rk.common.QpidUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.BytesMessage;

/**
 * @author Matthew Stevenson <www.robokind.org>
 */
public class JMSAvroPolymorphicBytesRecordAdapter<Msg> implements Adapter<BytesMessage, Msg> {
	private static final Logger theLogger = LoggerFactory.getLogger(JMSAvroPolymorphicBytesRecordAdapter.class);
	private PolymorphicAdapter<BytesMessage, Msg> myAdapter;

	public JMSAvroPolymorphicBytesRecordAdapter() {
		myAdapter = new PolymorphicAdapter(new ContentTypeKeyMap());
	}

	public <R extends IndexedRecord> void addAdapter(Class<R> recordClass,
													 Schema recordSchema, Adapter<R, Msg> adapter, String contentType) {

		JMSAvroBytesRecordAdapter<R> bytesAdapter =
				new JMSAvroBytesRecordAdapter<>(recordClass, recordSchema);
		ComplexAdapter<BytesMessage, R, Msg> complexAdapter =
				new ComplexAdapter<>(bytesAdapter, adapter);
		myAdapter.addAdapter(contentType, complexAdapter);
	}

	@Override
	public Msg adapt(BytesMessage a) {
		return myAdapter.adapt(a);
	}

	private class ContentTypeKeyMap implements AdapterKeyMap<BytesMessage> {
		@Override
		public String getKey(BytesMessage t) {
			return ((JMSBytesMessage) t).getEncoding();
		}
	}

	public class JMSAvroBytesRecordAdapter<Rec extends IndexedRecord> implements
			Adapter<BytesMessage, Rec> {
		private Class<Rec> myRecordClass;
		private Schema myRecordSchema;

		public JMSAvroBytesRecordAdapter(Class<Rec> recordClass, Schema schema) {
			if (recordClass == null || schema == null) {
				throw new NullPointerException();
			}
			myRecordClass = recordClass;
			myRecordSchema = schema;
		}

		@Override
		public Rec adapt(BytesMessage a) {
			try {
				return QpidUtils.unpackAvroMessage(
						myRecordClass, null, myRecordSchema, a);
			} catch (Exception ex) {
				theLogger.warn("There was an error upacking the message.", ex);
			}
			return null;
		}
	}
}

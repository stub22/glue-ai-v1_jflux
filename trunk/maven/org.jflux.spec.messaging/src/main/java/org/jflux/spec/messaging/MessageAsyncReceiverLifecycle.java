package org.jflux.spec.messaging;

import org.apache.avro.Schema;
import org.jflux.api.core.Adapter;
import org.jflux.api.core.config.Configuration;
import org.jflux.api.service.ServiceDependency;
import org.jflux.api.service.ServiceLifecycle;
import org.jflux.impl.messaging.rk.JMSAvroMessageAsyncReceiver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.jms.Destination;
import javax.jms.Session;

import static org.jflux.impl.encode.avro.SerializationConfigUtils.CONF_AVRO_RECORD_SCHEMA;
import static org.jflux.impl.encode.avro.SerializationConfigUtils.CONF_DECODING_ADAPTER;
import static org.jflux.impl.encode.avro.SerializationConfigUtils.CONF_OUTPUT_CLASS;

/**
 * @author Amy Jessica Book <jgpallack@gmail.com>
 */
public class MessageAsyncReceiverLifecycle
		implements ServiceLifecycle<JMSAvroMessageAsyncReceiver> {

	private static final Logger theLogger = LoggerFactory.getLogger(MessageAsyncReceiverLifecycle.class);
	//    private final static String theReceiverConfiguration =
//            "messageReceiverConfig";
//    private final static String theReceiverSession = "messageReceiverSession";
//    private final static String theReceiverDestination = "messageReceiverDest";
	private final static String theReceiverConfiguration = "msg_config_dep";
	private final static String theReceiverSession = "session_dep";
	private final static String theReceiverDestination = "destination_dep";


	private final static ServiceDependency[] theDependencyArray = {
			new ServiceDependency(
					theReceiverConfiguration, Configuration.class.getName(),
					ServiceDependency.Cardinality.MANDATORY_UNARY,
					ServiceDependency.UpdateStrategy.STATIC, Collections.EMPTY_MAP),
			new ServiceDependency(
					theReceiverSession, Session.class.getName(),
					ServiceDependency.Cardinality.MANDATORY_UNARY,
					ServiceDependency.UpdateStrategy.STATIC, Collections.EMPTY_MAP),
			new ServiceDependency(
					theReceiverDestination, Destination.class.getName(),
					ServiceDependency.Cardinality.MANDATORY_UNARY,
					ServiceDependency.UpdateStrategy.STATIC, Collections.EMPTY_MAP)
	};
	private final static String[] theClassNameArray = {
			JMSAvroMessageAsyncReceiver.class.getName()
	};

	public MessageAsyncReceiverLifecycle() {
	}

	@Override
	public List<ServiceDependency> getDependencySpecs() {
		return Arrays.asList(theDependencyArray);
	}

	@Override
	public JMSAvroMessageAsyncReceiver createService(
			Map<String, Object> dependencyMap) {
		Configuration config =
				(Configuration) dependencyMap.get(theReceiverConfiguration);
		Session session = (Session) dependencyMap.get(theReceiverSession);
		Destination dest =
				(Destination) dependencyMap.get(theReceiverDestination);

		Adapter adapter =
				(Adapter) config.getPropertyValue(CONF_DECODING_ADAPTER);
		Schema schema =
				(Schema) config.getPropertyValue(CONF_AVRO_RECORD_SCHEMA);
		Class recordClass = (Class) config.getPropertyValue(CONF_OUTPUT_CLASS);

		JMSAvroMessageAsyncReceiver receiver =
				new JMSAvroMessageAsyncReceiver(
						session, dest, recordClass, schema);
		receiver.setAdapter(adapter);

		try {
			receiver.start();
		} catch (Exception ex) {
			theLogger.warn("Error starting MessageReciever.", ex);
			return null;
		}

		return receiver;
	}

	@Override
	public JMSAvroMessageAsyncReceiver handleDependencyChange(
			JMSAvroMessageAsyncReceiver service, String changeType,
			String dependencyName, Object dependency,
			Map<String, Object> availableDependencies) {
		return null;
	}

	@Override
	public void disposeService(
			JMSAvroMessageAsyncReceiver service,
			Map<String, Object> availableDependencies) {
		if (service != null) {
			service.stop();
		}
	}

	@Override
	public String[] getServiceClassNames() {
		return theClassNameArray;
	}
}

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

	private static final Logger theLogger = LoggerFactory.getLogger(
			MessageAsyncReceiverLifecycle.class);

	private static final ServiceDependency theConfigurationDependency = new ServiceDependency(
			"msg_config_dep", Configuration.class.getName(),
			ServiceDependency.Cardinality.MANDATORY_UNARY,
			ServiceDependency.UpdateStrategy.STATIC, Collections.EMPTY_MAP);

	private static final ServiceDependency theSessionDependency = new ServiceDependency(
			"session_dep", Session.class.getName(),
			ServiceDependency.Cardinality.MANDATORY_UNARY,
			ServiceDependency.UpdateStrategy.STATIC, Collections.EMPTY_MAP);

	private static final ServiceDependency theDestinationDependency = new ServiceDependency(
			"destination_dep", Destination.class.getName(),
			ServiceDependency.Cardinality.MANDATORY_UNARY,
			ServiceDependency.UpdateStrategy.STATIC, Collections.EMPTY_MAP);

	private final static ServiceDependency[] theDependencyArray = {
			theConfigurationDependency,
			theSessionDependency,
			theDestinationDependency
	};
	private final static String[] theClassNameArray = {
			JMSAvroMessageAsyncReceiver.class.getName()
	};

	@Override
	public List<ServiceDependency> getDependencySpecs() {
		return Arrays.asList(theDependencyArray);
	}

	@Override
	public JMSAvroMessageAsyncReceiver createService(
			final Map<String, Object> dependencyMap) {
		final Configuration config = (Configuration) dependencyMap.get(theConfigurationDependency.getDependencyName());
		final Session session = (Session) dependencyMap.get(theSessionDependency.getDependencyName());
		final Destination dest = (Destination) dependencyMap.get(theDestinationDependency.getDependencyName());

		final Adapter adapter = (Adapter) config.getPropertyValue(CONF_DECODING_ADAPTER);
		final Schema schema = (Schema) config.getPropertyValue(CONF_AVRO_RECORD_SCHEMA);
		final Class recordClass = (Class) config.getPropertyValue(CONF_OUTPUT_CLASS);

		final JMSAvroMessageAsyncReceiver receiver = new JMSAvroMessageAsyncReceiver(
				session, dest, recordClass, schema);
		receiver.setAdapter(adapter);

		try {
			receiver.start();
		} catch (final Exception ex) {
			theLogger.error("Error starting MessageReceiver: {}", receiver, ex);
			return null;
		}

		return receiver;
	}

	@Override
	public JMSAvroMessageAsyncReceiver handleDependencyChange(
			final JMSAvroMessageAsyncReceiver service, final String changeType,
			final String dependencyName, final Object dependency,
			final Map<String, Object> availableDependencies) {
		return null;
	}

	@Override
	public void disposeService(
			final JMSAvroMessageAsyncReceiver service,
			final Map<String, Object> availableDependencies) {
		if (service != null) {
			service.stop();
		}
	}

	@Override
	public String[] getServiceClassNames() {
		return theClassNameArray;
	}
}

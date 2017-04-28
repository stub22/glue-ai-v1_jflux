package org.jflux.api.service.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jflux.api.registry.Descriptor;
import org.jflux.api.service.DefaultRegistrationStrategy;
import org.jflux.api.service.RegistrationStrategy;

/**
 *
 * @author matt
 */
public class RegistrationStrategyBuilder<T> {

	private final List<String> myClassNames = new ArrayList<>();
	private final Map<String, String> myRegistrationProperties = new HashMap<>();

	public RegistrationStrategyBuilder() {
	}

	public RegistrationStrategyBuilder<T> className(String className) {
		if (!myClassNames.contains(className)) {
			myClassNames.add(className);
		}
		return this;
	}

	public RegistrationStrategyBuilder<T> property(String key, String value) {
		myRegistrationProperties.put(key, value);
		return this;
	}
	
	
	public RegistrationStrategyBuilder<T> descriptor(Descriptor descriptor){
		for(String key : descriptor.getPropertyKeys()){
			property(key, descriptor.getProperty(key));
		}
		return className(descriptor.getClassName());
	}

	public RegistrationStrategy<T> getRegistrationStrategy() {
		return new DefaultRegistrationStrategy<>(
				myClassNames.toArray(new String[0]),
				myRegistrationProperties);
	}
	
	List<String> getClassNames(){
		return myClassNames;
	}

}

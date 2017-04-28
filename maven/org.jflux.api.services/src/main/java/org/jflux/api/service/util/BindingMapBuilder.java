
package org.jflux.api.service.util;

import java.util.HashMap;
import java.util.Map;
import org.jflux.api.registry.Descriptor;
import org.jflux.api.service.ServiceDependency;
import org.jflux.api.service.ServiceDependency.UpdateStrategy;
import org.jflux.api.service.binding.ServiceBinding;
import org.jflux.api.service.binding.ServiceBinding.BindingStrategy;

/**
 *
 * @author matt
 */


public class BindingMapBuilder {
	private final Map<String,ServiceBinding> myBindingMap = new HashMap<>();
	private BindingBuilder myActiveBuilder = null;

	public BindingMapBuilder(){}

	public BindingMapBuilder bind(ServiceDependency serviceDependency){
		completeActiveBinding();
		myActiveBuilder = new BindingBuilder(serviceDependency);
		return this;
	}

	public BindingMapBuilder className(String className){
		myActiveBuilder.dependencyClassName(className);
		return this;
	}

	public BindingMapBuilder bindingStrategy(BindingStrategy bindingStrategy){
		myActiveBuilder.bindingStrategy(bindingStrategy);
		return this;
	}

	public BindingMapBuilder updateStrategy(UpdateStrategy updateStrategy){
		myActiveBuilder.updateStrategy(updateStrategy);
		return this;
	}

	public BindingMapBuilder property(String key, String value){
		myActiveBuilder.property(key, value);
		return this;
	}
	
	public BindingMapBuilder descriptor(Descriptor descriptor){
		myActiveBuilder.descriptor(descriptor);
		return this;
	}

	public Map<String,ServiceBinding> getBindingMap(){
		completeActiveBinding();
		return myBindingMap;
	}

	private void completeActiveBinding(){
		if(myActiveBuilder == null){
			return;
		}
		ServiceBinding binding = myActiveBuilder.getBinding();
		myBindingMap.put(binding.getDependencyName(), binding);
		myActiveBuilder = null;
	}
}

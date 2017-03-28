package org.jflux.api.service.util;

import java.util.HashMap;
import java.util.Map;
import org.jflux.api.registry.Descriptor;
import org.jflux.api.registry.basic.BasicDescriptor;
import org.jflux.api.service.ServiceDependency;
import org.jflux.api.service.ServiceDependency.UpdateStrategy;
import org.jflux.api.service.binding.ServiceBinding;
import org.jflux.api.service.binding.ServiceBinding.BindingStrategy;

/**
 *
 * @author matt
 */


public class BindingBuilder {
	private ServiceDependency myServiceDepency;
	private String myDependencyClassName;
	private BindingStrategy myBindingStrategy = BindingStrategy.LAZY;
	private UpdateStrategy myUpdateStrategy;
	private Map<String,String> myDescriptorProperties = null;

	public BindingBuilder(){}

	public BindingBuilder(ServiceDependency serviceDependency){
		setServiceDependency(serviceDependency);
	}

	private void setServiceDependency(ServiceDependency serviceDependency){
		myServiceDepency = serviceDependency;
		myDependencyClassName = myServiceDepency.getDependencyClassName();
		myUpdateStrategy = myServiceDepency.getUpdateStrategy();
	}

	public BindingBuilder serviceDependency(ServiceDependency serviceDependency){
		setServiceDependency(serviceDependency);
		return this;
	}

	public BindingBuilder dependencyClassName(String className){
		myDependencyClassName = className;
		return this;
	}

	public BindingBuilder bindingStrategy(BindingStrategy bindingStrategy){
		myBindingStrategy = bindingStrategy;
		return this;
	}

	public BindingBuilder updateStrategy(UpdateStrategy updateStrategy){
		myUpdateStrategy = updateStrategy;
		return this;
	}

	public BindingBuilder property(String key, String value){
		if(myDescriptorProperties == null){
			myDescriptorProperties = new HashMap<>();
		}
		myDescriptorProperties.put(key, value);
		return this;
	}

	public ServiceBinding getBinding(){
		Descriptor descriptor = new BasicDescriptor(myDependencyClassName, myDescriptorProperties);
		return new ServiceBinding(myServiceDepency, descriptor, myBindingStrategy, myUpdateStrategy);
	}	
}

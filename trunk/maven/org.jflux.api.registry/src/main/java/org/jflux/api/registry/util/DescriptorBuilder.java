package org.jflux.api.registry.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.jflux.api.registry.Descriptor;
import org.jflux.api.registry.basic.BasicDescriptor;

/**
 *
 * @author matt
 */
public class DescriptorBuilder implements Descriptor{
	private String myDescriptorClassName;
	private Map<String,String> myDescriptorProperties = new HashMap<>();

	public DescriptorBuilder(){}

	public DescriptorBuilder(Descriptor descriptor){
		setDescriptor(descriptor);
	}
	
	public DescriptorBuilder(Class clazz){
		className(clazz);
	}
	
	public DescriptorBuilder(String className){
		DescriptorBuilder.this.className(className);
	}
	
	public DescriptorBuilder(String key, String value){
		property(key, value);
	}

	private void setDescriptor(Descriptor descriptor){
		myDescriptorClassName = descriptor.getClassName();
		for(String key : descriptor.getPropertyKeys()){
			property(key, descriptor.getProperty(key));
		}
	}

	public DescriptorBuilder descriptor(Descriptor descriptor){
		setDescriptor(descriptor);
		return this;
	}

	public DescriptorBuilder className(String className){
		myDescriptorClassName = className;
		return this;
	}

	public DescriptorBuilder className(Class className){
		myDescriptorClassName = className.getName();
		return this;
	}
	
	public DescriptorBuilder property(String key, String value){
		myDescriptorProperties.put(key, value);
		return this;
	}

	public Descriptor getBasicDescriptor(){
		return new BasicDescriptor(myDescriptorClassName, myDescriptorProperties);
	}

	@Override
	public String getProperty(String key) {
		return myDescriptorProperties.get(key);
	}

	@Override
	public Set<String> getPropertyKeys() {
		return myDescriptorProperties.keySet();
	}

	@Override
	public String getClassName() {
		return myDescriptorClassName;
	}
}

package org.jflux.api.registry.util;

import java.util.HashMap;
import java.util.Map;
import org.jflux.api.registry.Descriptor;
import org.jflux.api.registry.basic.BasicDescriptor;

/**
 *
 * @author matt
 */


public class DescriptorBuilder {
	private String myDescriptorClassName;
	private Map<String,String> myDescriptorProperties = null;

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

	public DescriptorBuilder prop(String key, String value){
		return property(key, value);
	}
	
	public DescriptorBuilder property(String key, String value){
		if(myDescriptorProperties == null){
			myDescriptorProperties = new HashMap<>();
		}
		myDescriptorProperties.put(key, value);
		return this;
	}

	public Descriptor getDescriptor(){
		return new BasicDescriptor(myDescriptorClassName, myDescriptorProperties);
	}

	public Descriptor build(){
		return getDescriptor();
	}
}

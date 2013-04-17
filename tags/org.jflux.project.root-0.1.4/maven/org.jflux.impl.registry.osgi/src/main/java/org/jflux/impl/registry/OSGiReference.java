/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jflux.impl.registry;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.jflux.api.core.Adapter;
import org.jflux.api.registry.Reference;
import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;

/**
 * Reference implementation for OSGi
 * @author Matthew Stevenson
 */
public class OSGiReference implements Reference, ServiceReference {

    /**
     * The reference's property name
     */
    public final static String PROP_REFERENCE_NAME = "OSGiReferenceNameProp";
    private ServiceReference myReference;

    /**
     * Converts an OSGi ServiceReference into a JFlux Reference
     * @param ref the ServiceReference
     */
    public OSGiReference(ServiceReference ref) {
        if (ref == null) {
            throw new NullPointerException();
        }
        myReference = ref;
    }

//    /**
//     * Gets the reference's name
//     * @return the reference's name
//     */
//    @Override
//    public String getName() {
//        Object nameObj = myReference.getProperty(PROP_REFERENCE_NAME);
//        return nameObj == null
//                ? "anonRef:" + this.toString() : nameObj.toString();
//    }

    /**
     * Gets a reference's property by name.
     * @param key the property name
     * @return the property value
     */
    @Override
    public String getProperty(String key) {
        Object obj = myReference.getProperty(key);
        return obj == null ? null : obj.toString();
    }

    /**
     * Gets the names of all the reference's properties
     * @return a set of property names
     */
    @Override
    public Set<String> getPropertyKeySet() {
        return new HashSet<String>(Arrays.asList(myReference.getPropertyKeys()));
    }
    
    final ServiceReference getReference(){
        return myReference;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final OSGiReference other = (OSGiReference) obj;
        if (this.myReference != other.myReference 
                && (this.myReference == null 
                        || !this.myReference.equals(other.myReference))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + 
                (this.myReference != null ? this.myReference.hashCode() : 0);
        return hash;
    }

    /**
     * Gets the names of all the reference's properties
     * @return the property names
     */
    @Override
    public String[] getPropertyKeys() {
        return myReference.getPropertyKeys();
    }

    /**
     * Gets the bundle associated with the reference
     * @return the bundle
     */
    @Override
    public Bundle getBundle() {
        return myReference.getBundle();
    }

    /**
     * Gets the bundles associated with the reference
     * @return the bundles
     */
    @Override
    public Bundle[] getUsingBundles() {
        return myReference.getUsingBundles();
    }

    /**
     * Determine if the reference binds to a specified class in a specified
     * bundle
     * @param bundle the bundle
     * @param className the class
     * @return true if the reference binds to a specified class in a specified
     * bundle
     */
    @Override
    public boolean isAssignableTo(Bundle bundle, String className) {
        return myReference.isAssignableTo(bundle, className);
    }

    @Override
    public int compareTo(Object reference) {
        return myReference.compareTo(reference);
    }
    
    private static Adapter<ServiceReference,OSGiReference> theReferenceApadter;
    public static Adapter<ServiceReference,OSGiReference> getReferenceAdapter(){
        if(theReferenceApadter == null){
            theReferenceApadter = new Adapter<ServiceReference,OSGiReference>() {
                @Override public OSGiReference adapt(ServiceReference a) {
                    return a == null ? null : new OSGiReference(a);
                }};
        }
        return theReferenceApadter;
    }
}

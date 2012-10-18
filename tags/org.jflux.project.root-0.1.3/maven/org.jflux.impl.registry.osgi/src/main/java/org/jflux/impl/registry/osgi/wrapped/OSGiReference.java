/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jflux.impl.registry.osgi.wrapped;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.jflux.api.core.Adapter;
import org.jflux.api.registry.opt.Reference;
import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;

/**
 *
 * @author Matthew Stevenson
 */
public class OSGiReference implements Reference<String, String>, ServiceReference {

    public final static String PROP_REFERENCE_NAME = "OSGiReferenceNameProp";
    private ServiceReference myReference;

    public OSGiReference(ServiceReference ref) {
        if (ref == null) {
            throw new NullPointerException();
        }
        myReference = ref;
    }

    @Override
    public String getName() {
        Object nameObj = myReference.getProperty(PROP_REFERENCE_NAME);
        return nameObj == null
                ? "anonRef:" + this.toString() : nameObj.toString();
    }

    @Override
    public String getProperty(String key) {
        Object obj = myReference.getProperty(key);
        return obj == null ? null : obj.toString();
    }

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

    @Override
    public String[] getPropertyKeys() {
        return myReference.getPropertyKeys();
    }

    @Override
    public Bundle getBundle() {
        return myReference.getBundle();
    }

    @Override
    public Bundle[] getUsingBundles() {
        return myReference.getUsingBundles();
    }

    @Override
    public boolean isAssignableTo(Bundle bundle, String className) {
        return myReference.isAssignableTo(bundle, className);
    }

    @Override
    public int compareTo(Object reference) {
        return myReference.compareTo(reference);
    }
    
    public static class ServiceReferenceWrapper implements 
            Adapter<ServiceReference,OSGiReference> {
        @Override
        public OSGiReference adapt(ServiceReference a) {
            return new OSGiReference(a);
        }        
    }
}

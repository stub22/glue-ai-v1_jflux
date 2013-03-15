/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jflux.impl.registry;

import java.util.Dictionary;
import org.jflux.api.core.Adapter;
import org.jflux.api.registry.opt.Certificate;
import org.osgi.framework.ServiceRegistration;

/**
 * Certificate implementation for OSGi.
 * @author Matthew Stevenson
 */
public class OSGiCertificate implements Certificate<OSGiReference>, ServiceRegistration {
    private ServiceRegistration myRegistration;

    OSGiCertificate(ServiceRegistration registration) {
        if(registration == null){
            throw new NullPointerException();
        }
        myRegistration = registration;
    }
    
    /**
     * Gets a reference to the certificate's service.
     * @return a reference to the certificate's service.
     */
    @Override
    public OSGiReference getReference() {
        return new OSGiReference(myRegistration.getReference());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final OSGiCertificate other = (OSGiCertificate) obj;
        if (this.myRegistration != other.myRegistration 
                && (this.myRegistration == null 
                        || !this.myRegistration.equals(other.myRegistration))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + 
                (this.myRegistration != null ? this.myRegistration.hashCode() : 0);
        return hash;
    }

    /**
     * Sets the certificate's properties.
     * @param properties the properties to set
     */
    @Override
    public void setProperties(Dictionary properties) {
        myRegistration.setProperties(properties);
    }

    /**
     * Unregisters the certificate's service
     */
    @Override
    public void unregister() {
        myRegistration.unregister();
    }
    
    /**
     * Wraps an OSGi ServiceRegistration into a JFlux Certificate
     */
    public static class ServiceRegistrationWrapper implements 
            Adapter<ServiceRegistration,OSGiCertificate> {
        /**
         * Converts a ServiceRegistration into a Certificate
         * @param a the ServiceRegistration
         * @return the Certificate
         */
        @Override
        public OSGiCertificate adapt(ServiceRegistration a) {
            return new OSGiCertificate(a);
        }        
    }
}

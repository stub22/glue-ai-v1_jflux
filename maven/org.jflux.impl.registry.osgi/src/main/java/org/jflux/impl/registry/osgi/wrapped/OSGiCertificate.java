/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jflux.impl.registry.osgi.wrapped;

import org.jflux.api.registry.opt.Certificate;
import org.osgi.framework.ServiceRegistration;

/**
 *
 * @author Matthew Stevenson
 */
public class OSGiCertificate implements Certificate<OSGiReference> {
    private ServiceRegistration myRegistration;

    OSGiCertificate(ServiceRegistration registration) {
        if(registration == null){
            throw new NullPointerException();
        }
        myRegistration = registration;
    }
    
    @Override
    public OSGiReference getReference() {
        return new OSGiReference(myRegistration.getReference());
    }
    
    final ServiceRegistration getRegistration(){
        return myRegistration;
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
}

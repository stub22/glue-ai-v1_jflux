package org.jflux.demo.discovery;

import org.jflux.spec.discovery.Discoverer;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

    public void start(BundleContext context) throws Exception {
        new Thread(new Discoverer()).start();
    }

    public void stop(BundleContext context) throws Exception {
        // TODO add deactivation code here
    }

}

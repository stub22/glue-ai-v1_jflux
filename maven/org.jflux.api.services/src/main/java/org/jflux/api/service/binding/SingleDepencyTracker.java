/*
 * Copyright 2013 The JFlux Project (www.jflux.org).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jflux.api.service.binding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.jflux.api.core.Source;
import org.jflux.api.registry.Reference;
import org.jflux.api.service.binding.ServiceBinding.BindingStrategy;
import static org.jflux.api.service.ServiceLifecycle.*;

/**
 *
 * @author matt
 */
public class SingleDepencyTracker<T> extends DependencyTracker<T> {

    private final Object trackerLock;

    public SingleDepencyTracker(
            String dependencyName, BindingStrategy binding,
            Source<Boolean> serviceCreated, Object lock) {

        super(dependencyName, binding, serviceCreated);
        trackerLock = lock;
    }

    @Override
    protected void eagerAdd(Reference ref) {
        synchronized (trackerLock) {
            List<Reference> refs = myTracker.getTrackedReferences();
            boolean tracking = !refs.isEmpty();
            Reference topRef = tracking ? refs.get(refs.size() - 1) : null;
            if (topRef != null && topRef == ref) {
                return;
            }
            T top = topRef != null ? myTracker.getTrackedService(topRef) : null;
            T t = myTracker.getService(ref);
            if (t == null) {
                return;
            }
            if (top == null) {
                firePropertyChange(PROP_DEPENDENCY_AVAILABLE, null, t);
            } else {
                firePropertyChange(PROP_DEPENDENCY_CHANGED, top, t);
                myTracker.releaseReference(topRef);
            }
        }
    }

    @Override
    protected void lazyAdd(Reference ref) {
        synchronized(trackerLock)
        {
            if (myCreatedFlag.getValue() || !myTracker.getTrackedServices().isEmpty()) {
                return;
            }
            T t = myTracker.getService(ref);
            if (t == null) {
                return;
            }
            firePropertyChange(PROP_DEPENDENCY_AVAILABLE, null, t);
        }
    }

    @Override
    protected void dependencyRemoved(Reference ref) {
        synchronized (trackerLock) {
            T service = myTracker.getTrackedService(ref);
            if (service == null) {
                return;
            }
            T newService = getReplacement(ref);
            if (newService == null) {
                firePropertyChange(PROP_DEPENDENCY_UNAVAILABLE, service, null);
            } else {
                firePropertyChange(PROP_DEPENDENCY_CHANGED, service, newService);
            }
        }
    }

    private T getReplacement(Reference ref) {
        List<Reference> tracked = myTracker.getTrackedReferences();
        if (myBindingStrategy == BindingStrategy.EAGER) {
            tracked = new ArrayList<Reference>(tracked);
            Collections.reverse(tracked);
        }
        for (Reference r : tracked) {
            if (r == ref) {
                continue;
            }
            T t = myTracker.getTrackedService(ref);
            if (t != null) {
                return t;
            }
        }
        tracked = myTracker.getAvailableReferences();
        if (myBindingStrategy == BindingStrategy.EAGER) {
            tracked = new ArrayList<Reference>(tracked);
            Collections.reverse(tracked);
        }
        for (Reference r : tracked) {
            if (r == ref) {
                continue;
            }
            T t = myTracker.getService(r);
            if (t != null) {
                return t;
            }
        }
        return null;
    }

    @Override
    public Object getTrackedDependency() {
        return getReference(myTracker.getTrackedReferences());
    }

    private T getReference(List<Reference> refs) {
        if (refs.isEmpty()) {
            return null;
        }
        if (myBindingStrategy == BindingStrategy.EAGER) {
            refs = new ArrayList<Reference>(refs);
            Collections.reverse(refs);
        }
        for (Reference r : refs) {
            T t = myTracker.getService(r);
            if (t != null) {
                return t;
            }
        }
        return null;
    }
}

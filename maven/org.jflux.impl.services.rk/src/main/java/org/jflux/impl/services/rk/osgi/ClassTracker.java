/*
 * Copyright 2011 Hanson Robokind LLC.
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

package org.jflux.impl.services.rk.osgi;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

/**
 * A generic wrapper for the ServiceTracker
 * Tracks all services registered to the OSGi framework with the given type and
 * matching the given filter
 *
 * @param <T> type of services to track
 * @author Matthew Stevenson <www.robokind.org>
 */
public class ClassTracker<T> {
	private static final Logger theLogger = LoggerFactory.getLogger(ClassTracker.class);
	private String myTrackedClassName;
	private BundleContext myContext;
	private String myFilter;
	private ServiceTrackerCustomizer myCustomizer;
	private ServiceTracker myTracker;

	public static <C> ClassTracker<C> build(Class<C> clazz, String filter) {
		if (clazz == null) {
			throw new NullPointerException();
		}
		BundleContext context = OSGiUtils.getBundleContext(clazz);
		if (context == null) {
			theLogger.warn("Could not find BundleContext for {}", clazz);
			return null;
		}
		ClassTracker<C> tracker = new ClassTracker<>(
				context, clazz.getName(), filter);
		return tracker;
	}

	public static <C> ClassTracker<C> build(Class<C> clazz, Properties props) {
		String filter = OSGiUtils.createServiceFilter(props);
		return build(clazz, filter);
	}

	/**
	 * Creates a new tracker for the given class.  init() must be called before
	 * the ClassTracker begins tracking.
	 *
	 * @param className name of the class to track
	 */
	public ClassTracker(String className) {
		setTrackedClassName(className);
	}

	/**
	 * Creates a new tracker for the given class, and begins tracking.
	 * There is no need to call init()
	 *
	 * @param className name of the class to track
	 */
	public ClassTracker(BundleContext context, String className, String filter)
			throws IllegalArgumentException {
		construct(context, className, filter, null);
	}

	/**
	 * Creates a new tracker for the given class, and begins tracking.
	 * There is no need to call init()
	 *
	 * @param className name of the class to track
	 */
	public ClassTracker(BundleContext context, String className, String filter,
						ServiceTrackerCustomizer customizer)
			throws IllegalArgumentException {
		construct(context, className, filter, customizer);
	}

	private void setTrackedClassName(String className) {
		if (className == null) {
			throw new NullPointerException();
		}
		myTrackedClassName =
				"(" + Constants.OBJECTCLASS + "=" + className + ")";
	}

	private void construct(BundleContext context, String className,
						   String filter, ServiceTrackerCustomizer customizer)
			throws IllegalArgumentException {
		myContext = context;
		setTrackedClassName(className);
		myFilter = filter;
		myCustomizer = customizer;
		if (init()) {
			return;
		}
		String filterMsg = (filter == null || filter.isEmpty()) ?
				"" : ", Filter: " + filter;
		String custMsg = customizer == null ?
				"" : ", Customizer: " + customizer;
		throw new IllegalArgumentException(
				"Unable to initialize ServiceTracker from given arguments."
						+ "  BundleContext: " + context + ", ClassName: " +
						className + filterMsg + custMsg + ".");

	}

	/**
	 * Sets the BundleContext to monitor.
	 *
	 * @param context BundleContext to monitor
	 */
	public void setContext(BundleContext context) {
		myContext = context;
	}

	/**
	 * Sets the filter for the service properties.
	 *
	 * @param filter filter service properties must match to be tracked
	 */
	public void setFilter(String filter) {
		myFilter = filter;
	}

	/**
	 * Gets the filter for the service properties.
	 *
	 * @return filter for the service properties
	 */
	public String getFilter() {
		return myFilter;
	}

	/**
	 * Sets the customizer
	 *
	 * @param cust the new customizer
	 */
	public void setCustomizer(ServiceTrackerCustomizer cust) {
		myCustomizer = cust;
	}

	/**
	 * Call to initialize the ClassTracker after setting the BundleContext,
	 * filter (optional), and customizer (optional).
	 *
	 * @return true if successful
	 */
	public boolean init() {
		if (myContext == null) {
			return false;
		}
		if (myTracker != null) {
			myTracker.close();
			myTracker = null;
		}
		boolean empty = (myFilter == null || myFilter.isEmpty());
		String filterStr = empty ? myTrackedClassName :
				"(&" + myTrackedClassName + myFilter + ")";
		try {
			Filter filter = myContext.createFilter(filterStr);
			myTracker = new ServiceTracker(myContext, filter, myCustomizer);
			myTracker.open();
		} catch (InvalidSyntaxException ex) {
			theLogger.warn("Could not create ServiceTracker, invalid filter syntax",
					ex);
			return false;
		}
		return true;
	}

	public void close() {
		if (myTracker == null) {
			return;
		}
		myTracker.close();
	}

	/**
	 * Returns a list of register services being tracked.
	 *
	 * @return list of register services being tracked or null if uninitialized
	 */
	public List<T> getServices() {
		if (myContext == null || myTracker == null) {
			return null;
		}
		Object[] objs = myTracker.getServices();
		int len = objs == null ? 0 : objs.length;
		if (len == 0) {
			return Collections.EMPTY_LIST;
		}
		List<T> ret = new ArrayList(len);
		for (Object t : objs) {
			try {
				ret.add((T) t);
			} catch (ClassCastException ex) {
				theLogger.warn("Found service not assignable to: '{}'"
						, myTrackedClassName, ex);
			}
		}
		return ret;
	}

	/**
	 * Returns the top service being tracked.
	 *
	 * @return the top service being tracked, or null if uninitialized or no services are found
	 */
	public T getTopService() {
		if (myContext == null || myTracker == null) {
			return null;
		}
		Object[] objs = myTracker.getServices();
		if (objs == null || objs.length == 0) {
			return null;
		}
		for (Object t : objs) {
			if (t == null) {
				continue;
			}
			try {
				return (T) t;
			} catch (ClassCastException ex) {
				String warning = "Found service not assignable to " +
						myTrackedClassName + ".  Found type: " +
						t.getClass().toString();
				theLogger.warn(warning, ex);
			}
		}
		return null;
	}
}

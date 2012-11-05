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


package org.jflux.api.services.extras;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * An implementation of PropertyChangeSource with helper methods for firing 
 * events.  This class uses the PropertyChangeSupportClass.
 * This can serve as a base class for classes which fire property
 * change events.
 * 
 * @author Stu Baurmann
 * @author Matthew Stevenson <www.robokind.org>
 */
public class PropertyChangeNotifier implements PropertyChangeSource{
    private transient PropertyChangeSupport myPropertyChangeSupport;

    /**
     * Creates a PropertyChangeNotifier with the given PropertyChangeSupport.
     * @param support
     */
    public PropertyChangeNotifier(PropertyChangeSupport support){
        myPropertyChangeSupport = support;
        completeInit(this);
    }

    /**
     * Creates a new PropertyChangeNotifier with a new PropertyChangeSupport
     * made from sourceBean.
     * @param sourceBean the source for the PropertyChangeSupport.
     */
    public PropertyChangeNotifier(Object sourceBean) {
        completeInit(sourceBean);
    }

    /**
     * Creates a new PropertyChangeNotifier with a new PropertyChangeSupport.
     */
    public PropertyChangeNotifier() {
        completeInit(this);
    }

    private void completeInit(Object sourceBean) {
        if (myPropertyChangeSupport == null) {
            myPropertyChangeSupport = PropertyChangeUtils.buildPropertyChangeSupport(sourceBean);
        }
    }
    
    /**
     * Add a PropertyChangeListener to the PropertyChangeSupport.
     * @param listener PropertyChangeListener to add.
     */
    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        myPropertyChangeSupport.addPropertyChangeListener(listener);
    }

    /**
     * Removes a PropertyChangeListener from the PropertyChangeSupport.
     * @param listener PropertyChangeListener to remove
     */
    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        myPropertyChangeSupport.removePropertyChangeListener(listener);
    }

    /**
     * Add a PropertyChangeListener to the PropertyChangeSupport for a specific 
     * event.
     * @param propertyName name of the event to listen for
     * @param listener PropertyChangeListener to add
     */
    @Override
    public void addPropertyChangeListener( String propertyName, PropertyChangeListener listener) {
        myPropertyChangeSupport.addPropertyChangeListener(propertyName, listener);
    }

    /**
     * Removes a PropertyChangeListener from the PropertyChangeSupport from
     * listening to a specific event.
     * @param propertyName name of the event
     * @param listener PropertyChangeListener to remove
     */
    @Override
    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        myPropertyChangeSupport.removePropertyChangeListener(propertyName, listener);
    }

    /**
     * Fire a property change event.
     * @param propertyName name of the property being changed
     * @param oldValue old property value
     * @param newValue new property value
     */
    protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        myPropertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
    }

    /**
     * Fire a property change event.
     * @param propertyName name of the property being changed
     * @param oldValue old property value
     * @param newValue new property value
     */
    protected void firePropertyChange(String propertyName, int oldValue, int newValue) {
        myPropertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
    }

    /**
     * Fire a property change event.
     * @param propertyName name of the property being changed
     * @param oldValue old property value
     * @param newValue new property value
     */
    protected void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {
        myPropertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
    }

    /**
     * Fire a property change event using an existing PropertyChangeEvent.
     * @param evt PropertyChangeEvent to fire
     */
    protected void firePropertyChange(PropertyChangeEvent evt) {
        myPropertyChangeSupport.firePropertyChange(evt);
    }

    /**
     * Fire a property change event for a property with an index.
     * @param propertyName name of the property being changed
     * @param index property index
     * @param oldValue old property value
     * @param newValue new property value
     */
    protected void fireIndexedPropertyChange(String propertyName, int index, Object oldValue, Object newValue) {
        myPropertyChangeSupport.fireIndexedPropertyChange(propertyName, index, oldValue, newValue);
    }

    /**
     * Fire a property change event for a property with an index.
     * @param propertyName name of the property being changed
     * @param index property index
     * @param oldValue old property value
     * @param newValue new property value
     */
    protected void fireIndexedPropertyChange(String propertyName, int index, int oldValue, int newValue) {
        myPropertyChangeSupport.fireIndexedPropertyChange(propertyName, index, oldValue, newValue);
    }

    /**
     * Fire a property change event for a property with an index.
     * @param propertyName name of the property being changed
     * @param index property index
     * @param oldValue old property value
     * @param newValue new property value
     */
    protected void fireIndexedPropertyChange(String propertyName, int index, boolean oldValue, boolean newValue){
        myPropertyChangeSupport.fireIndexedPropertyChange(propertyName, index, oldValue, newValue);
    }
    
    protected PropertyChangeListener[] getPropertyChangeListeners(){
        return myPropertyChangeSupport.getPropertyChangeListeners();
    }
    
    protected PropertyChangeListener[] getPropertyChangeListeners(String propertyName){
        return myPropertyChangeSupport.getPropertyChangeListeners(propertyName);
    }
    
    protected boolean hasListeners(String propertyName){
        return myPropertyChangeSupport.hasListeners(propertyName);
    }   
    /**
     * Removes all PropertyChangeListeners.
     */
    protected void clearAllListeners(){
        PropertyChangeListener[] listeners = getPropertyChangeListeners();
        if(listeners == null || listeners.length == 0){
            return;
        }
        for(PropertyChangeListener listener : listeners){
            removePropertyChangeListener(listener);
        }
    }
    /**
     * Removes all PropertyChangeListeners associated with the given property
     * name.  If propertyName is null, no listeners are removed.
     * Listeners are completely removed, not just removed for the property.
     * @param propertyName remove listeners for this property
     */
    protected void clearListeners(String propertyName){
        PropertyChangeListener[] listeners = 
                getPropertyChangeListeners(propertyName);
        if(listeners == null || listeners.length == 0){
            return;
        }
        for(PropertyChangeListener listener : listeners){
            removePropertyChangeListener(listener);
        }
    }
}

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


package org.jflux.impl.services.rk.property;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A support class for listening to PropertyChangeEvents.  This class is
 * useful for class inheritance hierarchies which need to listen for property
 * change events.
 * This class holds property event names and property change actions.
 * When a property change event is received, all PropertyChangeActions 
 * associated with the given event name will be fired in the order they were 
 * added.
 * 
 * @author Matthew Stevenson <www.robokind.org>
 */
public class PropertyChangeMonitor implements PropertyChangeListener{
    /**
     * A map associating PropertyChangeActions with an event name.
     */
    protected Map<String,List<PropertyChangeActionBase>> myPropertyActions;

    /**
     * Adds a PropertyChangeAction to be fired when an event with the given name 
     * is found.
     * @param property event name to listen for
     * @param action action to fire
     */
    public void addAction(String property, PropertyChangeActionBase action){
        if(myPropertyActions == null){
            myPropertyActions = new HashMap();
        }
        if(!myPropertyActions.containsKey(property)){
            myPropertyActions.put(property, new ArrayList(1));
        }
        myPropertyActions.get(property).add(action);
    }

    /**
     * Fires all actions associated with the name of the given event.
     * @param evt PropertyChangeEvent for the property being changed
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if(myPropertyActions == null){
            return;
        }
        String name = evt.getPropertyName();
        List<PropertyChangeActionBase> actions = myPropertyActions.get(name);
        if(actions == null || actions.isEmpty()){
            return;
        }
        for(PropertyChangeActionBase action : actions){
            action.performAction(evt);
        }
    }
}

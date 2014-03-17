/*
 * Copyright 2014 the RoboWorkshop Project
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
package org.jflux.swing.services;

import java.awt.Dimension;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JPanel;
import javax.swing.RepaintManager;
import org.jflux.impl.services.rk.lifecycle.DependencyDescriptor;

/**
 *
 * @author Matthew Stevenson <www.roboworkshop.org>
 */
public class DependencyListPanel extends JPanel{
    private Map<String,DependencyPanel> myPanelMap;
    
    public DependencyListPanel(){
        myPanelMap = new HashMap<String, DependencyPanel>();
    }
    
    public void addDependency(DependencyDescriptor dep, Boolean status){
        if(dep == null){
            throw new NullPointerException();
        }
        if(myPanelMap.containsKey(dep.getDependencyName())){
           return;
        }
        DependencyPanel panel = new DependencyPanel();
        panel.setDependency(dep, status);
        myPanelMap.put(dep.getDependencyName(), panel);
        add(panel);
    }
    
    public Dimension getDependenciesSize(){
        int height = 0;
        for(DependencyPanel p : myPanelMap.values()){
            height += p.getPreferredSize().height;
        }
        Dimension d = new Dimension(getPreferredSize().width, height);
        return d;
    }

    public void removeDependency(DependencyDescriptor dep){
        if(dep == null){
            throw new NullPointerException();
        }
        DependencyPanel panel = myPanelMap.remove(dep.getDependencyName());
        if(panel == null){
            return;
        }
        myPanelMap.remove(dep.getDependencyName());
        remove(panel);
    }
    
    public void updateDependnecyPanel(DependencyDescriptor dep, Boolean status){
        if(dep == null){
            throw new NullPointerException();
        }
        DependencyPanel panel = myPanelMap.get(dep.getDependencyName());
        if(panel == null){
            return;
        }
        panel.updateDisplay(status);
    }
    
    public boolean hasDependency(String depId){
        if(depId == null){
            return false;
        }
        return myPanelMap.containsKey(depId);
    }
    
    public void updateDependnecyStatus(String depId, Boolean status){
        if(depId == null){
            throw new NullPointerException();
        }
        final DependencyPanel panel = myPanelMap.get(depId);
        if(panel == null){
            return;
        }
        panel.updateStatus(status);
        RepaintManager.currentManager(this).markCompletelyDirty(panel);
    }
    
    public void clearDependencies(){
        myPanelMap.clear();
        removeAll();
    }
}

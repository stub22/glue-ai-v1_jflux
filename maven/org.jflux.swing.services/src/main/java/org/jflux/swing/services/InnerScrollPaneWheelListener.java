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

import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import javax.swing.JScrollPane;

/**
 *
 * @author Matthew Stevenson <www.roboworkshop.org>
 */


public class InnerScrollPaneWheelListener implements MouseWheelListener {
    private Integer myPrevVal;
    private int myCount = 3;
    
    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        JScrollPane pane = (JScrollPane)e.getSource();
        int val = pane.getVerticalScrollBar().getValue();
        if(myPrevVal != null && myPrevVal != val){
            myPrevVal = val;
            myCount = 0;
            return;
        }
        myPrevVal = val;
        if(myCount++ < 3){
            return;
        }
        int max = pane.getVerticalScrollBar().getMaximum();
        int min = pane.getVerticalScrollBar().getMinimum();
        int vis = pane.getVerticalScrollBar().getVisibleAmount();
        if((val+vis == max && e.getWheelRotation() == 1)
                || (val == min && e.getWheelRotation() == -1)){
                pane.getParent().dispatchEvent(e);
        }
    }
}

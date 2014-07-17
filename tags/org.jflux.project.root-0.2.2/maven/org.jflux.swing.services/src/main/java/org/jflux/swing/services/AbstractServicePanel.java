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

import java.beans.PropertyChangeListener;
import javax.swing.JPanel;
import org.jflux.api.service.Manager;

/**
 * Abstraction of a managed services panel.
 * @author Amy Jessica Book <jgpallack@gmail.com>
 */
public abstract class AbstractServicePanel<T extends Manager> extends JPanel {
    public abstract void setService(T service);
    public abstract PropertyChangeListener getServiceChangeListener();
}

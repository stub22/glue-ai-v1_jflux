/*
 * Copyright 2014 the JFlux Project.
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

package org.jflux.api.common.rk.config;

import java.io.File;
import javax.swing.ImageIcon;

/**
 * FileSystemAdapter implementation for reading from operating system's file
 * system.
 * 
 * @author Matthew Stevenson <www.jflux.org>
 */
public class FileSystemAdapterImpl implements FileSystemAdapter {

    @Override
    public File openFile(String path) {
        if(path == null){
            return null;
        }
        return new File(path);
    }

    @Override
    public ImageIcon openImageIcon(String path) {
        if(path == null){
            return null;
        }
        return new ImageIcon(path);
    }

}

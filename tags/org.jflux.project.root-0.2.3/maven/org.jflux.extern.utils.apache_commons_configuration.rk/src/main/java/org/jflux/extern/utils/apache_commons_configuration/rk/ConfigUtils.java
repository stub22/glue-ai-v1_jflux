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

package org.jflux.extern.utils.apache_commons_configuration.rk;

import java.awt.Color;
import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.jflux.api.common.rk.config.FileSystemAdapter;
import org.jflux.api.common.rk.config.FileSystemAdapterImpl;
import org.jflux.api.common.rk.localization.LanguageLocale;
import org.jflux.api.common.rk.utils.ListUtils;
import org.jflux.api.common.rk.utils.Utils;

/**
 * Static utility methods for working with Configurations.
 * 
 * @author Matthew Stevenson <www.jflux.org>
 */
public class ConfigUtils {
    private final static Logger theLogger = Logger.getLogger(ConfigUtils.class.getName());
    private static Configuration theConfiguration;
    private static ConfigCache theCache = new ConfigCache();
    private static FileSystemAdapter theFileSystemAdapter;

    /**
     * Attempts to load a Configuration from the default location.
     * @return Configuration loaded from the default location
     */
    public static Configuration getConfiguration(){
        if(theConfiguration == null){
            try{
                File config = getFileSystemAdapter().openFile("./resources/main.properties");
                theConfiguration = new PropertiesConfiguration(config);
            }catch(ConfigurationException t){
                theLogger.log(Level.WARNING, "Cannont find main.properties", t);
            }
        }
        return theConfiguration;
    }
    
    /**
     * Set the FileSystemAdapter.
     * @param fsa FileSystemAdapter to use
     */
    public static void setFileSystemAdapter(FileSystemAdapter fsa){
        theFileSystemAdapter = fsa;
    }

    /**
     * Returns the current FileSystemAdapter.
     * @return current FileSystemAdapter
     */
    public static FileSystemAdapter getFileSystemAdapter(){
        if(theFileSystemAdapter == null){
            theFileSystemAdapter = new FileSystemAdapterImpl();
        }
        return theFileSystemAdapter;
    }

    /**
     * Retrieves a Configuration linked from the the default Configuration.
     * @param key key for the Configuration.
     * @return Configuration linked from the the default Configuration
     */
    public static Configuration getLinkedConfig(String key){
        return theCache.getLinkedConfig(getConfiguration(), key);
    }

    /**
     * Reads a color value from the Configuration at the given key
     * @param config Configuration to read
     * @param key Configuration key for the f
     * @return color value from the Configuration at the given key
     */
    public static Color readColor(Configuration config, String key){
        if(!config.containsKey(key)){
            return null;
        }
        String colStr = config.getString(key).trim();
        int len = colStr.length()-1;
        if(!colStr.matches("#[0-9a-fA-F]{3,8}") || colStr.length() == 5){
            return null;
        }
        if(len == 3 || len == 6){
            return Color.decode(colStr);
        }else if(len == 4){
            int r = Utils.readHex(colStr.charAt(1))*17;
            int g = Utils.readHex(colStr.charAt(1))*17;
            int b = Utils.readHex(colStr.charAt(1))*17;
            int a = Utils.readHex(colStr.charAt(1))*17;
            return new Color(r, g, b, a);
        }else{
            int r = Utils.readHex(colStr.substring(1, 3));
            int g = Utils.readHex(colStr.substring(3, 5));
            int b = Utils.readHex(colStr.substring(5, 7));
            int a = Utils.readHex(colStr.substring(7, 9));
            return new Color(r, g, b, a);
        }
    }

    /**
     * Reads a list of floats from the Configuration at the given key
     * @param config Configuration to read
     * @param key Configuration key for the floats
     * @return list of floats from the Configuration at the given key
     */
    public static float[] readFloats(Configuration config, String key){
        try{
            String[] strs = config.getStringArray(key);
            return ListUtils.parseFloats(strs);
        }catch(Throwable t){
            theLogger.log(Level.WARNING, "There was an error readign key:" + key, t);
            return null;
        }
    }
    
    /**
     * Builds a new LanguageLocale from the given Configuration.
     * @param langConfig Configuration for the LanguageLocale
     * @return new LanguageLocale from the given Configuration
     */
    public static LanguageLocale loadLanguage(Configuration langConfig){
        String locale = langConfig.getString("language.locale", null);
        if(locale == null || locale.isEmpty()){
            theLogger.log(Level.SEVERE, "Unable to load language, could not find " +
                    " language name.  Expected at key language.locale");
            return null;
        }
        String abrv = langConfig.getString("language.locale.abrv", null);
        if(abrv == null || abrv.isEmpty()){
            abrv = locale.substring(0, 3);
            theLogger.log(Level.SEVERE, "Unable to find language abreviation for '{0}'" +
                    ".  Expected at key language.locale.abrv." +
                    "Setting abrevation to {1}", new Object[]{locale, abrv});
        }
        Map<String,String> words = new HashMap<String, String>();
        Iterator<String> keys = langConfig.getKeys();
        while(keys.hasNext()){
            String key = keys.next();
            words.put(key, langConfig.getString(key,""));
        }
        return new LanguageLocale(locale, abrv, words);
    }
}

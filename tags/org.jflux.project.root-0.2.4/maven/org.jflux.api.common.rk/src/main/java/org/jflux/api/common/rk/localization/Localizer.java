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

package org.jflux.api.common.rk.localization;

/**
 * Static methods for language localization.
 * 
 * @author Matthew Stevenson <www.jflux.org>
 */
public class Localizer {
    private static LanguageLocale theLocale;

    /**
     * Sets the LanguageLocale to use for localization.
     * @param locale new LanguageLocal
     */
    public static void setLocale(LanguageLocale locale){
        theLocale = locale;
    }

    /**
     * Returns the LanguageLocale being used.
     * @return current LanguageLocale
     */
    public static LanguageLocale getLocale(){
        return theLocale;
    }

    /**
     * Returns the name of the current LanguageLocale.
     * @return name of the current LanguageLocale.  null if no locale is set.
     */
    public static String getLocaleName(){
        if(theLocale == null){
            return null;
        }
        return theLocale.getLocale();
    }

    /**
     * Returns the localized value for the given key.
     * @param key String to localize
     * @return localized value for the given key
     */
    public static String localize(String key){
        if(theLocale == null){
            return key;
        }
        return theLocale.get(key);
    }

    /**
     * Return the String associated with the given key for the set LanguageLocale
     * @param key the key of the String to lookup
     * @return String associated with the given key for the set LanguageLocale
     */
    public static String $(String key){
        return localize(key);
    }
    /**
     * Return the String associated with the given key followed by a space: ' '.
     * @param key the key of the String to lookup 
     * @return String associated with the given key followed by a space
     */
    public static String $_(String key){
        return localize(key) + " ";
    }
    /**
     * Return the String associated with the given key proceeded with a space: ' '.
     * @param key the key of the String to lookup
     * @return String associated with the given key proceeded with a space
     */
    public static String _$(String key){
        return " " + localize(key);
    }
    /**
     * Return the String associated with the given key surrounded with spaces: ' '.
     * @param key the key of the String to lookup 
     * @return String associated with the given key surrounded with spaces
     */
    public static String _$_(String key){
        return " " + localize(key) + " ";
    }
}

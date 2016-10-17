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

import java.util.Map;
import java.util.logging.Logger;

/**
 * A class for localizing String to a certain language.
 * 
 * @author Matthew Stevenson <www.jflux.org>
 */
public class LanguageLocale {
    private final static Logger theLogger = Logger.getLogger(LanguageLocale.class.getName());
    private String myLocale;
    private String myAbbreviation;
    private Map<String,String> myDictionary;

    /**
     * Creates a new LanguageLocale.
     * @param locale name of the locale
     * @param abrv language abbreviation
     * @param dictionary language localization dictionary
     */
    public LanguageLocale(String locale, String abrv, Map<String,String> dictionary){
        myLocale = locale;
        myAbbreviation = abrv;
        myDictionary = dictionary;
    }

    /**
     * Returns the name of the LanguageLocale.
     * @return name of the LanguageLocale
     */
    public String getLocale(){
        return myLocale;
    }

    /**
     * Returns the abbreviation of the language.
     * @return abbreviation of the language
     */
    public String getAbbreviation(){
        return myAbbreviation;
    }

    /**
     * Returns the localized value of the given key.
     * @param key String to localize
     * @return localized value of the given key
     */
    public String get(String key){
        if(key == null){
            return "";
        }
        if(!myDictionary.containsKey(key)){
            return key;
        }
        return myDictionary.get(key);
    }
}

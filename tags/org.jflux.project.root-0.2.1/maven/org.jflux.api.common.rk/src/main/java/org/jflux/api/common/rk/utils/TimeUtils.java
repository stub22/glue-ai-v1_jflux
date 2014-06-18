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

package org.jflux.api.common.rk.utils;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility methods for dealing with time.
 * 
 * @author Matthew Stevenson <www.jflux.org>
 */
public class TimeUtils {
    private final static Logger theLogger = Logger.getLogger(TimeUtils.class.getName());

    /**
     * Returns the current clock time.
     * @return current clock time
     */
    public static long now(){
        return System.currentTimeMillis();
    }

    /**
     * Attempts to sleep for the specified length
     * @param msec number of milliseconds to sleep
     */
    public static void sleep(long msec){
        try{
            Thread.sleep(msec);
        }catch(InterruptedException ex){
            theLogger.log(Level.WARNING, "Thread sleep interrupted.", ex);
        }
    }

    /**
     * Formats a timespan in milliseconds
     * @param millisec number of milliseconds
     * @return a string of hours:minutes:seconds:milliseconds
     */
    public static String timeString(long millisec){
        return timeString(millisec, 0);
    }
    /**
     * Formats a timespan in milliseconds
     * @param millisec number of milliseconds
     * @param len minimum String length, padded with 0s
     * @return a string of hours:minutes:seconds:milliseconds
     */
    public static String timeString(long millisec, int len){
        boolean neg = millisec < 0;
        if(neg){
            millisec *= -1;
        }
        String ret = "";
        for(int i=0; i<3; i++, len--){
            long val = millisec % 10;
            ret = val + ret;
            millisec /= 10;
        }
        do{
            long val = millisec % 60;
            ret = val + ":" + ret;
            if(val < 10){
                ret = "0" + ret;
            }
            millisec /= 60;
            len-=2;
        }while(millisec > 0);
        while(len > 0){
            ret = "00:" + ret;
            len -= 2;
        }
        if(neg){
            ret = "-" + ret;
        }
        return ret;
    }
}

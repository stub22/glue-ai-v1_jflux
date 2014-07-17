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

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Utility methods for working with Lists.
 * 
 * @author Matthew Stevenson <www.jflux.org>
 */
public class ListUtils {
    /**
     * Returns an array of floats parsed from the given strings.
     * @param fStrs strings to parse
     * @return array of floats parsed from the given strings
     */
    public static float[] parseFloats(String...fStrs){
        float[] floats = new float[fStrs.length];
        for(int i=0; i<fStrs.length; i++){
            floats[i] = Float.parseFloat(fStrs[i]);
        }
        return floats;
    }

    /**
     * Returns a new List with a clone of each point from the list provided.
     * @param list points to copy
     * @return deep copy of a list of Point2D
     */
    public static List<Point2D> deepCopy(List<Point2D> list){
        List<Point2D> ret = new ArrayList(list.size());
        for(Point2D p : list){
            ret.add((Point2D)p.clone());
        }
        return ret;
    }

    /**
     * Takes an array of unsorted integers with no repeated values.  Sorts it 
     * into arrays of consecutive numbers.
     * @param vals unsorted array of integer without repeats
     * @return List of arrays of consecutive numbers
     */
    public static List<Integer[]> findConsecutiveSequences(Integer[] vals){
        Arrays.sort(vals);
        List<Integer[]> breaks = new ArrayList();
        int prev = 0;
        for(int i = 1; i<vals.length; i++){
            if(vals[i] == vals[i-1] + 1){
                continue;
            }
            breaks.add(Arrays.copyOfRange(vals, prev, i));
            prev = i;
        }
        breaks.add(Arrays.copyOfRange(vals, prev, vals.length));
        return breaks;
    }

    /**
     * Returns the items in the given list which match the given class.
     * @param <T> class to match
     * @param list items to filter
     * @param cls class to match
     * @return items in the given list which match the given class
     */
    public static <T> List<T> filterType(List list, Class<T> cls){
        List<T> filtered = new ArrayList();
        for(Object obj : list){
            if(cls.isInstance(obj)){
                filtered.add((T)obj);
            }
        }
        return filtered;
    }
}

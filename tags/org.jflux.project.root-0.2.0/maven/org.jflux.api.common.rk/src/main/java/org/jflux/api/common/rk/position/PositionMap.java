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

package org.jflux.api.common.rk.position;

import java.util.Map;

/**
 * PositionMap is a Map of an Identifier type to a Position type.
 * The Positions are commonly a NormalizedRange.
 * 
 * @param <Identifier> 
 * @param <Position> 
 * @author Matthew Stevenson <www.jflux.org>
 */
public interface PositionMap<Identifier, Position> 
        extends Map<Identifier, Position> {
    
    /**
     * PositionMap.HashMap&lt;Id, Pos&gt; is a java.util.HashMap&lt;Id, Pos&gt;
     * @param <Id> identifier
     * @param <Pos> position
     */
    public static class HashMap<Id, Pos> extends java.util.HashMap<Id, Pos>
        implements PositionMap<Id, Pos> {}
}

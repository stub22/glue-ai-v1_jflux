/*
 * Copyright 2012 The JFlux Project (www.jflux.org).
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
package org.jflux.api.registry;

/**
 * Retrieves items using a Reference.
 * 
 * @param <Ref> Registry reference type
 * 
 * @author Matthew Stevenson
 */
public interface Retriever<Ref> {
    /**
     * Returns an Adapter for retrieving an item.
     * @param clazz the item's class
     * @return Adapter for retrieving an item
     */
    public <T> T retrieve(Class<T> clazz, Ref reference);
    /**
     * Returns an Adapter for retrieving an untyped item.
     * @return Adapter for retrieving an untyped item
     */
    public Object retrieve(Ref reference);
    /**
     * Returns a Listener for releasing an item which was retrieved.
     * @return Listener for releasing an item which was retrieved
     */
    public void release(Ref reference);
//    
//    /**
//     * Returns an Adapter for retrieving an item asynchronously.
//     * @param clazz the item's class
//     * @return Adapter for retrieving an item
//     */
//    public <T> void retrieveAsync(Class<T> clazz, Ref reference, Listener<T> listener);
//    
//    /**
//     * Returns a Listener for releasing an item which was retrieved
//     * asynchronously.
//     * @return Listener for releasing an item which was retrieved
//     */
//    public void retrieveAsync(Ref reference, Listener listener);
}

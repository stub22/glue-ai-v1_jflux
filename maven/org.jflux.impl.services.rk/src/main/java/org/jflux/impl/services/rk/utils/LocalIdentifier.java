/*
 * Copyright 2011 Hanson Robokind LLC.
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

package org.jflux.impl.services.rk.utils;

/**
 * A LocalIdentifier should be unique within its scope, but has no expectation 
 * of being Globally Unique if used elsewhere.  
 * 
 * A Servo.Id is expected to be unique within the ServoController, but two 
 * ServoControllers may use the same Servo.Id to identify different Servos. * 
 * 
 * @author Matthew Stevenson <www.robokind.org>
 */
public interface LocalIdentifier {
    
}

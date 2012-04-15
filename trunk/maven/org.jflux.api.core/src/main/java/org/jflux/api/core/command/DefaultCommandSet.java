/*
 *  Copyright 2012 by The JFlux Project (www.jflux.org).
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
package org.jflux.api.core.command;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Matthew Stevenson <www.robokind.org>
 */
public class DefaultCommandSet<Cmd extends Command> implements CommandSet<Cmd> {
    private Map<String,CommandInterpreter<Cmd>> myIntpreterMap;
    
    public DefaultCommandSet(){
        myIntpreterMap = new HashMap<String, CommandInterpreter<Cmd>>();
    }
    
    @Override
    public void addInterpreter(
            String commandName, CommandInterpreter<Cmd> interpreter){
        myIntpreterMap.put(commandName, interpreter);
    }
    
    @Override
    public void removeInterpreter(String commandType){
        myIntpreterMap.remove(commandType);
    }
    
    @Override
    public CommandInterpreter<Cmd> getInterpreter(Cmd command) {
        if(command == null){
            throw new NullPointerException();
        }
        String commandName = command.getCommandName();
        if(commandName == null){
            throw new NullPointerException();
        }
        CommandInterpreter<Cmd> interpreter = myIntpreterMap.get(commandName);
        if(interpreter == null){
            return null;
        }
        if(!interpreter.validateCommand(command)){
            return null;
        }
        return interpreter;
    }

    @Override
    public void handleEvent(Cmd command) {
        if(command == null){
            throw new NullPointerException();
        }
        CommandInterpreter<Cmd> interpreter = getInterpreter(command);
        if(interpreter == null){
            throw new IllegalArgumentException(
                    "Unable to find CommandInterpreter for Command: " 
                    + command);
        }
        interpreter.handleCommand(command);
    }
    
}

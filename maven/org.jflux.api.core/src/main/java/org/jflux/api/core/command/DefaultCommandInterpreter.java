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

import org.jflux.api.core.util.Listener;

/**
 *
 * @author Matthew Stevenson <www.robokind.org>
 */
public class DefaultCommandInterpreter<Cmd extends Command> implements
        CommandInterpreter<Cmd> {
    private String myCommandName;
    private Listener<Cmd> myCommandProc;
    
    public DefaultCommandInterpreter(
            String commandName, Listener<Cmd> commandProc){
        if(commandName == null || commandProc == null){
            throw  new NullPointerException();
        }
        myCommandName = commandName;
        myCommandProc = commandProc;
    }
    
    @Override
    public boolean validateCommand(Cmd command) {
        if(command == null){
            return false;
        }
        String cmdType = command.getCommandName();
        return myCommandName.equals(cmdType);
    }
    
    @Override
    public boolean handleCommand(Cmd command) {
        if(validateCommand(command)){
            myCommandProc.handleEvent(command);
            return true;
        }
        return false;
    }
    
    public static class Factory<Cmd extends Command> implements 
            CommandInterpreterFactory<Cmd>{

        @Override
        public CommandInterpreter<Cmd> buildInterpreter(
                String name, Listener<Cmd> r) {
            if(name == null || r == null){
                throw new NullPointerException();
            }
            return new DefaultCommandInterpreter<Cmd>(name, r);
        }
    }
}

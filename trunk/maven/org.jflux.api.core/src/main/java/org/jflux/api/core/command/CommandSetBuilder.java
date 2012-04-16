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
 * @author Matthew Stevenson <www.jflux.org>
 */
public class CommandSetBuilder<Cmd> {
    private CommandInterpreterFactory<Cmd> myFactory;
    private CommandSet<Cmd> myCommandSet;
    
    public static <Cmd extends Command> CommandSetBuilder<Cmd> create(){
        return new CommandSetBuilder<Cmd>(
                new DefaultCommandSet<Cmd>(), 
                new DefaultCommandInterpreter.Factory<Cmd>());
    }
    
    public static <Cmd extends Command> 
            CommandSetBuilder<Cmd> create(CommandSet<Cmd> cmdSet){
        if(cmdSet == null){
            throw new NullPointerException();
        }
        return new CommandSetBuilder<Cmd>(
                cmdSet, 
                new DefaultCommandInterpreter.Factory<Cmd>());
    }
    
    public CommandSetBuilder(
            CommandSet<Cmd> cmdSet, CommandInterpreterFactory<Cmd> factory){
        if(cmdSet == null || factory == null){
            throw new NullPointerException();
        }
        myCommandSet = cmdSet;
        myFactory = factory;
    }
    
    public CommandSetBuilder<Cmd> add(
            String commandName, Listener<Cmd> commandHandler){
        CommandInterpreter<Cmd> interp = 
                myFactory.buildInterpreter(commandName, commandHandler);
        if(interp == null){
            throw new NullPointerException();
        }
        myCommandSet.addInterpreter(commandName, interp);
        return this;
    }
    
    public CommandSet<Cmd> getCommandSet(){
        return myCommandSet;
    }
}

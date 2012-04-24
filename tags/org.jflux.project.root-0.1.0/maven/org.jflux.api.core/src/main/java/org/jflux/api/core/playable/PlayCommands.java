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
package org.jflux.api.core.playable;

import org.jflux.api.core.command.Command;
import org.jflux.api.core.command.CommandInterpreterFactory;
import org.jflux.api.core.command.CommandSet;
import org.jflux.api.core.command.DefaultCommandInterpreter;
import org.jflux.api.core.command.DefaultCommandSet;
import org.jflux.api.core.command.CommandSetBuilder;
import org.jflux.api.core.util.Listener;

/**
 *
 * @author Matthew Stevenson <www.jflux.org>
 */
public class PlayCommands {
    public final static String START = "START";
    public final static String PAUSE = "PAUSE";
    public final static String RESUME = "RESUME";
    public final static String STOP = "STOP";
    
    public static <Cmd extends Command> 
            CommandSet<Cmd> buildDefault(Playable p){
        CommandSet<Cmd> cmdSet = new DefaultCommandSet<Cmd>();
        addPlayCommands(p, cmdSet, 
                new DefaultCommandInterpreter.Factory<Cmd>());
        return cmdSet;
    } 
    
    public static <Cmd> void addPlayCommands(
            final Playable p, 
            CommandSet<Cmd> cmdSet,
            CommandInterpreterFactory<Cmd> fact){
        if(p == null || cmdSet == null || fact == null){
            throw new NullPointerException();
        }
        new CommandSetBuilder(cmdSet, fact)
            .add(START, new Listener<Cmd>() {
                @Override public void handleEvent(Cmd event) {
                    p.start();
                }})
            .add(PAUSE, new Listener<Cmd>() {
                @Override public void handleEvent(Cmd event) {
                    p.pause();
                }})
            .add(RESUME, new Listener<Cmd>() {
                @Override public void handleEvent(Cmd event) {
                    p.resume();
                }})
            .add(STOP, new Listener<Cmd>() {
                @Override public void handleEvent(Cmd event) {
                    p.stop();
                }});
    }
}

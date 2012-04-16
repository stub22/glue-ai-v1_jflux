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
package org.jflux.api.core.service;

import org.jflux.api.core.command.Command;
import org.jflux.api.core.command.CommandInterpreterFactory;
import org.jflux.api.core.command.CommandSet;
import org.jflux.api.core.command.DefaultCommandInterpreter;
import org.jflux.api.core.node.Node;
import org.jflux.api.core.util.playable.PlayCommands;
import org.jflux.api.core.util.DefaultNotifier;
import org.jflux.api.core.util.Notifier;

/**
 *
 * @author Matthew Stevenson <www.jflux.org>
 */
public class DefaultService<Cmd, Status, Ident, N extends Node> implements 
        Service<Cmd, Status, Ident, N> {
    /**
     * Creates a Service for the given Node with a default Playable CommandSet.
     * @param <Cmd> type of command message to use
     * @param <Status> type of status message to use
     * @param <N> type of Node for the Service
     * @param node Node to be hosted by the Service
     * @return Service for the given node with a default Playable CommandSet
     */
    public static <Cmd extends Command, Status, Ident, N extends Node> 
            DefaultService<Cmd, Status, Ident, N> createDefault(
            Ident ident, N node) {
        if(node == null){
            throw new NullPointerException();
        }
        CommandSet<Cmd> cmdSet = PlayCommands.buildDefault(node);
        return new DefaultService<Cmd, Status, Ident, N>(ident, node, cmdSet);
    }
    
    /**
     * Creates a new Service for the given Node and CommandSet.  Adds Playable 
     * commands to the CommandSet.
     * @param <Cmd> type of command message to use
     * @param <Status> type of status message to use
     * @param <N> type of Node for the Service
     * @param node Node to be hosted by the Service
     * @return Service for the given node with a default Playable CommandSet
     * @param cmdSet CommandSet to use
     * @return new Service for the given Node and CommandSet
     */
    public static <Cmd extends Command, Status, Ident, N extends Node> 
            DefaultService<Cmd, Status, Ident, N> createDefault(
            Ident ident, N node, CommandSet<Cmd> cmdSet) {
        if(node == null || cmdSet == null){
            throw new NullPointerException();
        }
        PlayCommands.addPlayCommands(node, cmdSet, 
                new DefaultCommandInterpreter.Factory<Cmd>());
        return new DefaultService<Cmd, Status, Ident, N>(ident, node, cmdSet);
    }
    /**
     * Creates a new Service for the given Node and CommandSet.  Adds Playable 
     * commands to the CommandSet.
     * @param <Cmd> type of command message to use
     * @param <Status> type of status message to use
     * @param <N> type of Node for the Service
     * @param node Node to be hosted by the Service
     * @return Service for the given node with a default Playable CommandSet
     * @param cmdSet CommandSet to use
     * @return new Service for the given Node and CommandSet
     * @param factory creates CommandInterepreters of the appropriate command 
     * type
     * @return new Service for the given Node and CommandSet
     */
    public static <Cmd, Status, Ident, N extends Node> 
            DefaultService<Cmd, Status, Ident, N> createDefault(
            Ident ident, N node, CommandSet<Cmd> cmdSet, 
            CommandInterpreterFactory<Cmd> factory) {
        if(node == null || cmdSet == null || factory == null){
            throw new NullPointerException();
        }
        PlayCommands.addPlayCommands(node, cmdSet, factory);
        return new DefaultService<Cmd, Status, Ident, N>(ident, node, cmdSet);
    }
    
    private Notifier<Status> myStatusNotifier;
    private N myNode;
    private CommandSet<Cmd> myCommandSet;
    private Ident myIdentifier;
    
    public DefaultService(
            Ident identifier, N node, CommandSet<Cmd> cmdSet){
        if(identifier == null || node == null || cmdSet == null){
            throw new NullPointerException();
        }
        myIdentifier = identifier;
        myStatusNotifier = new DefaultNotifier<Status>();
        myNode = node;
        myCommandSet = cmdSet;
    }

    @Override
    public Ident getIdentifier() {
        return myIdentifier;
    }
    
    @Override
    public CommandSet<Cmd> getCommandListener() {
        return myCommandSet;
    }

    @Override
    public Notifier<Status> getStatusNotifier() {
        return myStatusNotifier;
    }

    @Override
    public N getNode() {
        return myNode;
    }
}

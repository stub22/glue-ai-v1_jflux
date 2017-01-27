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
package org.jflux.api.messaging.rk.services;

import org.jflux.api.common.rk.playable.AbstractPlayable;
import org.jflux.api.messaging.rk.MessageAsyncReceiver;
import org.jflux.api.messaging.rk.MessageSender;
import org.slf4j.LoggerFactory;

/**
 * @author Matthew Stevenson <www.jflux.org>
 */
public class DefaultServiceClient<Conf>
		extends AbstractPlayable implements RemoteServiceClient<Conf> {
	private static final org.slf4j.Logger theLogger = LoggerFactory.getLogger(DefaultServiceClient.class);

	private MessageSender<ServiceCommand> myCommandSender;
	private MessageSender<Conf> myConfigSender;
	private MessageAsyncReceiver<ServiceError> myErrorReceiver;
	private ServiceCommandFactory myCommandFactory;
	private String myLocalServiceId;
	private String myRemoteServiceId;

	public DefaultServiceClient(
			String localId, String remoteId,
			MessageSender<ServiceCommand> commandSender,
			MessageSender<Conf> configSender,
			MessageAsyncReceiver<ServiceError> errorReceiver,
			ServiceCommandFactory commandFactory) {
		if (localId == null || remoteId == null) {
			throw new NullPointerException();
		}
		myLocalServiceId = localId;
		myRemoteServiceId = remoteId;
		myCommandSender = commandSender;
		myConfigSender = configSender;
		myErrorReceiver = errorReceiver;
		myCommandFactory = commandFactory;
	}

	@Override
	public void initialize(Conf config) {
		if (config == null) {
			throw new NullPointerException();
		}
		if (myConfigSender == null) {
			theLogger.warn("Unable to send config, Config Sender is null.");
			return;
		}
		myConfigSender.notifyListeners(config);
	}

	@Override
	public boolean onStart(long time) {
		send(ServiceCommand.START);
		return true;
	}

	@Override
	public boolean onPause(long time) {
		send(ServiceCommand.PAUSE);
		return true;
	}

	@Override
	public boolean onResume(long time) {
		send(ServiceCommand.RESUME);
		return true;
	}

	@Override
	public boolean onComplete(long time) {
		send(ServiceCommand.STOP);
		return true;
	}

	@Override
	public boolean onStop(long time) {
		send(ServiceCommand.STOP);
		return true;
	}

	protected void send(String commandStr) {
		if (myCommandSender == null || myCommandFactory == null) {
			theLogger.warn("Unable to send command. "
					+ " Command Sender or Command Factory is null.");
			return;
		}
		ServiceCommand command = myCommandFactory.create(
				myLocalServiceId, myRemoteServiceId, commandStr);
		theLogger.info("Sending Service Command [{}] - "
						+ "source: {}, dest: {}, time: {}.",
				command.getCommand(),
				command.getSourceId(),
				command.getDestinationId(),
				command.getTimestampMillisecUTC());
		myCommandSender.notifyListeners(command);
	}

	@Override
	public void setCommandSender(MessageSender<ServiceCommand> sender) {
		myCommandSender = sender;
	}

	@Override
	public void setConfigSender(MessageSender<Conf> sender) {
		myConfigSender = sender;
	}

	@Override
	public void setErrorReceiver(MessageAsyncReceiver<ServiceError> receiver) {
		myErrorReceiver = receiver;
	}

	@Override
	public void setCommandFactory(ServiceCommandFactory factory) {
		myCommandFactory = factory;
	}

	@Override
	public String getClientId() {
		return myLocalServiceId;
	}

	@Override
	public String getHostId() {
		return myRemoteServiceId;
	}
}

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

package org.jflux.extern.utils.apache_commons_configuration.rk;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Class for caching Configurations which have been loaded.
 *
 * @author Matthew Stevenson <www.jflux.org>
 */
public class ConfigCache {
	private static final org.slf4j.Logger theLogger = LoggerFactory.getLogger(ConfigCache.class);
	private Map<String, Configuration> myConfigs;

	/**
	 * Creates an empty ConfigCache.
	 */
	public ConfigCache() {
		myConfigs = new HashMap<>();
	}

	/**
	 * Loads a Configuration from a path defined in another Configuration
	 *
	 * @param config Configuration with the path to load
	 * @param key    key for the path to load
	 * @return Configuration loaded from the defined path
	 */
	public Configuration getLinkedConfig(Configuration config, String key) {
		if (myConfigs.containsKey(key)) {
			return myConfigs.get(key);
		}
		if (config == null || key == null) {
			return null;
		}
		String path = config.getString(key, null);
		if (path == null) {
			theLogger.error("Unable to find Configuratin key: {}. Unable to retrieve specified Configuration.", key);
			return null;
		}
		Configuration keyConfig = null;
		try {
			File file = ConfigUtils.getFileSystemAdapter().openFile(path);
			keyConfig = new PropertiesConfiguration(file);
		} catch (ConfigurationException ex) {
			theLogger.error("Unable to open Configuration for key '{}', at '{}'. {}", key, path, ex);
		}
		if (keyConfig != null && !keyConfig.isEmpty()) {
			myConfigs.put(key, keyConfig);
		}
		return keyConfig;
	}
}

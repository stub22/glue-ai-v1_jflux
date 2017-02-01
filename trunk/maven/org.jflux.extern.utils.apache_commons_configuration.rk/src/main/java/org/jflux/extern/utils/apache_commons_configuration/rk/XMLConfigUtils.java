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

import org.apache.commons.configuration.AbstractConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.tree.ConfigurationNode;
import org.jflux.api.common.rk.config.VersionProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Utility methods for XML files
 *
 * @author Matthew Stevenson <www.jflux.org>
 */
public class XMLConfigUtils {
	private static final Logger theLogger = LoggerFactory.getLogger(XMLConfigUtils.class);
	/**
	 * VersionProperty XML element name.
	 */
	public final static String XML_VERSION = "Version";
	/**
	 * VersionProperty XML type attribute name.
	 */
	public final static String XML_VERSION_TYPE = "type";
	/**
	 * VersionProperty Name XML element name.
	 */
	public final static String XML_VERSION_NAME = "Name";
	/**
	 * VersionProperty Number XML element name.
	 */
	public final static String XML_VERSION_NUMBER = "Number";
	/**
	 * VersionProperty XML namespace.
	 */
	public final static String VERSION_NAMESPACE = null;
	private final static String theAttributeTemplate = "[@%s]";

	/**
	 * Loads an XML file from the given path.
	 *
	 * @param path location of the XML file
	 * @return XML file at the given path
	 */
	public static HierarchicalConfiguration loadXMLConfig(String path) {
		File file = ConfigUtils.getFileSystemAdapter().openFile(path);
		try {
			return new XMLConfiguration(file);
		} catch (ConfigurationException t) {
			theLogger.warn("Cannot open XML file at: {}", path, t);
			return null;
		}
	}

	/**
	 * Reads a VersionProperty from a XML node.
	 *
	 * @param config XML node of the VersionProperty
	 * @return VersionProperty from a XML node
	 */
	public static VersionProperty readVersion(HierarchicalConfiguration config) {
		String name = config.getString(XML_VERSION_NAME);
		String num = config.getString(XML_VERSION_NUMBER);
		return new VersionProperty(name, num);
	}

	/**
	 * Reads a VersionProperty with a given type attribute from a XML node.
	 *
	 * @param config XML node with the VersionProperty
	 * @param type   type attribute value of the VersionProperty to read
	 * @return VersionProperty with a given type attribute from an XML node
	 */
	public static VersionProperty readVersion(HierarchicalConfiguration config, String type) {
		return readVersions(config, type).get(type);
	}

	/**
	 * Reads VersionProperties containing a given type attribute from a XML node.
	 *
	 * @param config XML node with the VersionProperties
	 * @param type   type attribute values of the VersionProperties to read
	 * @return VersionProperties containing a given type attribute from a XML node
	 */
	public static Map<String, VersionProperty> readVersions(HierarchicalConfiguration config, String... type) {
		Map<String, VersionProperty> versions = new HashMap();
		Map<String, HierarchicalConfiguration> versionConfigs = getVersionNodes(config, type);
		for (Entry<String, HierarchicalConfiguration> e : versionConfigs.entrySet()) {
			versions.put(e.getKey(), readVersion(e.getValue()));
		}
		return versions;
	}

	private static Map<String, HierarchicalConfiguration> getVersionNodes(HierarchicalConfiguration xml, String... types) {
		Arrays.sort(types);
		Map<String, HierarchicalConfiguration> nodes = new HashMap();
		for (HierarchicalConfiguration conf : (List<HierarchicalConfiguration>) xml.configurationsAt(XML_VERSION)) {
			String t = conf.getString(attributeKey(XML_VERSION_TYPE), "");
			if (!nodes.containsKey(t) && Arrays.binarySearch(types, t) >= 0) {
				nodes.put(t, conf);
			}
		}
		return nodes;
	}

	/**
	 * Returns a XML node for the given VersionProperty.
	 *
	 * @param version VersionProperty to write
	 * @param type    type attribute for the VersionProperty
	 * @return XML node for the given VersionProperty
	 */
	public static ConfigurationNode writeVersion(VersionProperty version, String type) {
		ConfigurationNode node = node(XML_VERSION);
		node.addAttribute(node(XML_VERSION_TYPE, type));
		node.addChild(node(XML_VERSION_NAME, version.getName()));
		node.addChild(node(XML_VERSION_NUMBER, version.getNumber()));
		return node;
	}

	/**
	 * Reads an Integer from the given XML node.
	 *
	 * @param config XML node containing an Integer
	 * @param key    attribute key
	 * @param def    default Integer value
	 * @return Integer from the given XML node.  <code>def</code> if an Integer cannot be read
	 */
	public static Integer getIntegerAttribute(AbstractConfiguration config, String key, Integer def) {
		return config.getInteger(attributeKey(key), def);

	}

	/**
	 * Reads a Double from the given XML node.
	 *
	 * @param config XML node containing a Double
	 * @param key    attribute key
	 * @param def    default Double value
	 * @return Double from the given XML node.  <code>def</code> if a Double cannot be read
	 */
	public static Double getDoubleAttribute(AbstractConfiguration config, String key, Double def) {
		return config.getDouble(attributeKey(key), def);
	}

	/**
	 * Reads a Boolean from the given XML node.
	 *
	 * @param config XML node containing a Boolean
	 * @param key    attribute key
	 * @param def    default Boolean value
	 * @return Boolean from the given XML node.  <code>def</code> if a Boolean cannot be read
	 */
	public static Boolean getBooleanAttribute(AbstractConfiguration config, String key, Boolean def) {
		return config.getBoolean(attributeKey(key), def);
	}

	/**
	 * Reads a String from the given XML node.
	 *
	 * @param config XML node containing a String
	 * @param key    attribute key
	 * @param def    default String value
	 * @return String from the given XML node.  <code>def</code> if a String cannot be read
	 */
	public static String getStringAttribute(AbstractConfiguration config, String key, String def) {
		return config.getString(attributeKey(key), def);
	}

	/**
	 * Formats the string as an xpath attribute.
	 *
	 * @param attribute attribute name
	 * @return xpath expression for selecting the attribute
	 */
	public static String attributeKey(String attribute) {
		return String.format(theAttributeTemplate, attribute);
	}

	/**
	 * Returns a new XML node.
	 *
	 * @return new XML node
	 */
	public static ConfigurationNode node() {
		return new HierarchicalConfiguration.Node();
	}

	/**
	 * Returns a new XML node with the given name.
	 *
	 * @param name name of the new XML node
	 * @return new XML node with the given name
	 */
	public static ConfigurationNode node(String name) {
		return new HierarchicalConfiguration.Node(name);
	}


	/**
	 * Returns a new XML node with the given name and value.
	 *
	 * @param name  name of the new XML node
	 * @param value value of the new XML node
	 * @return new XML node with the given name and value
	 */
	public static ConfigurationNode node(String name, Object value) {
		return new HierarchicalConfiguration.Node(name, value);
	}
}

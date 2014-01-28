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

package org.jflux.extern.utils.xpp3.rk;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jflux.api.common.rk.config.VersionProperty;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

/**
 * Utility methods for the XPP3 XML library.
 * 
 * @author Matthew Stevenson <www.jflux.org>
 */
public class XMLUtils {
    private final static Logger theLogger = Logger.getLogger(XMLUtils.class.getName());
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

    /**
     * Skips the current XML Element
     * @param xpp XML parser
     * @throws XmlPullParserException
     * @throws IOException
     */
    public static void skipElement(XmlPullParser xpp) throws XmlPullParserException, IOException{
        String name = xpp.getName();
        theLogger.log(Level.FINE, "Skipping Element: {0}", name);

        int eventType = xpp.next();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if(eventType == XmlPullParser.END_TAG && xpp.getName().equals(name)) {
                return;
            }else if(eventType == XmlPullParser.START_TAG) {
               skipElement(xpp);
            }
            eventType = xpp.next();
        }
        return;
    }

    /**
     * Returns the text from the current XML Element.
     * @param xpp XML Parser
     * @return text from the current XML Element
     * @throws XmlPullParserException
     * @throws IOException
     */
    public static String getElementText(XmlPullParser xpp) throws XmlPullParserException, IOException{
        String name = xpp.getName();
        int eventType = xpp.getEventType();
        String val = null;
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if(eventType == XmlPullParser.TEXT) {
                val = xpp.getText();
            } else if(eventType == XmlPullParser.END_TAG) {
                if(val == null){
                    break;
                }
                return val;
            }
            eventType = xpp.next();
        }
        throw new IllegalArgumentException("Unable to find value for " + name);
    }

    /**
     * Creates an XmlSerializer for writing XML to the given file.
     * @param path location to write the XML file
     * @return XmlSerializer for writing XML to the given file
     * @throws XmlPullParserException
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static XmlSerializer getXmlFileSerializer(String path) throws XmlPullParserException, FileNotFoundException, IOException{
        XmlPullParserFactory fact = XmlPullParserFactory.newInstance();
        XmlSerializer xs = fact.newSerializer();
        XMLUtils.format(xs, true);
        xs.setOutput(new FileWriter(path));
        return xs;
    }

    /**
     * Writes an XML element with the given namespace, name, and value.
     * @param xs XmlSerializer to write the XML
     * @param namespace element namespace
     * @param name element name
     * @param val element value
     * @throws IOException
     */
    public static void writeVal(XmlSerializer xs, String namespace, String name, Object val) throws IOException{
        xs.startTag(namespace, name);
            xs.text("" + val);
        xs.endTag(namespace, name);
    }

    /**
     * Set the indention String to be used for the given XmlSerializer.
     * @param xs XmlSerializer to change indention
     * @param indent new indention string
     */
    public static void setIndent(XmlSerializer xs, String indent){
        xs.setProperty("http://xmlpull.org/v1/doc/properties.html#serializer-indentation", indent);
    }

    /**
     * Set the newline String to be used for the given XmlSerializer.
     * @param xs XmlSerializer to change newline
     * @param nl new newline string
     */
    public static void setNewLine(XmlSerializer xs, String nl){
        xs.setProperty("http://xmlpull.org/v1/doc/properties.html#serializer-line-separator", nl);
    }

    /**
     * Set default XML formatting.
     * @param xs XmlSerializer to set formatting
     * @param on if true indention is a tab \\t and newline is \\n, otherwise
     * indention and newline are set to empty strings
     */
    public static void format(XmlSerializer xs, boolean on){
        setIndent(xs, on ? "\t" : "");
        setNewLine(xs, on ? "\n" : "");
    }

    /**
     * Writes a VersionProperty to XML.
     * @param xs XmlSerializer to write VersionProperty
     * @param p VersionProperty to write
     * @param type VersionProperty type attribute value
     * @throws IOException
     */
    public static void writeVersionProperty(XmlSerializer xs, VersionProperty p, String type) throws IOException{
        String namespace = VERSION_NAMESPACE;
        xs.startTag(namespace, XML_VERSION);
            xs.attribute(namespace, XML_VERSION_TYPE, type);
            xs.startTag(namespace, XML_VERSION_NAME);
                xs.text(p.getName());
            xs.endTag(namespace, XML_VERSION_NAME);
            xs.startTag(namespace, XML_VERSION_NUMBER);
                xs.text(p.getNumber());
            xs.endTag(namespace, XML_VERSION_NUMBER);
        xs.endTag(namespace, XML_VERSION);
    }
}

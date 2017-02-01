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

package org.jflux.api.common.rk.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Miscellaneous utility methods.
 *
 * @author Matthew Stevenson <www.jflux.org>
 */
public class Utils {
	private static final Logger theLogger = LoggerFactory.getLogger(Utils.class);

	/**
	 * Creates a String from the given byte array.
	 *
	 * @param bytes array of bytes
	 * @return String from the given byte array
	 */
	public static String bytesToString(byte[] bytes) {
		String ret = "";
		for (byte b : bytes) {
			ret += (char) b;
		}
		return ret;
	}

	/**
	 * Adds the values from src to dest where the key from src does not exist in dest.
	 */
	public static <K, V> void mergeMaps(Map<K, V> dest, Map<K, V>... srcs) {
		for (Map<K, V> src : srcs) {
			for (Entry<K, V> e : src.entrySet()) {
				if (!dest.containsKey(e.getKey())) {
					dest.put(e.getKey(), e.getValue());
				}
			}
		}
	}

	/**
	 * Returns the unsigned value of the given signed byte.
	 *
	 * @param b signed byte
	 * @return unsigned value of the given signed byte
	 */
	public static int unsign(byte b) {
		if (b < 0) {
			return 256 + b;
		}
		return b;
	}

	public static byte sign(int b) {
		if (b >= 128) {
			return (byte) (b - 256);
		}
		return (byte) b;
	}

	/**
	 * Calculates a checksum from the data and extra bytes
	 *
	 * @param data   byte array to check
	 * @param offset array offset
	 * @param len    data length
	 * @param invert should the value be inverted
	 * @param extra  extra bytes to use in calculating the checksum
	 * @return checksum calculated from the data and extra bytes
	 */
	public static byte checksum(byte[] data, int offset, int len, boolean invert, byte... extra) {
		int chk = 0;
		for (int i = 0; i < len; i++) {
			chk += data[offset + i];
		}
		for (byte b : extra) {
			chk += b;
		}
		if (invert) {
			return (byte) (~chk & 0xff);
		}
		return (byte) chk;
	}

	/**
	 * Ensures: low &lt;= n &lt;= high
	 *
	 * @param n    value to be bounded
	 * @param low  lower bound
	 * @param high upper bound
	 * @return n if it is within the bounds, otherwise the returns the bound it has passed
	 */
	public static double bound(double n, double low, double high) {
		return Math.max(low, Math.min(n, high));
	}

	/**
	 * Ensures: low &lt;= n &lt;= high
	 *
	 * @param n    value to be bounded
	 * @param low  lower bound
	 * @param high upper bound
	 * @return n if it is within the bounds, otherwise the returns the bound it has passed
	 */
	public static long bound(long n, long low, long high) {
		return Math.max(low, Math.min(n, high));
	}

	/**
	 * Ensures: low &lt;= n &lt;= high
	 *
	 * @param n    value to be bounded
	 * @param low  lower bound
	 * @param high upper bound
	 * @return n if it is within the bounds, otherwise the returns the bound it has passed
	 */
	public static int bound(int n, int low, int high) {
		return Math.max(low, Math.min(n, high));
	}

	/**
	 * Returns the decimal value of a two character hex string.
	 *
	 * @param xx two character hex string [0-9a-fA-F]
	 * @return an value 0-255
	 */
	public static int readHex(String xx) {
		int high = readHex(xx.charAt(0));
		high *= 16;
		int low = readHex(xx.charAt(1));
		return high + low;
	}

	/**
	 * Converts the characters '0'-'9' (48-58), 'A'-'F' (65-70), and 'a'-'f'
	 * (32-37) or to the decimal value of their corresponding hex values.
	 *
	 * @param c character to convert
	 * @return a value 0-15 corresponding the hex value of the character
	 */
	public static int readHex(char c) {
		int a = c;
		if (a >= 97) {
			a = a - 97 + 10;
		} else if (a >= 65) {
			a = a - 65 + 10;
		} else if (a >= 48) {
			a -= 48;
		}
		return a;
	}

	/**
	 * Converts a List of Byte Objects to an array of byte primatives.
	 *
	 * @param a List of Byte Objects
	 * @return array of byte primatives
	 */
	public static byte[] convertToByteArray(List<Byte> a) {
		byte[] b = new byte[a.size()];
		for (int i = 0; i < a.size(); i++) {
			b[i] = a.get(i);
		}
		return b;
	}
}

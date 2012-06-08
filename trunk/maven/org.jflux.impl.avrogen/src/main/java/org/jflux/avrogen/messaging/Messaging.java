package org.jflux.avrogen.messaging;

@SuppressWarnings("all")
public interface Messaging {
  public static final org.apache.avro.Protocol PROTOCOL = org.apache.avro.Protocol.parse("{\"protocol\":\"Messaging\",\"namespace\":\"org.jflux.avrogen.messaging\",\"types\":[{\"type\":\"record\",\"name\":\"MessageList\",\"fields\":[{\"name\":\"header\",\"type\":\"bytes\"},{\"name\":\"messages\",\"type\":{\"type\":\"array\",\"items\":\"bytes\"}}]}],\"messages\":{}}");
}

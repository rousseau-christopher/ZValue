package net.lamad.spring.dvalue.zookeeper.deserialiser;

public interface Deserializer {
    Object deserialize(byte[] input, Class targetClass, String charset);

    /**
     * @return source type supported, ex JSON, XML
     */
    String type();
}

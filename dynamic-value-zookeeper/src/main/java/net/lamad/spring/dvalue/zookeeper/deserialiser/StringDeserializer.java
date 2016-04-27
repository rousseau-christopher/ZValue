package net.lamad.spring.dvalue.zookeeper.deserialiser;

import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;

@Component
public class StringDeserializer implements Deserializer {
    @Override
    public Object deserialize(byte[] input, Class targetClass, String charset) {
        try {
            return new String(input, charset);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public String type() {
        return "String";
    }
}

package net.lamad.spring.dvalue.core.deserialiser;

import com.google.gson.Gson;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

public class GsonDeserializer implements Deserializer {
    private Gson gson = new Gson();

    @Override
    public Object deserialize(byte[] input, Class targetClass, String charset) {
        try {
            return gson.fromJson(new InputStreamReader(new ByteArrayInputStream(input), charset), targetClass);
        } catch (UnsupportedEncodingException e) {
            return new IllegalArgumentException(e);
        }
    }

    @Override
    public String type() {
        return "json";
    }
}

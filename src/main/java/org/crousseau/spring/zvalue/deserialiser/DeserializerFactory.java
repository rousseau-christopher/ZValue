package org.crousseau.spring.zvalue.deserialiser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class DeserializerFactory {

    private Map<String, Deserializer> deserializers = new HashMap<>();

    @Autowired
    public DeserializerFactory(List<Deserializer> deserializerList) {
        for (Deserializer deserializer : deserializerList) {
            deserializers.put(deserializer.type(), deserializer);
        }
    }

    public Deserializer getForSourceType(String type) {
        return deserializers.get(type);
    }
}

package org.crousseau.spring.zvalue;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;

import javax.inject.Inject;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.crousseau.spring.zvalue.deserialiser.Deserializer;
import org.crousseau.spring.zvalue.deserialiser.DeserializerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class ZValueUpdater {
    private final Logger logger = LoggerFactory.getLogger(ZValueUpdater.class);

    @Inject
    private CuratorFramework curatorFramework;

    @Inject
    private DeserializerFactory deserializerFactory;

    ZValueUpdater watch(final TargetMethod targetMethod) {
        try {
            final NodeCache nodeCache = new NodeCache(curatorFramework, targetMethod.getZValue().path());
            nodeCache.getListenable().addListener(new NodeCacheListener() {
                public void nodeChanged() throws Exception {
                    setValue(targetMethod, nodeCache);
                }
            });
            nodeCache.start(true);
            setValue(targetMethod, nodeCache);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    private void setValue(TargetMethod targetMethod, NodeCache nodeCache) throws IllegalAccessException, InvocationTargetException, UnsupportedEncodingException {
        logger.debug("Node Changed {} : {}", nodeCache.getCurrentData().getPath(), new String(nodeCache.getCurrentData().getData(), targetMethod.getZValue().charset()));
        targetMethod.call(getDeserializedObjectFromNode(targetMethod, nodeCache));
    }

    private Object getDeserializedObjectFromNode(TargetMethod targetMethod, NodeCache nodeCache) {
        Deserializer deserializer = deserializerFactory.getForSourceType(targetMethod.getZValue().type());
        return deserializer.deserialize(nodeCache.getCurrentData().getData(), targetMethod.getType(), targetMethod.getZValue().charset());
    }
}

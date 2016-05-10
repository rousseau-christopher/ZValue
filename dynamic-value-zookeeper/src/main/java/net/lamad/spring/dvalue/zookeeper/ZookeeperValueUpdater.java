package net.lamad.spring.dvalue.zookeeper;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;

import javax.inject.Inject;

import net.lamad.spring.dvalue.core.TargetMethod;
import net.lamad.spring.dvalue.core.deserialiser.Deserializer;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import net.lamad.spring.dvalue.core.deserialiser.DeserializerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class ZookeeperValueUpdater {
    private final Logger logger = LoggerFactory.getLogger(ZookeeperValueUpdater.class);

    @Inject
    private CuratorFramework curatorFramework;

    @Inject
    private DeserializerFactory deserializerFactory;

    private Deserializer deserializer;

    public ZookeeperValueUpdater watch(final TargetMethod targetMethod) {
        try {
            deserializer = deserializerFactory.getForSourceType(targetMethod.getDValueAnnotation().type());
            final NodeCache nodeCache = new NodeCache(curatorFramework, targetMethod.getAsFilePath());
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
        if (logger.isDebugEnabled()) {
            logger.debug("Node Changed {} : {}", nodeCache.getCurrentData().getPath(), new String(nodeCache.getCurrentData().getData(), targetMethod.getDValueAnnotation().charset()));
        }
        targetMethod.call(getDeserializedObjectFromNode(targetMethod, nodeCache));
    }

    private Object getDeserializedObjectFromNode(TargetMethod targetMethod, NodeCache nodeCache) {
        return deserializer.deserialize(nodeCache.getCurrentData().getData(), targetMethod.getParameterType(), targetMethod.getDValueAnnotation().charset());
    }
}

package org.crousseau.spring.zvalue;

import java.lang.reflect.Method;

import javax.inject.Inject;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
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

    ZValueUpdater watch(final Object bean, final Method method, String path) {
        try {
            final NodeCache nodeCache = new NodeCache(curatorFramework, path);
            nodeCache.getListenable().addListener(new NodeCacheListener() {
                public void nodeChanged() throws Exception {
                    String data = new String(nodeCache.getCurrentData().getData(), "UTF8");
                    logger.debug("Node Changed {} : {}", nodeCache.getCurrentData().getPath(), data);
                    method.invoke(bean, data);
                }
            });
            nodeCache.start(true);
            method.invoke(bean, new String(nodeCache.getCurrentData().getData(), "UTF8"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return this;
    }
}

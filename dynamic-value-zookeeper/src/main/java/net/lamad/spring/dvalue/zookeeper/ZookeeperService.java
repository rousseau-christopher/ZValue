package net.lamad.spring.dvalue.zookeeper;

import net.lamad.spring.dvalue.core.DValueUpdaterService;
import net.lamad.spring.dvalue.core.TargetMethod;
import org.apache.curator.framework.CuratorFramework;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.ArrayList;
import java.util.List;

@Component
public class ZookeeperService implements DValueUpdaterService {
    private final static Logger logger = LoggerFactory.getLogger(ZookeeperService.class);

    @Inject
    private CuratorFramework curatorFramework;

    @Inject
    private Provider<ZookeeperValueUpdater> valueUpdaterZookeeperProvider;

    private final List<ZookeeperValueUpdater> zkUpdater = new ArrayList<>();

    @Override
    public boolean handlingSuccessfull(TargetMethod targetMethod) {
        if (zNodeExist(targetMethod.getAsFilePath())) {
            zkUpdater.add(
                    valueUpdaterZookeeperProvider.get().watch(targetMethod)
            );
            return true;
        }
        return false;
    }

    private boolean zNodeExist(String zPath) {
        try {
            if (curatorFramework.checkExists().forPath(zPath) == null) {
                return false;
            }
        } catch (Exception e) {
            logger.error("error checking ofr existance {}", zPath, e);
        }
        return true;
    }


}

## Dynamic Value

This library add the possible to dynamically inject configuration value in component, the same way we use @Value to inject properties (or Environment variable) in component.
@DValue register a watcher and update the component as soon as it change in the source repository

Using this pattern you can dynamically update your configuration without restarting you server.

# Zookeeper implementation
Implemented, The application use Curator library

# File
Work in progress

# Consul
Need to be implemented

## Usage
Import Dvalue Configuration in your Spring configuration :

```
@Import(net.lamad.spring.dvalue.core.ZValueConfiguration.class)
```

Your context must contain a bean of type org.apache.curator.framework.CuratorFramework to work.
ex:

```
@Bean
public CuratorFramework curatorFramework() throws Exception {
    RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
    CuratorFramework curatorFramework= CuratorFrameworkFactory.newClient("localhost:2181", retryPolicy);
    curatorFramework.start();
}
```

then you can use @ZValue annotation to inject Zookepper node value in your component:

```
@ZValue(path = "/config/path")
public void setValue(String value) {
    this.value = value;
}
```

## Evolution
Currently @Zvalue support only Injection of value as String only. Futur implemenation will add:

- Deserialization of json/xml into Pojo : WIP (need to clean/refactor the code
- spring boot for autoconfiguration
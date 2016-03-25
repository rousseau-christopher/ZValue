## Zookeeper Value

This library add the possible to use Zookeeper to inject configuration in component, the same way we use @Value to inject properties (or Envirronement variable) in component.
@ZValue register a watcher and update the component as soon is equivalent ZNode is updated in zookeeper.

Using this pattern you can dynamically update your configuration without restarting you server.

The application use Curator library

## Usage
Import Zvalue Configuration in your Spring configuration :

```
@Import(org.springframework.zvalue.ZValueConfiguration.class)
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

- Deserialization of json/xml into Pojo
- spring boot for autoconfiguration
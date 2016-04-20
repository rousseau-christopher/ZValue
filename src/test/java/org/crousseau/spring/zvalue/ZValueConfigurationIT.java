package org.crousseau.spring.zvalue;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.test.TestingServer;
import org.crousseau.spring.zvalue.deserialiser.Deserializer;
import org.crousseau.spring.zvalue.deserialiser.GsonDeserializer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ZValueConfigurationIT.TestConfiguration.class)
public class ZValueConfigurationIT {

    @Autowired
    private MyComponent myComponent;

    @Autowired
    private CuratorFramework curatorFramework;

    @Before
    public void init() throws Exception {
        curatorFramework.setData().forPath("/config/path", "initialValue".getBytes());
        curatorFramework.setData().forPath("/config/anotherProperty", "property".getBytes());
        curatorFramework.setData().forPath("/config/json", "{ \"timeout\" : 60}".getBytes());
        Thread.sleep(50);
    }

    @Test
    public void should_update_value() throws Exception {
        // Given
        assertThat(myComponent.getValue()).isEqualTo("initialValue");
        assertThat(myComponent.getAnotherProperty()).isEqualTo("property");
        assertThat(myComponent.getMyParam().getTimeout()).isEqualTo(60);

        // When
        curatorFramework.setData().forPath("/config/path", "changedValue".getBytes());
        curatorFramework.setData().forPath("/config/anotherProperty", "changedProperty".getBytes());
        curatorFramework.setData().forPath("/config/json", "{ \"timeout\" : 120}".getBytes());
        Thread.sleep(50);
        // Then

        assertThat(myComponent.getValue()).isEqualTo("changedValue");
        assertThat(myComponent.getAnotherProperty()).isEqualTo("changedProperty");
        assertThat(myComponent.getMyParam().getTimeout()).isEqualTo(120);
    }

    @Test
    public void should_not_failed() throws Exception {
        // Given
        assertThat(myComponent.getMyParam().getTimeout()).isEqualTo(60);

        // When
        curatorFramework.setData().forPath("/config/json", "{ \"timeout\" : \"azerty\"}".getBytes());
        Thread.sleep(50);
        // Then
        assertThat(myComponent.getMyParam().getTimeout()).isEqualTo(60);
    }

    @Configuration
    @Import(ZValueConfiguration.class)
    public static class TestConfiguration {

        @Bean
        public MyComponent myComponent() {
            return new MyComponent();
        }

        @Bean
        public Deserializer jacksonDeserializer() {
            return new GsonDeserializer();
        }

        @Bean
        public CuratorFramework curatorFramework() throws Exception {
            TestingServer testingServer = new TestingServer();

            CuratorFramework curatorFramework = CuratorFrameworkFactory.builder()
                    .namespace("prod")
                    .connectString(testingServer.getConnectString())
                    .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                    .build();
            curatorFramework.start();

            curatorFramework.create().creatingParentsIfNeeded().forPath("/config/path", "initialValue".getBytes());
            curatorFramework.create().creatingParentsIfNeeded().forPath("/config/anotherProperty", "property".getBytes());
            curatorFramework.create().creatingParentsIfNeeded().forPath("/config/json", "{ \"timeout\" : 60}".getBytes());
            return curatorFramework;
        }
    }

    public static class MyComponent {
        private String value;
        private String anotherProperty;
        private MyParam myParam;

        @ZValue(path = "/config/path")
        public void setValue(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        @ZValue(path = "/config/anotherProperty")
        public void setAnotherProperty(String anotherValue) {
            this.anotherProperty = anotherValue;
        }

        public String getAnotherProperty() {
            return anotherProperty;
        }

        @ZValue(path = "/config/json", type = "json")
        public void setMyParam(MyParam myParam) {
            this.myParam = myParam;
        }

        public MyParam getMyParam() {
            return myParam;
        }
    }

    public static class MyParam {
        private int timeout;

        public int getTimeout() {
            return timeout;
        }

        public void setTimeout(int timeout) {
            this.timeout = timeout;
        }
    }

}
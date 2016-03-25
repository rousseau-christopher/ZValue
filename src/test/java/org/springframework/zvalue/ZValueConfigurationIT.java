package org.springframework.zvalue;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.test.TestingServer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.ZValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ZValueConfigurationIT.TestConfiguration.class)
public class ZValueConfigurationIT {

    @Autowired
    private MyComponent myComponent;

    @Autowired
    private CuratorFramework curatorFramework;

    @Test
    public void should_update_value() throws Exception {
        // Given
        assertThat(myComponent.getValue()).isEqualTo("initialValue");
        assertThat(myComponent.getAnotherProperty()).isEqualTo("property");

        // When
        curatorFramework.setData().forPath("/config/path", "myValue".getBytes());
        curatorFramework.setData().forPath("/config/anotherProperty", "changedProperty".getBytes());
        Thread.sleep(50);
        // Then

        assertThat(myComponent.getValue()).isEqualTo("myValue");
        assertThat(myComponent.getAnotherProperty()).isEqualTo("changedProperty");
    }

    @Configuration
    @Import(ZValueConfiguration.class)
    public static class TestConfiguration {

        @Bean
        public MyComponent myComponent() {
            return new MyComponent();
        }

        @Bean
        public CuratorFramework curatorFramework() throws Exception {
            TestingServer testingServer = new TestingServer();

            RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
            CuratorFramework curatorFramework= CuratorFrameworkFactory.newClient(testingServer.getConnectString(), retryPolicy);
            curatorFramework.start();

            curatorFramework.create().creatingParentsIfNeeded().forPath("/config/path", "initialValue".getBytes());
            curatorFramework.create().creatingParentsIfNeeded().forPath("/config/anotherProperty", "property".getBytes());
            return curatorFramework;
        }
    }

    public static class MyComponent {
        private String value;
        private String anotherProperty;

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

    }

}
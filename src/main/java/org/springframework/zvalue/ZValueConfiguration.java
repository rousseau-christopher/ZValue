package org.springframework.zvalue;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ComponentScan(basePackages = "org.springframework.zvalue")
@Import(ZValueConfiguration.class)
public class ZValueConfiguration {
}

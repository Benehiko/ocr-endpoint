package com.benehiko;

import com.company.acs.AcsApplication;
import com.company.acs.AcsApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

@Configuration
@EnableConfigurationProperties
public class Setup {

    @Bean
    public AcsApplication createApplication() {
        return new AcsApplicationBuilder().withIpAddress("192.168.122.5").withUsername("alano").withPassword("Iknow").build();
    }

    @Bean
    public Jackson2ObjectMapperBuilder jacksonBuilder() {
        return new Jackson2ObjectMapperBuilder().indentOutput(true);
    }

}

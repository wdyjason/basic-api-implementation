package com.thoughtworks.rslist;

import com.thoughtworks.rslist.service.RsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebConfig {

    @Bean
    public RsService rsService() {
        return new RsService();
    }
}

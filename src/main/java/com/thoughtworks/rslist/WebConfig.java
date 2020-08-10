package com.thoughtworks.rslist;

import com.thoughtworks.rslist.repository.RsEventRepository;
import com.thoughtworks.rslist.repository.UserRepository;
import com.thoughtworks.rslist.repository.VoteRepository;
import com.thoughtworks.rslist.service.RsService;
import com.thoughtworks.rslist.service.VoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebConfig {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VoteRepository voteRepository;

    @Autowired
    private RsEventRepository rsEventRepository;

    @Bean
    public RsService rsService() {
        return new RsService(userRepository, voteRepository, rsEventRepository);
    }

    @Bean
    public VoteService voteService() {
        return new VoteService(voteRepository, userRepository);
    }

}

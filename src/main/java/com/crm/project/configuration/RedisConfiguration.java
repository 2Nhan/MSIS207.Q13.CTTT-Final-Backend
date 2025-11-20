package com.crm.project.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.net.URI;


@Configuration
public class RedisConfiguration {

    @Value("${spring.data.redis.url}")
    private String url;

    @Value("${spring.data.redis.password}")
    private String password;

    @Bean
    public LettuceConnectionFactory lettuceConnectionFactory() throws Exception {
        URI uri = new URI(url);

        String host = uri.getHost();
        int port = uri.getPort();

        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(host, port);

        config.setPassword(password);

        LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder().useSsl().build();

        return new LettuceConnectionFactory(config, clientConfig);
    }

    @Bean
    public StringRedisTemplate stringRedisTemplate() throws Exception {
        return new StringRedisTemplate(lettuceConnectionFactory());
    }
}

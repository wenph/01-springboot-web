package com.example.lock;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.SentinelServersConfig;
import org.redisson.config.SingleServerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.apache.commons.lang3.StringUtils;

@Configuration
@ConditionalOnClass(Config.class)
@EnableConfigurationProperties(RedissonProperties.class)
public class RedissonAutoConfiguration {

    @Autowired
    private RedissonProperties redissonProperties;

    @Bean
    @ConditionalOnProperty(name="redission.master-name")
    RedissonClient redissonSentinel(){
        Config config = new Config();
        SentinelServersConfig sentinelServersConfig = config.useSentinelServers()
            .setDatabase(redissonProperties.getDatabase())
            .addSentinelAddress(redissonProperties.getSentinelAddresses())
            .setMasterName(redissonProperties.getMasterName())
            .setTimeout(redissonProperties.getTimeout())
            .setMasterConnectionPoolSize(redissonProperties.getMasterConnectionPoolSize())
            .setSlaveConnectionPoolSize(redissonProperties.getSlaveConnectionPoolSize());
        if(StringUtils.isNotBlank(redissonProperties.getPassword())){
            sentinelServersConfig.setPassword(redissonProperties.getPassword());
        }
        return Redisson.create(config);
    }

    @Bean
    @ConditionalOnProperty(name = "redisson.address")
    RedissonClient redissonClient(){
        Config config = new Config();
        SingleServerConfig singleServerConfig = config.useSingleServer()
            .setAddress("redis://" + StringUtils.trim(redissonProperties.getAddress()))
            .setDatabase(redissonProperties.getDatabase())
            .setTimeout(redissonProperties.getTimeout())
            .setConnectionPoolSize(redissonProperties.getConnectionPoolSize())
            .setConnectionMinimumIdleSize(redissonProperties.getConnectionMinimumIdleSize());
        if( StringUtils.isNotEmpty(redissonProperties.getPassword()) ){
            singleServerConfig.setPassword(redissonProperties.getPassword());
        }
        return Redisson.create(config);
    }


    @Bean
    @ConditionalOnBean(RedissonClient.class)
    DistributedLock distributedLock(RedissonClient redissonClient){
        DistributedLock lock = new RedissonDistributedLocker(redissonClient);
        return lock;
    }
}


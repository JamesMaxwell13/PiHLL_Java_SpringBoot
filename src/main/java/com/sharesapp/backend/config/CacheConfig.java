package com.sharesapp.backend.config;

import com.sharesapp.backend.utils.cache.GenericCache;
import com.sharesapp.backend.utils.cache.MyCache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class CacheConfig<K, V> {

    @Bean
    @Scope("prototype")
    public GenericCache<K, V> cache() {
        return new MyCache<>();
    }
}

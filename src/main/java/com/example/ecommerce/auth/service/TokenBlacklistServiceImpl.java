package com.example.ecommerce.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class TokenBlacklistServiceImpl implements TokenBlacklistService {

    private final static String BLACKLISTED_TOKEN_KEY_PREFIX = "blacklisted:";

    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public void blacklistToken(String token, long timeout, TimeUnit unit) {
        redisTemplate.opsForValue().set(
                BLACKLISTED_TOKEN_KEY_PREFIX + token,
                token,
                timeout,
                unit
        );
    }

    @Override
    public boolean isTokenBlacklisted(String token) {
        return redisTemplate.hasKey(BLACKLISTED_TOKEN_KEY_PREFIX + token);
    }

}

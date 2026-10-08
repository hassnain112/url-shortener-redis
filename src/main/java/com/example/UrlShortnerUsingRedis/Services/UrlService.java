package com.example.UrlShortnerUsingRedis.Services;

import com.example.UrlShortnerUsingRedis.DTOs.ResponseDTO;
import com.example.UrlShortnerUsingRedis.ModelClasses.Url;
import com.example.UrlShortnerUsingRedis.Repositories.UrlRepository;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;
import java.util.Optional;

@Service
public class UrlService {
    private final StringRedisTemplate redis;
    private static final Duration CACHE_TTL = Duration.ofHours(24);

    private final UrlRepository repository;
    public static final  String KEY_PREFIX = "url:";
    private final SecureRandom random = new SecureRandom();


    public UrlService(UrlRepository repository,StringRedisTemplate redis) {
        this.repository = repository;
        this.redis=redis;
    }

    public ResponseEntity<ResponseDTO> createShortUrl(String url) {
        Url urlEntity = new Url();
        String shortCode = generateShortCode();


        urlEntity.setOrignalUrl(url);
        urlEntity.setShortUrl(shortCode);

        repository.save(urlEntity);
        redis.opsForValue().set(KEY_PREFIX+shortCode,url,CACHE_TTL);

        ResponseDTO response = new ResponseDTO(shortCode);

        return ResponseEntity.ok(response);

    }

    public String generateShortCode() {
        StringBuilder code = new StringBuilder();
        String source = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        for (int i = 0; i < 6; i++) {
            int index = random.nextInt(source.length());
            code.append(source.charAt(index));
        }
        return code.toString();

    }
    public String getOrignalSource(String url){
        String originalUrl = orignalUrlFetcher(url);
        return originalUrl;



    }
    public String orignalUrlFetcher(String url){
        String key =KEY_PREFIX + url;
        String cached = redis.opsForValue().get(key);
        if(cached!= null){
            return cached;
        }
        Url parent  = repository.findByShortUrl(url).orElseThrow(() -> new RuntimeException("URL not found"));
        String orignalUrl = parent.getOrignalUrl();
        redis.opsForValue().set(key, orignalUrl, CACHE_TTL);

        return parent.getOrignalUrl();



    }




}

package com.example.UrlShortnerUsingRedis.Services;

import com.example.UrlShortnerUsingRedis.DTOs.ResponseDTO;
import com.example.UrlShortnerUsingRedis.ModelClasses.Url;
import com.example.UrlShortnerUsingRedis.Repositories.UrlRepository;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.Optional;

@Service
public class UrlService {
    private final StringRedisTemplate redis;
    private static final Duration CACHE_TTL = Duration.ofHours(24);

    private final UrlRepository repository;
    public static final  String KEY_PREFIX = "url:";
    public static final  String CLICK_PREFIX = "Click:";
    private final SecureRandom random = new SecureRandom();


    public UrlService(UrlRepository repository,StringRedisTemplate redis) {
        this.repository = repository;
        this.redis=redis;
    }

    public ResponseEntity<ResponseDTO> createShortUrl(String url) {
        Url urlEntity = new Url();
        String shortCode = generateShortCode();


        while (repository.findByShortUrl(shortCode).isPresent()) {
            shortCode = generateShortCode();
        }




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
        trackClicks(url);
        return originalUrl;



    }
    public String orignalUrlFetcher(String url){
        String key =KEY_PREFIX + url;
        String cached = redis.opsForValue().get(key);
        if(cached!= null){
            return cached;
        }
        Url parent  = repository.findByShortUrl(url).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"NOt Found"));
        String orignalUrl = parent.getOrignalUrl();
        redis.opsForValue().set(key, orignalUrl, CACHE_TTL);

        return parent.getOrignalUrl();



    }
    public void trackClicks(String shortCode){
        redis.opsForValue().increment(CLICK_PREFIX + shortCode);
    }
    public long getClicks(String shortCode){
        String value = redis.opsForValue().get(CLICK_PREFIX+shortCode);
        return value == null ? 0L: Long.parseLong(value);

    }
    public void checkValidityOfUrl(String url){
        try{
            URI uri = new URI(url);
            if (uri.getHost() == null || !(uri.getScheme().equalsIgnoreCase("http") ||
                    uri.getScheme().equalsIgnoreCase("https"))){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Enter a valid url");

            }
        }catch (URISyntaxException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid URL"
            );
        }
    }





}

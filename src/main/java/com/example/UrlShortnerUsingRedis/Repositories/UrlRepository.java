package com.example.UrlShortnerUsingRedis.Repositories;

import com.example.UrlShortnerUsingRedis.ModelClasses.Url;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UrlRepository extends JpaRepository<Url,Long> {
    Optional<Url> findByShortUrl(String shortUrl);
}

package com.example.UrlShortnerUsingRedis.Controllers;

import com.example.UrlShortnerUsingRedis.DTOs.RequestDTO;
import com.example.UrlShortnerUsingRedis.DTOs.ResponseDTO;
import com.example.UrlShortnerUsingRedis.Services.UrlService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/url")

public class UrlController {
    private final UrlService service;
    public UrlController(UrlService service){
        this.service=service;
    }
    @PostMapping
    public ResponseEntity<ResponseDTO> createShortUrl(@RequestBody RequestDTO request){
        return service.createShortUrl(request.url());

    }
    @GetMapping("/{url}")
    public ResponseEntity<Void> redirect(@PathVariable String url){
        String originalUrl = service.getOrignalSource(url);
        return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(originalUrl)).build();
    }

}

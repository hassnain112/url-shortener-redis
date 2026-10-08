package com.example.UrlShortnerUsingRedis.ModelClasses;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.validation.Valid;
import lombok.*;
import jakarta.persistence.Id;

@Getter
@Setter@NoArgsConstructor
@AllArgsConstructor
@Entity


public class Url {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String orignalUrl;
    private String shortUrl;
}

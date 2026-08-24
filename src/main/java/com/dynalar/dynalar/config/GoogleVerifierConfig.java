package com.dynalar.dynalar.config;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
public class GoogleVerifierConfig {

    @Value("${google.client-id}")
    private String googleClientId;

    @Value("${google.client-id-android}")
    private String googleClientIdAndroid;

    @Bean
    public GoogleIdTokenVerifier googleIdTokenVerifier() {
        return new GoogleIdTokenVerifier.Builder(
                new NetHttpTransport(),
                GsonFactory.getDefaultInstance())
                .setAudience(Arrays.asList(
                        googleClientId,
                        googleClientIdAndroid
                ))
                .build();
    }
}
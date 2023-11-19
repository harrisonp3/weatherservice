package com.liveintent.weather.weatherservice.service;

import org.springframework.stereotype.Service;

@Service
public class CredentialService {
    public String getWeatherbitApiCredential() {
        //@todo encrypt/decrypt this - pulling into its own service class because we'd want to store/retrieve this securely
        return "361873f7ccfe4de08d96b649c583eb27";
    }
}

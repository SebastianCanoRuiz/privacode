package com.sebastiancano.privatecode.config;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
@Configuration
@ConfigurationProperties(prefix = "utils.privacode")
public class ConfigPrivaCode {
    private String sensitiveHeaders = "Authorization,Secret,Token";
    private String maskValue = "*";
    private int minLength = 4;
    private boolean keepStart = true;
    private int keepStartCount = 2;
    private boolean keepEnd = true;
    private int keepEndCount = 2;

    private List<String> sensitiveHeadersList = new ArrayList<>();

    @PostConstruct
    public void init() {
        if (!sensitiveHeaders.isEmpty()) {
            sensitiveHeadersList = Arrays.asList(sensitiveHeaders.split(","));
        }
    }
}

package com.blockchain.larisa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication(scanBasePackages = {"com.blockchain.larisa"})
@PropertySource(value = {"classpath:private.properties", "classpath:singlekline.properties", "classpath:grid.properties", "classpath:db.properties"})
@ServletComponentScan(basePackages = {"com.blockchain.larisa"})
public class LarisaLauncher {
    public static void main(String[] args) {

        SpringApplication.run(LarisaLauncher.class, args);
    }
}

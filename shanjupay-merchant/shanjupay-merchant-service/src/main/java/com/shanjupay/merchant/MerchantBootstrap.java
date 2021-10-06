package com.shanjupay.merchant;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.shanjupay.merchant.mapper")
public class MerchantBootstrap {
    public static void main(String[] args) {
        SpringApplication.run(MerchantBootstrap.class, args);
    }
}

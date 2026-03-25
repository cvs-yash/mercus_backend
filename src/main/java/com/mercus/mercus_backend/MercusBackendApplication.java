package com.mercus.mercus_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MercusBackendApplication {

    public static void main(String[] args) {

        SpringApplication.run(MercusBackendApplication.class, args);

        System.out.println("Backend running successfully...");
    }

}

package com.example.islab1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
@ServletComponentScan
public class IsLab1Application {

    public static void main(String[] args) {
        SpringApplication.run(IsLab1Application.class, args);
    }

}

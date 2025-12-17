package com.ecom.EcomSB.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @Tag(name = "Test API", description = "API for Test the Elastic Beanstalk where our Backend is Deployed")
    @GetMapping("/")
    public String getHello(){
        return "Backend is Running";
    }
}

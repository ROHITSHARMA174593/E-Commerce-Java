package com.secure.SecurityDemo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GrettingsController {

    @GetMapping("/hello")
    public String sayHello(){
        return "Hello";
    }

}

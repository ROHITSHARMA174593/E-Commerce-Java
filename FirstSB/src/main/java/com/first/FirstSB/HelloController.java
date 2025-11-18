package com.first.FirstSB;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    @GetMapping("/")
    public String firstPage(){
        return "GO - TO /hello route to see Something New!!!";
    }

    @GetMapping("/hello")
    public String printHello(){
        return "Hello World";
    }

    @PostMapping("/nnn")
    public String printName(@RequestBody String name){
        return "Hello "+name;
    }
}

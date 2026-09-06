package com.railgo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class getrequest {
    @GetMapping("/hello")
    public String sayHello() {
        return "Hello, this is a normal GET request! 123  456 123";
    }
}

package com.online.tienda_empeno.controller;

import com.online.tienda_empeno.dto.LoginRequest;
import com.online.tienda_empeno.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/login")
public class LoginController {

    @Autowired
    private LoginService loginService;

    @PostMapping
    public String login(@RequestBody LoginRequest request){
        return loginService.login(request);
    }
}

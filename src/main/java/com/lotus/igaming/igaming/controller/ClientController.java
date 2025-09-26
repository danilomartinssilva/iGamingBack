package com.lotus.igaming.igaming.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/clients/")
public class ClientController {
    @GetMapping("/")
    public String getAllClients(){
        return "Ola";
    }
}

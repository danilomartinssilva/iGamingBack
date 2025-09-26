package com.lotus.igaming.igaming.controller;

import java.util.List;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lotus.igaming.igaming.Client;
import com.lotus.igaming.igaming.repositories.ClientRepository;

@RestController
@RequestMapping("/api/clients/")
public class ClientController {
    private final ClientRepository clientRepository;

    public ClientController(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @GetMapping("/")
    public ResponseEntity getAllClients() {

        List<Client> clients = clientRepository.findAll();
        System.out.println(clients);

        if (clients.isEmpty()) {
            return ResponseEntity.status(HttpStatusCode.valueOf(204)).body(clients);
        }

        return ResponseEntity.status(HttpStatusCode.valueOf(200)).body(clients);
    }
}

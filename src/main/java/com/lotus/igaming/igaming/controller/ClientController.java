package com.lotus.igaming.igaming.controller;

import java.util.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.lotus.igaming.igaming.Client;
import com.lotus.igaming.igaming.repositories.ClientRepository;

@RestController
@RequestMapping("/api/clients/")
public class ClientController {
    private final ClientRepository clientRepository;

    public ClientController(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @GetMapping
    public ResponseEntity<Page<Client>> getAllClients(
            @PageableDefault(page = 0, size = 20) Pageable pageable) {

        Page<Client> clientsPage = clientRepository.findAll(pageable);
        System.out.println("Página de Clientes: " + clientsPage.getContent().size());

        if (clientsPage.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(clientsPage);
        }

        return ResponseEntity.ok(clientsPage);
    }

    @PostMapping("/")
    public ResponseEntity createClient(@RequestBody Client client) {

        clientRepository.save(client);
        return ResponseEntity.status(HttpStatusCode.valueOf(201)).body(client);
    }

    @GetMapping("/{id}")
    public Client getNoteById(@PathVariable(value = "id") Long id) {
        System.out.println(id + "id");
        return clientRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteClient(@PathVariable(value = "id") Long id) {
        clientRepository.deleteById(id);
        return ResponseEntity.status(HttpStatusCode.valueOf(204)).body(null);
    }

    @PatchMapping("/{id}")
    public ResponseEntity updateClient(@PathVariable(value = "id") Long id, @RequestBody Client client) {
        Optional<Client> clientData = clientRepository.findById(id);
        if (clientData.isPresent()) {
            Client _client = clientData.get();
            _client.setName(client.getName());
            _client.setEmail(client.getEmail());
            _client.setPhone(client.getPhone());
            _client.setBirthday(client.getBirthday());
            _client.setStatus(client.getStatus());
            _client.setCurrency(client.getCurrency());
            _client.setBalance(client.getBalance());
            clientRepository.save(_client);
            return ResponseEntity.status(HttpStatusCode.valueOf(200)).body(_client);
        } else {
            return ResponseEntity.status(HttpStatusCode.valueOf(404)).body(null);
        }
    }
}

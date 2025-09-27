package com.lotus.igaming.igaming.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lotus.igaming.igaming.Client;
import com.lotus.igaming.igaming.controller.ClientController;
import com.lotus.igaming.igaming.repositories.ClientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.postgresql.util.PSQLException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;

import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;



import java.util.Arrays;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;




@WebMvcTest(controllers = ClientController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
public class ClientControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockitoBean
    private ClientRepository clientRepository;
    private Client client1;
    private Client client2;
    private List<Client> clientList;
    private Page<Client> clientPage;

    @BeforeEach
    void initial(){
        client1 = new Client();
        client1.setId(1L);
        client1.setName("Danilo Martins");
        client1.setEmail("danilomartins.pacs@gmail.com");
        client1.setBalance(3.399);
        client1.setStatus(false);
        client1.setCurrency("BRL");

        client2 = new Client();
        client2.setId(2L); // ID corrigido
        client2.setName("Danilo Martins da Silva");
        client2.setEmail("danilomartins1.pacs@gmail.com");
        client2.setBalance(3.399);
        client2.setStatus(false);
        client2.setCurrency("BRL");
        clientList = Arrays.asList(client1,client2);
        clientPage = new PageImpl<>(clientList, PageRequest.of(0,20),clientList.size());
    }

    @Test
    void getAllClients_ShouldReturnAllClientswithPagination() throws Exception{
        when(clientRepository.findAll(any(Pageable.class))).thenReturn(clientPage);
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.get("/api/clients/")
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].name").value(client1.getName()))
                .andExpect(jsonPath("$.content[0].email").value(client1.getEmail()))
                .andExpect(jsonPath("$.content[1].id").value(2));

    }

    @Test
    void deleteClient_ShouldReturn204NoContent() throws Exception {
        Long clientIdToDelete = 1L;
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/clients/{id}", clientIdToDelete)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        verify(clientRepository).deleteById(clientIdToDelete);
    }

    @Test
    void updateClient_ShouldReturn404NotFound() throws Exception {
        Long clientToUpdateId = 99L;

        Client clientUpdateDetails = new Client();
        clientUpdateDetails = client1;
        clientUpdateDetails.setEmail("teste@naoencontrado.com");


        when(clientRepository.findById(clientToUpdateId)).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.patch("/api/clients/{id}", clientToUpdateId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clientUpdateDetails)))

                .andExpect(status().isNotFound()
                );
    }

    @Test
    void updateClient_ShouldReturn200() throws Exception {
        Long clientToUpdateId = 1L;
        Client clientUpdateDetails = new Client();
        clientUpdateDetails.setEmail("teste.atualizado@email.com");
        clientUpdateDetails.setName("Nome Atualizado");
        clientUpdateDetails.setId(clientToUpdateId);
        clientUpdateDetails.setBalance(client1.getBalance());
        clientUpdateDetails.setCurrency(client1.getCurrency());
        clientUpdateDetails.setStatus(client1.getStatus());

        when(clientRepository.findById(clientToUpdateId)).thenReturn(Optional.of(client1));

        Client clientAfterUpdate = new Client();
        clientAfterUpdate.setId(client1.getId());
        clientAfterUpdate.setName(clientUpdateDetails.getName());
        clientAfterUpdate.setEmail(clientUpdateDetails.getEmail());
        clientAfterUpdate.setPhone(client1.getPhone());
        clientAfterUpdate.setBirthday(client1.getBirthday());
        clientAfterUpdate.setStatus(client1.getStatus());
        clientAfterUpdate.setCurrency(client1.getCurrency());
        clientAfterUpdate.setBalance(client1.getBalance());

        when(clientRepository.save(any(Client.class))).thenReturn(clientAfterUpdate);

        mockMvc.perform(MockMvcRequestBuilders.patch("/api/clients/{id}", clientToUpdateId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clientUpdateDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(clientToUpdateId))
                .andExpect(jsonPath("$.name").value(clientUpdateDetails.getName()))
                .andExpect(jsonPath("$.email").value(clientUpdateDetails.getEmail()));
    }


    @Test
    void inserClient_shouldReturn400whenBalanceLess1() throws Exception {

        Client clientToCreate = spy(new Client());
        doNothing().when(clientToCreate).setBalance(0.0);

        clientToCreate.setEmail("teste.atualizado@email.com");
        clientToCreate.setName("Nome Atualizado");
        clientToCreate.setBalance(0.0);
        clientToCreate.setCurrency(client1.getCurrency());
        clientToCreate.setStatus(client1.getStatus());


        doThrow(new IllegalArgumentException("Balance must be greater than 0"))
                .when(clientRepository).save(any(Client.class));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/clients/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clientToCreate)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void inserClient_shouldReturn209whenHasSameEmailOnDatabase() throws Exception {
        Client clientToCreate = new Client();
        clientToCreate.setEmail(client1.getEmail());
        clientToCreate.setName("Nome Atualizado");
        clientToCreate.setBalance(client1.getBalance());
        clientToCreate.setCurrency(client1.getCurrency());
        clientToCreate.setStatus(client1.getStatus());
        PSQLException psqlException = new PSQLException(
                "ERROR: duplicate key value violates unique constraint \"clients_email_key\"",
                org.postgresql.util.PSQLState.UNIQUE_VIOLATION
        );

        doThrow(new DataIntegrityViolationException("Clients email already exists",
                psqlException))
                .when(clientRepository).save(any(Client.class));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/clients/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clientToCreate)))
                .andExpect(status().isConflict());
    }

    @Test
    void inserClient_shouldReturn201() throws Exception {
        Long clientToUpdateId = 3L;
        Client clientToCreate = new Client();
        clientToCreate.setEmail("teste.atualizado@email.com");
        clientToCreate.setName("Nome Atualizado");
        clientToCreate.setId(clientToUpdateId);
        clientToCreate.setBalance(client1.getBalance());
        clientToCreate.setCurrency(client1.getCurrency());
        clientToCreate.setId(3L);
        clientToCreate.setStatus(client1.getStatus());

        when(clientRepository.save(any(Client.class))).thenReturn(clientToCreate);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/clients/" )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clientToCreate)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(clientToUpdateId))
                .andExpect(jsonPath("$.name").value(clientToCreate.getName()))
                .andExpect(jsonPath("$.email").value(clientToCreate.getEmail()));
    }

}
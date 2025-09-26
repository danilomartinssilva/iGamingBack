package com.lotus.igaming.igaming.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lotus.igaming.igaming.Client;

public interface ClientRepository extends JpaRepository<Client, Long> {

}

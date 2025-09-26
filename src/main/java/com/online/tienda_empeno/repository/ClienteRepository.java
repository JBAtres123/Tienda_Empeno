package com.online.tienda_empeno.repository;

import com.online.tienda_empeno.entity.Cliente;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Integer> {
}


package com.online.tienda_empeno.repository;

import com.online.tienda_empeno.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Integer> {
    Cliente findByEmailCliente(String emailCliente);
}

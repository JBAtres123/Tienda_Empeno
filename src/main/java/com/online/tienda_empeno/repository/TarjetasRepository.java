package com.online.tienda_empeno.repository;

import com.online.tienda_empeno.entity.Tarjetas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TarjetasRepository extends JpaRepository<Tarjetas, Integer> {

    // Buscar tarjetas de un cliente
    @Query("SELECT t FROM Tarjetas t WHERE t.cliente.idCliente = :idCliente")
    List<Tarjetas> findByClienteId(@Param("idCliente") Integer idCliente);
}
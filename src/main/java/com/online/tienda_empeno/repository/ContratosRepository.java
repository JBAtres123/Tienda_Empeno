package com.online.tienda_empeno.repository;

import com.online.tienda_empeno.entity.Contratos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ContratosRepository extends JpaRepository<Contratos, Integer> {

    // Buscar contrato por id de préstamo
    @Query("SELECT c FROM Contratos c WHERE c.prestamo.idPrestamo = :idPrestamo")
    Contratos findByPrestamoId(@Param("idPrestamo") Integer idPrestamo);

    // Buscar contratos de un cliente
    @Query("SELECT c FROM Contratos c WHERE c.prestamo.cliente.idCliente = :idCliente")
    List<Contratos> findByClienteId(@Param("idCliente") Integer idCliente);

    // Buscar contratos pendientes de un cliente
    @Query("SELECT c FROM Contratos c WHERE c.prestamo.cliente.idCliente = :idCliente AND c.idEstado = 10")
    List<Contratos> findPendientesByClienteId(@Param("idCliente") Integer idCliente);
}
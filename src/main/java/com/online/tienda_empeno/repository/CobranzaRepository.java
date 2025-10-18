package com.online.tienda_empeno.repository;

import com.online.tienda_empeno.entity.Cobranza;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CobranzaRepository extends JpaRepository<Cobranza, Integer> {

    // Buscar cobranza por id de pago
    @Query("SELECT c FROM Cobranza c WHERE c.pagoPrestamo.idPagoPrestamo = :idPagoPrestamo")
    Cobranza findByPagoPrestamoId(@Param("idPagoPrestamo") Integer idPagoPrestamo);

    // Buscar cobranzas de un cliente
    @Query("SELECT c FROM Cobranza c WHERE c.pagoPrestamo.prestamo.cliente.idCliente = :idCliente")
    List<Cobranza> findByClienteId(@Param("idCliente") Integer idCliente);

    // Buscar cobranzas asignadas a un cobrador
    @Query("SELECT c FROM Cobranza c WHERE c.pagoPrestamo.administrador.idAdmin = :idAdmin")
    List<Cobranza> findByCobradorId(@Param("idAdmin") Integer idAdmin);
}
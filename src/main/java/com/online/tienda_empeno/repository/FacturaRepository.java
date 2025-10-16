package com.online.tienda_empeno.repository;

import com.online.tienda_empeno.entity.Factura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FacturaRepository extends JpaRepository<Factura, Integer> {

    // Buscar factura por id de pago
    @Query("SELECT f FROM Factura f WHERE f.pagoPrestamo.idPagoPrestamo = :idPagoPrestamo")
    Factura findByPagoPrestamoId(@Param("idPagoPrestamo") Integer idPagoPrestamo);

    // Buscar facturas de un cliente
    @Query("SELECT f FROM Factura f WHERE f.pagoPrestamo.prestamo.cliente.idCliente = :idCliente")
    List<Factura> findByClienteId(@Param("idCliente") Integer idCliente);
}
package com.online.tienda_empeno.repository;

import com.online.tienda_empeno.entity.PagosPrestamo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PagosPrestamoRepository extends JpaRepository<PagosPrestamo, Integer> {

    // Buscar pagos de un préstamo
    @Query("SELECT p FROM PagosPrestamo p WHERE p.prestamo.idPrestamo = :idPrestamo")
    List<PagosPrestamo> findByPrestamoId(@Param("idPrestamo") Integer idPrestamo);

    // Buscar pagos de un cliente
    @Query("SELECT p FROM PagosPrestamo p WHERE p.prestamo.cliente.idCliente = :idCliente")
    List<PagosPrestamo> findByClienteId(@Param("idCliente") Integer idCliente);

    // Buscar pagos por préstamo (método alternativo)
    List<PagosPrestamo> findByPrestamoIdPrestamo(Integer idPrestamo);
}
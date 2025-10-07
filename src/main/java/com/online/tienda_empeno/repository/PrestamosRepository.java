package com.online.tienda_empeno.repository;

import com.online.tienda_empeno.entity.Prestamos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PrestamosRepository extends JpaRepository<Prestamos, Integer> {

    // Buscar préstamos por estado
    @Query("SELECT p FROM Prestamos p WHERE p.idEstado = :idEstado")
    List<Prestamos> findByEstado(@Param("idEstado") Integer idEstado);

    // Buscar préstamos de un cliente
    @Query("SELECT p FROM Prestamos p WHERE p.cliente.idCliente = :idCliente")
    List<Prestamos> findByClienteId(@Param("idCliente") Integer idCliente);

    // Buscar préstamos evaluados por un admin
    @Query("SELECT p FROM Prestamos p WHERE p.administrador.idAdmin = :idAdmin")
    List<Prestamos> findByAdminId(@Param("idAdmin") Integer idAdmin);

    // Buscar préstamo por id de artículo
    @Query("SELECT p FROM Prestamos p WHERE p.articulo.idArticulo = :idArticulo")
    Prestamos findByArticuloId(@Param("idArticulo") Integer idArticulo);
}
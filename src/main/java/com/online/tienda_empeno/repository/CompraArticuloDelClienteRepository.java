package com.online.tienda_empeno.repository;

import com.online.tienda_empeno.entity.CompraArticuloDelCliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompraArticuloDelClienteRepository extends JpaRepository<CompraArticuloDelCliente, Integer> {

    // Buscar compra por artículo
    @Query("SELECT c FROM CompraArticuloDelCliente c WHERE c.articulo.idArticulo = :idArticulo")
    CompraArticuloDelCliente findByArticuloId(@Param("idArticulo") Integer idArticulo);

    // Verificar si existe una compra para un artículo
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM CompraArticuloDelCliente c WHERE c.articulo.idArticulo = :idArticulo")
    boolean existsByArticuloId(@Param("idArticulo") Integer idArticulo);

    // Listar compras de un cliente
    @Query("SELECT c FROM CompraArticuloDelCliente c WHERE c.articulo.cliente.idCliente = :idCliente ORDER BY c.fechaCreacion DESC")
    List<CompraArticuloDelCliente> findByClienteId(@Param("idCliente") Integer idCliente);
}
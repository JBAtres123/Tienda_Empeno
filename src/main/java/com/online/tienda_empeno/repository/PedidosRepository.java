package com.online.tienda_empeno.repository;

import com.online.tienda_empeno.entity.Pedidos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PedidosRepository extends JpaRepository<Pedidos, Integer> {

    // Listar pedidos de un cliente
    @Query("SELECT p FROM Pedidos p WHERE p.cliente.idCliente = :idCliente ORDER BY p.fechaPedido DESC")
    List<Pedidos> findByClienteId(@Param("idCliente") Integer idCliente);

    // Listar pedidos por estado
    @Query("SELECT p FROM Pedidos p WHERE p.idEstado = :idEstado ORDER BY p.fechaPedido DESC")
    List<Pedidos> findByEstado(@Param("idEstado") Integer idEstado);

    // Buscar pedidos pendientes de un cliente
    @Query("SELECT p FROM Pedidos p WHERE p.cliente.idCliente = :idCliente AND p.idEstado = 19")
    List<Pedidos> findPendientesByCliente(@Param("idCliente") Integer idCliente);
}
package com.online.tienda_empeno.repository;

import com.online.tienda_empeno.entity.PagoPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PagoPedidoRepository extends JpaRepository<PagoPedido, Integer> {

    // Buscar pago por pedido
    @Query("SELECT p FROM PagoPedido p WHERE p.pedido.idPedido = :idPedido")
    Optional<PagoPedido> findByPedidoId(@Param("idPedido") Integer idPedido);

    // Listar pagos de un cliente
    @Query("SELECT p FROM PagoPedido p WHERE p.cliente.idCliente = :idCliente ORDER BY p.fechaPago DESC")
    List<PagoPedido> findByClienteId(@Param("idCliente") Integer idCliente);
}
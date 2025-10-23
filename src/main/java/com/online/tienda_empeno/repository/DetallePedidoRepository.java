package com.online.tienda_empeno.repository;

import com.online.tienda_empeno.entity.DetallePedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetallePedidoRepository extends JpaRepository<DetallePedido, Integer> {

    // Listar detalles de un pedido
    @Query("SELECT d FROM DetallePedido d WHERE d.pedido.idPedido = :idPedido")
    List<DetallePedido> findByPedidoId(@Param("idPedido") Integer idPedido);

    // Verificar si un producto ya está en un pedido
    @Query("SELECT CASE WHEN COUNT(d) > 0 THEN true ELSE false END FROM DetallePedido d WHERE d.pedido.idPedido = :idPedido AND d.productoTienda.idProductoTienda = :idProducto")
    boolean existsByPedidoAndProducto(@Param("idPedido") Integer idPedido, @Param("idProducto") Integer idProducto);
}
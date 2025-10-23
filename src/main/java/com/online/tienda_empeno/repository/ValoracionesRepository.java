package com.online.tienda_empeno.repository;

import com.online.tienda_empeno.entity.Valoraciones;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ValoracionesRepository extends JpaRepository<Valoraciones, Integer> {

    // Listar valoraciones de un producto
    @Query("SELECT v FROM Valoraciones v WHERE v.productoTienda.idProductoTienda = :idProducto ORDER BY v.fechaValoracion DESC")
    List<Valoraciones> findByProductoId(@Param("idProducto") Integer idProducto);

    // Verificar si un cliente ya valoró un producto
    @Query("SELECT v FROM Valoraciones v WHERE v.cliente.idCliente = :idCliente AND v.productoTienda.idProductoTienda = :idProducto")
    Optional<Valoraciones> findByClienteAndProducto(@Param("idCliente") Integer idCliente, @Param("idProducto") Integer idProducto);

    // Verificar si existe valoración
    @Query("SELECT CASE WHEN COUNT(v) > 0 THEN true ELSE false END FROM Valoraciones v WHERE v.cliente.idCliente = :idCliente AND v.productoTienda.idProductoTienda = :idProducto")
    boolean existsByClienteAndProducto(@Param("idCliente") Integer idCliente, @Param("idProducto") Integer idProducto);

    // Calcular promedio de calificaciones de un producto
    @Query("SELECT AVG(v.calificacion) FROM Valoraciones v WHERE v.productoTienda.idProductoTienda = :idProducto")
    Double calcularPromedioCalificacion(@Param("idProducto") Integer idProducto);

    // Contar valoraciones de un producto
    @Query("SELECT COUNT(v) FROM Valoraciones v WHERE v.productoTienda.idProductoTienda = :idProducto")
    Long contarValoraciones(@Param("idProducto") Integer idProducto);
}
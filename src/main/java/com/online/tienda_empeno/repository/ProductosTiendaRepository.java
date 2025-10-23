package com.online.tienda_empeno.repository;

import com.online.tienda_empeno.entity.ProductosTienda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductosTiendaRepository extends JpaRepository<ProductosTienda, Integer> {

    // Buscar por artículo
    @Query("SELECT p FROM ProductosTienda p WHERE p.articulo.idArticulo = :idArticulo")
    Optional<ProductosTienda> findByArticuloId(@Param("idArticulo") Integer idArticulo);

    // Verificar si existe producto por artículo
    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM ProductosTienda p WHERE p.articulo.idArticulo = :idArticulo")
    boolean existsByArticuloId(@Param("idArticulo") Integer idArticulo);

    // Listar todos los productos disponibles en catálogo (artículos con estado 17)
    @Query("SELECT p FROM ProductosTienda p WHERE p.articulo.idEstado = 17 ORDER BY p.fechaPublicacion DESC")
    List<ProductosTienda> findProductosDisponibles();

    // Buscar producto por ID con validación de disponibilidad
    @Query("SELECT p FROM ProductosTienda p WHERE p.idProductoTienda = :idProducto AND p.articulo.idEstado = 17")
    Optional<ProductosTienda> findByIdAndDisponible(@Param("idProducto") Integer idProducto);
}
package com.online.tienda_empeno.repository;

import com.online.tienda_empeno.entity.Promociones;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PromocionesRepository extends JpaRepository<Promociones, Integer> {

    // Buscar promociones vigentes
    @Query("SELECT p FROM Promociones p WHERE p.activo = true AND p.fechaInicio <= :ahora AND p.fechaFin >= :ahora")
    List<Promociones> findPromocionesVigentes(@Param("ahora") LocalDateTime ahora);

    // Buscar promoción para un producto específico
    @Query("SELECT p FROM Promociones p WHERE p.productoTienda.idProductoTienda = :idProducto AND p.activo = true AND p.fechaInicio <= :ahora AND p.fechaFin >= :ahora")
    Optional<Promociones> findPromocionVigenteParaProducto(@Param("idProducto") Integer idProducto, @Param("ahora") LocalDateTime ahora);

    // Buscar promoción para una categoría
    @Query("SELECT p FROM Promociones p WHERE p.tipoArticulo.idTipoArticulo = :idTipo AND p.activo = true AND p.fechaInicio <= :ahora AND p.fechaFin >= :ahora")
    List<Promociones> findPromocionesVigentesParaCategoria(@Param("idTipo") Integer idTipo, @Param("ahora") LocalDateTime ahora);

    // Buscar promociones generales
    @Query("SELECT p FROM Promociones p WHERE p.tipoPromocion = 'GENERAL' AND p.activo = true AND p.fechaInicio <= :ahora AND p.fechaFin >= :ahora")
    List<Promociones> findPromocionesGeneralesVigentes(@Param("ahora") LocalDateTime ahora);
}
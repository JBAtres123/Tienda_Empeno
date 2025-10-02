package com.online.tienda_empeno.repository;

import com.online.tienda_empeno.entity.ImagenesArticulos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ImagenesArticulosRepository extends JpaRepository<ImagenesArticulos, Integer> {

    // Buscar todas las imágenes de un artículo específico
    @Query("SELECT i FROM ImagenesArticulos i WHERE i.articulo.idArticulo = :idArticulo")
    List<ImagenesArticulos> findByArticuloId(@Param("idArticulo") Integer idArticulo);
}

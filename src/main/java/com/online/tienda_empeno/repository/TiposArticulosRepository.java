package com.online.tienda_empeno.repository;

import com.online.tienda_empeno.entity.TiposArticulos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TiposArticulosRepository extends JpaRepository<TiposArticulos, Integer> {

    // Obtener solo los tipos de artículos ACTIVOS (id_estado_articulo = 1)
    @Query("SELECT t FROM TiposArticulos t WHERE t.estadoArticulo.idEstadoArticulo = 1")
    List<TiposArticulos> findAllActivos();
}

package com.online.tienda_empeno.repository;

import com.online.tienda_empeno.entity.Articulos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ArticulosRepository extends JpaRepository<Articulos, Integer> {

    // Buscar todos los artículos de un cliente específico
    @Query("SELECT a FROM Articulos a WHERE a.cliente.idCliente = :idCliente")
    List<Articulos> findByClienteId(@Param("idCliente") Integer idCliente);

    // Buscar artículos por tipo de artículo
    @Query("SELECT a FROM Articulos a WHERE a.tipoArticulo.idTipoArticulo = :idTipoArticulo")
    List<Articulos> findByTipoArticulo(@Param("idTipoArticulo") Integer idTipoArticulo);
}

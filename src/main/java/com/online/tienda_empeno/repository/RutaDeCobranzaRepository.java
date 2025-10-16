package com.online.tienda_empeno.repository;

import com.online.tienda_empeno.entity.RutaDeCobranza;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RutaDeCobranzaRepository extends JpaRepository<RutaDeCobranza, Integer> {

    // Buscar ruta por id de cobranza
    @Query("SELECT r FROM RutaDeCobranza r WHERE r.cobranza.idCobranza = :idCobranza")
    RutaDeCobranza findByCobranzaId(@Param("idCobranza") Integer idCobranza);
}
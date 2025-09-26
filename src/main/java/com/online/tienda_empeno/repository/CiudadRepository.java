package com.online.tienda_empeno.repository;


import com.online.tienda_empeno.entity.Ciudad;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CiudadRepository extends JpaRepository<Ciudad, Integer> {
    List<Ciudad> findByDepartamentoIdDepartamento(Integer idDepartamento);
}



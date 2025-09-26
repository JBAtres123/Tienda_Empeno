package com.online.tienda_empeno.repository;


import com.online.tienda_empeno.entity.Departamento;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DepartamentoRepository extends JpaRepository<Departamento, Integer> {
    List<Departamento> findByPaisIdPais(Integer idPais);
}


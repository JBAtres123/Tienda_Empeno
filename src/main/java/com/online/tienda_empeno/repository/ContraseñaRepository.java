package com.online.tienda_empeno.repository;

import com.online.tienda_empeno.entity.Contraseñas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContraseñaRepository extends JpaRepository<Contraseñas, Integer> { }

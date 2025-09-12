package com.online.tienda_empeno.repository;

import com.online.tienda_empeno.entity.Administradores;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdministradorRepository extends JpaRepository<Administradores, Integer> {
    Administradores findByEmailAdminAndIdContraseña(String emailAdmin, Integer idContraseña);
}


package com.online.tienda_empeno.repository;

import com.online.tienda_empeno.entity.Administradores;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AdministradorRepository extends JpaRepository<Administradores, Integer> {

    // Buscar administrador por email y contraseña en texto usando la relación con Contraseña
    @Query("SELECT a FROM Administradores a JOIN Contraseña c ON a.idContraseña = c.idContraseña " +
            "WHERE a.emailAdmin = :email AND c.contraseña = :password")
    Administradores findByEmailAndPassword(@Param("email") String email, @Param("password") String password);
}

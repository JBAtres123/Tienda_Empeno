package com.online.tienda_empeno.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "Contraseñas")
public class Contraseñas {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idContraseña;

    private String contraseña;

    public Integer getIdContraseña() { return idContraseña; }
    public void setIdContraseña(Integer idContraseña) { this.idContraseña = idContraseña; }

    public String getContraseña() { return contraseña; }
    public void setContraseña(String contraseña) { this.contraseña = contraseña; }
}

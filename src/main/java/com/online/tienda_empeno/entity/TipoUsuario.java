package com.online.tienda_empeno.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "Tipo_Usuario")
public class TipoUsuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tipo_usuario")
    private Integer idTipoUsuario;

    @Column(name = "tipo_usuario", nullable = false, length = 15)
    private String tipoUsuario;

    // Getters y Setters
    public Integer getIdTipoUsuario() { return idTipoUsuario; }
    public void setIdTipoUsuario(Integer idTipoUsuario) { this.idTipoUsuario = idTipoUsuario; }

    public String getTipoUsuario() { return tipoUsuario; }
    public void setTipoUsuario(String tipoUsuario) { this.tipoUsuario = tipoUsuario; }
}



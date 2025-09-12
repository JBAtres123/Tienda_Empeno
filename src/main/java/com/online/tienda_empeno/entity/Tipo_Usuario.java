package com.online.tienda_empeno.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "Tipo_Usuario")
public class Tipo_Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tipo_usuario")
    private Integer idTipoUsuario;

    @Column(name = "tipo_usuario")
    private String tipoUsuario;

    // getters y setters
}


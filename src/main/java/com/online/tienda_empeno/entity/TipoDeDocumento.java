package com.online.tienda_empeno.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "Tipo_De_Documento")
public class TipoDeDocumento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tipo_documento")
    private Integer idTipoDocumento;

    @Column(name = "nombre", nullable = false, length = 20)
    private String nombre;

    // Getters y Setters
    public Integer getIdTipoDocumento() { return idTipoDocumento; }
    public void setIdTipoDocumento(Integer idTipoDocumento) { this.idTipoDocumento = idTipoDocumento; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
}




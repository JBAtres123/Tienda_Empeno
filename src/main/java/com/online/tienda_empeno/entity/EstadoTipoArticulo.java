package com.online.tienda_empeno.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "Estado_Tipo_Articulo")
public class EstadoTipoArticulo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_estado_articulo")
    private Integer idEstadoArticulo;

    @Column(name = "tipo_estado_articulo", nullable = false, length = 20)
    private String tipoEstadoArticulo;

    // Getters y Setters
    public Integer getIdEstadoArticulo() { return idEstadoArticulo; }
    public void setIdEstadoArticulo(Integer idEstadoArticulo) { this.idEstadoArticulo = idEstadoArticulo; }

    public String getTipoEstadoArticulo() { return tipoEstadoArticulo; }
    public void setTipoEstadoArticulo(String tipoEstadoArticulo) { this.tipoEstadoArticulo = tipoEstadoArticulo; }
}

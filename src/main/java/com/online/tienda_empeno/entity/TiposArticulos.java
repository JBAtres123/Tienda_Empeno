package com.online.tienda_empeno.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "Tipos_Articulos")
public class TiposArticulos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tipo_articulo")
    private Integer idTipoArticulo;

    @ManyToOne
    @JoinColumn(name = "id_estado_articulo", nullable = false)
    private EstadoTipoArticulo estadoArticulo;

    @ManyToOne
    @JoinColumn(name = "id_parametro", nullable = false)
    private ParametroAvaluo parametroAvaluo;

    @Column(name = "nombre_tipo_articulo", nullable = false, length = 25)
    private String nombreTipoArticulo;

    // Getters y Setters
    public Integer getIdTipoArticulo() { return idTipoArticulo; }
    public void setIdTipoArticulo(Integer idTipoArticulo) { this.idTipoArticulo = idTipoArticulo; }

    public EstadoTipoArticulo getEstadoArticulo() { return estadoArticulo; }
    public void setEstadoArticulo(EstadoTipoArticulo estadoArticulo) { this.estadoArticulo = estadoArticulo; }

    public ParametroAvaluo getParametroAvaluo() { return parametroAvaluo; }
    public void setParametroAvaluo(ParametroAvaluo parametroAvaluo) { this.parametroAvaluo = parametroAvaluo; }

    public String getNombreTipoArticulo() { return nombreTipoArticulo; }
    public void setNombreTipoArticulo(String nombreTipoArticulo) { this.nombreTipoArticulo = nombreTipoArticulo; }
}


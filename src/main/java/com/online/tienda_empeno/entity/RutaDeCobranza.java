package com.online.tienda_empeno.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "Ruta_De_Cobranza")
public class RutaDeCobranza {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ruta")
    private Integer idRuta;

    @ManyToOne
    @JoinColumn(name = "id_cobranza", nullable = false)
    private Cobranza cobranza;

    @ManyToOne
    @JoinColumn(name = "id_departamento") // ← NUEVO
    private Departamento departamento;

    @Column(name = "asignacion_ruta", length = 100)
    private String asignacionRuta;

    // Getters y Setters
    public Integer getIdRuta() { return idRuta; }
    public void setIdRuta(Integer idRuta) { this.idRuta = idRuta; }

    public Cobranza getCobranza() { return cobranza; }
    public void setCobranza(Cobranza cobranza) { this.cobranza = cobranza; }

    public Departamento getDepartamento() { return departamento; } // ← NUEVO
    public void setDepartamento(Departamento departamento) { this.departamento = departamento; } // ← NUEVO

    public String getAsignacionRuta() { return asignacionRuta; }
    public void setAsignacionRuta(String asignacionRuta) { this.asignacionRuta = asignacionRuta; }
}
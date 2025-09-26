package com.online.tienda_empeno.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "Pais")
public class Pais {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pais")
    private Integer idPais;

    @Column(name = "nombre_pais", nullable = false, length = 40)
    private String nombrePais;

    @OneToMany(mappedBy = "pais", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Departamento> departamentos;

    // Getters y Setters
    public Integer getIdPais() { return idPais; }
    public void setIdPais(Integer idPais) { this.idPais = idPais; }

    public String getNombrePais() { return nombrePais; }
    public void setNombrePais(String nombrePais) { this.nombrePais = nombrePais; }

    public List<Departamento> getDepartamentos() { return departamentos; }
    public void setDepartamentos(List<Departamento> departamentos) { this.departamentos = departamentos; }
}

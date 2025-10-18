package com.online.tienda_empeno.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "Metodo_Pago")
public class MetodoPago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_metodo_pago")
    private Integer idMetodoPago;

    @Column(name = "nombre_metodo_pago", nullable = false, length = 50)
    private String nombreMetodoPago;

    // Getters y Setters
    public Integer getIdMetodoPago() { return idMetodoPago; }
    public void setIdMetodoPago(Integer idMetodoPago) { this.idMetodoPago = idMetodoPago; }

    public String getNombreMetodoPago() { return nombreMetodoPago; }
    public void setNombreMetodoPago(String nombreMetodoPago) { this.nombreMetodoPago = nombreMetodoPago; }
}


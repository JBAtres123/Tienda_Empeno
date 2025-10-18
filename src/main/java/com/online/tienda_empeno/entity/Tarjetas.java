package com.online.tienda_empeno.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "tarjetas")
public class Tarjetas {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tarjeta")
    private Integer idTarjeta;

    @ManyToOne
    @JoinColumn(name = "id_cliente", nullable = false)
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "Id_metodo_pago", nullable = false)
    private MetodoPago metodoPago;

    @Column(name = "numero_tarjeta", nullable = false, length = 20) // ← CORREGIR AQUÍ
    private String numeroTarjeta;

    @Column(name = "fecha_expiracion", nullable = false, length = 10)
    private String fechaExpiracion;

    @Column(name = "cvv", nullable = false, length = 4)
    private String cvv;

    // Getters y Setters
    public Integer getIdTarjeta() { return idTarjeta; }
    public void setIdTarjeta(Integer idTarjeta) { this.idTarjeta = idTarjeta; }

    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }

    public MetodoPago getMetodoPago() { return metodoPago; }
    public void setMetodoPago(MetodoPago metodoPago) { this.metodoPago = metodoPago; }

    public String getNumeroTarjeta() { return numeroTarjeta; }
    public void setNumeroTarjeta(String numeroTarjeta) { this.numeroTarjeta = numeroTarjeta; }

    public String getFechaExpiracion() { return fechaExpiracion; }
    public void setFechaExpiracion(String fechaExpiracion) { this.fechaExpiracion = fechaExpiracion; }

    public String getCvv() { return cvv; }
    public void setCvv(String cvv) { this.cvv = cvv; }
}
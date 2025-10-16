package com.online.tienda_empeno.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "Pagos_Prestamo")
public class PagosPrestamo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pago_prestamo")
    private Integer idPagoPrestamo;

    @ManyToOne
    @JoinColumn(name = "id_prestamo", nullable = false)
    private Prestamos prestamo;

    @ManyToOne
    @JoinColumn(name = "id_metodo_pago", nullable = false)
    private MetodoPago metodoPago;

    @ManyToOne
    @JoinColumn(name = "id_admin", nullable = true)
    private Administradores administrador;

    @Column(name = "monto", nullable = false, precision = 12, scale = 2)
    private BigDecimal monto;

    @Column(name = "mes_pago", nullable = false)
    private LocalDate mesPago;

    // Getters y Setters
    public Integer getIdPagoPrestamo() {
        return idPagoPrestamo;
    }

    public void setIdPagoPrestamo(Integer idPagoPrestamo) {
        this.idPagoPrestamo = idPagoPrestamo;
    }

    public Prestamos getPrestamo() {
        return prestamo;
    }

    public void setPrestamo(Prestamos prestamo) {
        this.prestamo = prestamo;
    }

    public MetodoPago getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(MetodoPago metodoPago) {
        this.metodoPago = metodoPago;
    }

    public Administradores getAdministrador() {
        return administrador;
    }

    public void setAdministrador(Administradores administrador) {
        this.administrador = administrador;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public LocalDate getMesPago() {
        return mesPago;
    }

    public void setMesPago(LocalDate mesPago) {
        this.mesPago = mesPago;
    }
}
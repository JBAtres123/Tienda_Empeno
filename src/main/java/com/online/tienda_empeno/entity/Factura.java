package com.online.tienda_empeno.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "Factura")
public class Factura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_factura")
    private Integer idFactura;

    @ManyToOne
    @JoinColumn(name = "id_pago_prestamo", nullable = false)
    private PagosPrestamo pagoPrestamo;

    @Column(name = "fecha_pago", nullable = false)
    private LocalDate fechaPago;

    // Getters y Setters
    public Integer getIdFactura() { return idFactura; }
    public void setIdFactura(Integer idFactura) { this.idFactura = idFactura; }

    public PagosPrestamo getPagoPrestamo() { return pagoPrestamo; }
    public void setPagoPrestamo(PagosPrestamo pagoPrestamo) { this.pagoPrestamo = pagoPrestamo; }

    public LocalDate getFechaPago() { return fechaPago; }
    public void setFechaPago(LocalDate fechaPago) { this.fechaPago = fechaPago; }
}
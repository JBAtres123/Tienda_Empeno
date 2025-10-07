package com.online.tienda_empeno.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "Prestamos")
public class Prestamos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_prestamo")
    private Integer idPrestamo;

    @ManyToOne
    @JoinColumn(name = "id_articulo", nullable = false)
    private Articulos articulo;

    @ManyToOne
    @JoinColumn(name = "id_cliente", nullable = false)
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "id_admin", nullable = false)
    private Administradores administrador;

    @Column(name = "id_estado", nullable = false)
    private Integer idEstado;

    @Column(name = "tasa_interes", precision = 5, scale = 2)
    private BigDecimal tasaInteres;

    @Column(name = "monto_prestamo", precision = 12, scale = 2)
    private BigDecimal montoPrestamo;

    @Column(name = "porcentaje_avaluo", precision = 5, scale = 2)
    private BigDecimal porcentajeAvaluo;

    @Column(name = "plazo_meses")
    private Integer plazoMeses;

    @Column(name = "fecha_inicio")
    private LocalDate fechaInicio;

    @Column(name = "fecha_vencimiento")
    private LocalDate fechaVencimiento;

    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;

    // Getters y Setters
    public Integer getIdPrestamo() { return idPrestamo; }
    public void setIdPrestamo(Integer idPrestamo) { this.idPrestamo = idPrestamo; }

    public Articulos getArticulo() { return articulo; }
    public void setArticulo(Articulos articulo) { this.articulo = articulo; }

    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }

    public Administradores getAdministrador() { return administrador; }
    public void setAdministrador(Administradores administrador) { this.administrador = administrador; }

    public Integer getIdEstado() { return idEstado; }
    public void setIdEstado(Integer idEstado) { this.idEstado = idEstado; }

    public BigDecimal getTasaInteres() { return tasaInteres; }
    public void setTasaInteres(BigDecimal tasaInteres) { this.tasaInteres = tasaInteres; }

    public BigDecimal getMontoPrestamo() { return montoPrestamo; }
    public void setMontoPrestamo(BigDecimal montoPrestamo) { this.montoPrestamo = montoPrestamo; }

    public BigDecimal getPorcentajeAvaluo() { return porcentajeAvaluo; }
    public void setPorcentajeAvaluo(BigDecimal porcentajeAvaluo) { this.porcentajeAvaluo = porcentajeAvaluo; }

    public Integer getPlazoMeses() { return plazoMeses; }
    public void setPlazoMeses(Integer plazoMeses) { this.plazoMeses = plazoMeses; }

    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }

    public LocalDate getFechaVencimiento() { return fechaVencimiento; }
    public void setFechaVencimiento(LocalDate fechaVencimiento) { this.fechaVencimiento = fechaVencimiento; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
        if (idEstado == null) {
            idEstado = 2; // En Evaluación por defecto
        }
    }
}
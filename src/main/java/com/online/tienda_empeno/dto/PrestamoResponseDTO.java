package com.online.tienda_empeno.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class PrestamoResponseDTO {
    private Integer idPrestamo;
    private Integer idArticulo;
    private String nombreArticulo;
    private Integer idCliente;
    private String nombreCliente;
    private Integer idAdmin;
    private String nombreAdmin;
    private Integer idEstado;
    private String nombreEstado;
    private BigDecimal tasaInteres;
    private BigDecimal montoPrestamo;
    private BigDecimal porcentajeAvaluo;
    private Integer plazoMeses;
    private LocalDate fechaInicio;
    private LocalDate fechaVencimiento;
    private BigDecimal precioArticulo;
    private BigDecimal precioAvaluo;
    private BigDecimal saldoAdeudado;


    public BigDecimal getSaldoAdeudado() {
        return saldoAdeudado;
    }

    public void setSaldoAdeudado(BigDecimal saldoAdeudado) {
        this.saldoAdeudado = saldoAdeudado;
    }

    // Getters y Setters
    public Integer getIdPrestamo() { return idPrestamo; }
    public void setIdPrestamo(Integer idPrestamo) { this.idPrestamo = idPrestamo; }

    public Integer getIdArticulo() { return idArticulo; }
    public void setIdArticulo(Integer idArticulo) { this.idArticulo = idArticulo; }

    public String getNombreArticulo() { return nombreArticulo; }
    public void setNombreArticulo(String nombreArticulo) { this.nombreArticulo = nombreArticulo; }

    public Integer getIdCliente() { return idCliente; }
    public void setIdCliente(Integer idCliente) { this.idCliente = idCliente; }

    public String getNombreCliente() { return nombreCliente; }
    public void setNombreCliente(String nombreCliente) { this.nombreCliente = nombreCliente; }

    public Integer getIdAdmin() { return idAdmin; }
    public void setIdAdmin(Integer idAdmin) { this.idAdmin = idAdmin; }

    public String getNombreAdmin() { return nombreAdmin; }
    public void setNombreAdmin(String nombreAdmin) { this.nombreAdmin = nombreAdmin; }

    public Integer getIdEstado() { return idEstado; }
    public void setIdEstado(Integer idEstado) { this.idEstado = idEstado; }

    public String getNombreEstado() { return nombreEstado; }
    public void setNombreEstado(String nombreEstado) { this.nombreEstado = nombreEstado; }

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

    public BigDecimal getPrecioArticulo() { return precioArticulo; }
    public void setPrecioArticulo(BigDecimal precioArticulo) { this.precioArticulo = precioArticulo; }

    public BigDecimal getPrecioAvaluo() { return precioAvaluo; }
    public void setPrecioAvaluo(BigDecimal precioAvaluo) { this.precioAvaluo = precioAvaluo; }
}
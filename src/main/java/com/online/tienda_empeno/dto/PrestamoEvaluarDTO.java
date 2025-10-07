package com.online.tienda_empeno.dto;

import java.math.BigDecimal;

public class PrestamoEvaluarDTO {
    private Integer idEstado; // 3 = Aprobado, 4 = Rechazado
    private BigDecimal tasaInteres;
    private BigDecimal montoPrestamo;
    private BigDecimal porcentajeAvaluo;
    private Integer plazoMeses;

    // Getters y Setters
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
}
package com.online.tienda_empeno.dto;

import java.math.BigDecimal;

public class PagoEfectivoDTO {
    private Integer idPrestamo;
    private BigDecimal monto;

    // Getters y Setters
    public Integer getIdPrestamo() { return idPrestamo; }
    public void setIdPrestamo(Integer idPrestamo) { this.idPrestamo = idPrestamo; }

    public BigDecimal getMonto() { return monto; }
    public void setMonto(BigDecimal monto) { this.monto = monto; }
}
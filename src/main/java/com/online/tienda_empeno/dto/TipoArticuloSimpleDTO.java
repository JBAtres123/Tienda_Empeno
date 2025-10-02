package com.online.tienda_empeno.dto;

import java.math.BigDecimal;

public class TipoArticuloSimpleDTO {
    private Integer idTipoArticulo;
    private String nombreTipoArticulo;
    private BigDecimal porcentajeMin;
    private BigDecimal porcentajeMax;

    // Getters y Setters
    public Integer getIdTipoArticulo() { return idTipoArticulo; }
    public void setIdTipoArticulo(Integer idTipoArticulo) { this.idTipoArticulo = idTipoArticulo; }

    public String getNombreTipoArticulo() { return nombreTipoArticulo; }
    public void setNombreTipoArticulo(String nombreTipoArticulo) { this.nombreTipoArticulo = nombreTipoArticulo; }

    public BigDecimal getPorcentajeMin() { return porcentajeMin; }
    public void setPorcentajeMin(BigDecimal porcentajeMin) { this.porcentajeMin = porcentajeMin; }

    public BigDecimal getPorcentajeMax() { return porcentajeMax; }
    public void setPorcentajeMax(BigDecimal porcentajeMax) { this.porcentajeMax = porcentajeMax; }
}
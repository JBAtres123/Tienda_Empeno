package com.online.tienda_empeno.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "Parametro_Avaluo")
public class ParametroAvaluo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_parametro")
    private Integer idParametro;

    @Column(name = "porcentaje_min", nullable = false, precision = 5, scale = 2)
    private BigDecimal porcentajeMin;

    @Column(name = "porcentaje_max", nullable = false, precision = 5, scale = 2)
    private BigDecimal porcentajeMax;

    // Getters y Setters
    public Integer getIdParametro() { return idParametro; }
    public void setIdParametro(Integer idParametro) { this.idParametro = idParametro; }

    public BigDecimal getPorcentajeMin() { return porcentajeMin; }
    public void setPorcentajeMin(BigDecimal porcentajeMin) { this.porcentajeMin = porcentajeMin; }

    public BigDecimal getPorcentajeMax() { return porcentajeMax; }
    public void setPorcentajeMax(BigDecimal porcentajeMax) { this.porcentajeMax = porcentajeMax; }
}

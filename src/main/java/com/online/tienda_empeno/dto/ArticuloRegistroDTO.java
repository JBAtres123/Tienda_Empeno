package com.online.tienda_empeno.dto;

import java.math.BigDecimal;

public class ArticuloRegistroDTO {
    private Integer idCliente;
    private Integer idTipoArticulo;
    private String estadoArticulo; // Del 1 al 10 (estado físico)
    private String nombreArticulo;
    private String descripcion;
    private BigDecimal precioArticulo;
    private String urlImagen; // URL de la imagen del artículo

    // Getters y Setters
    public Integer getIdCliente() { return idCliente; }
    public void setIdCliente(Integer idCliente) { this.idCliente = idCliente; }

    public Integer getIdTipoArticulo() { return idTipoArticulo; }
    public void setIdTipoArticulo(Integer idTipoArticulo) { this.idTipoArticulo = idTipoArticulo; }

    public String getEstadoArticulo() { return estadoArticulo; }
    public void setEstadoArticulo(String estadoArticulo) { this.estadoArticulo = estadoArticulo; }

    public String getNombreArticulo() { return nombreArticulo; }
    public void setNombreArticulo(String nombreArticulo) { this.nombreArticulo = nombreArticulo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public BigDecimal getPrecioArticulo() { return precioArticulo; }
    public void setPrecioArticulo(BigDecimal precioArticulo) { this.precioArticulo = precioArticulo; }

    public String getUrlImagen() { return urlImagen; }
    public void setUrlImagen(String urlImagen) { this.urlImagen = urlImagen; }
}

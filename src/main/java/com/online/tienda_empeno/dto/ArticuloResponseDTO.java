package com.online.tienda_empeno.dto;

import java.math.BigDecimal;

public class ArticuloResponseDTO {
    private Integer idArticulo;
    private Integer idCliente;
    private String nombreCliente;
    private Integer idEstado;
    private String estadoArticulo; // Del 1 al 10
    private String nombreArticulo;
    private String descripcion;
    private BigDecimal precioArticulo;
    private TipoArticuloSimpleDTO tipoArticulo;
    private String urlImagen;

    // Getters y Setters
    public Integer getIdArticulo() { return idArticulo; }
    public void setIdArticulo(Integer idArticulo) { this.idArticulo = idArticulo; }

    public Integer getIdCliente() { return idCliente; }
    public void setIdCliente(Integer idCliente) { this.idCliente = idCliente; }

    public String getNombreCliente() { return nombreCliente; }
    public void setNombreCliente(String nombreCliente) { this.nombreCliente = nombreCliente; }

    public Integer getIdEstado() { return idEstado; }
    public void setIdEstado(Integer idEstado) { this.idEstado = idEstado; }

    public String getEstadoArticulo() { return estadoArticulo; }
    public void setEstadoArticulo(String estadoArticulo) { this.estadoArticulo = estadoArticulo; }

    public String getNombreArticulo() { return nombreArticulo; }
    public void setNombreArticulo(String nombreArticulo) { this.nombreArticulo = nombreArticulo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public BigDecimal getPrecioArticulo() { return precioArticulo; }
    public void setPrecioArticulo(BigDecimal precioArticulo) { this.precioArticulo = precioArticulo; }

    public TipoArticuloSimpleDTO getTipoArticulo() { return tipoArticulo; }
    public void setTipoArticulo(TipoArticuloSimpleDTO tipoArticulo) { this.tipoArticulo = tipoArticulo; }

    public String getUrlImagen() { return urlImagen; }
    public void setUrlImagen(String urlImagen) { this.urlImagen = urlImagen; }
}

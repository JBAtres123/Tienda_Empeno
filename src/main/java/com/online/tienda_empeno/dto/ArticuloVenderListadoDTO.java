package com.online.tienda_empeno.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class ArticuloVenderListadoDTO {
    private Integer idArticulo;
    private String nombreArticulo;
    private String descripcion;
    private BigDecimal precioArticulo;
    private String estadoArticulo;
    private String nombreCliente;
    private String emailCliente;
    private Integer idCliente;
    private String tipoArticulo;
    private String urlImagen;  // Primera imagen (compatibilidad)
    private List<String> imagenes;  // Todas las imágenes para el carrusel
    private LocalDateTime fechaCreacion;

    // Getters y Setters
    public Integer getIdArticulo() {
        return idArticulo;
    }

    public void setIdArticulo(Integer idArticulo) {
        this.idArticulo = idArticulo;
    }

    public String getNombreArticulo() {
        return nombreArticulo;
    }

    public void setNombreArticulo(String nombreArticulo) {
        this.nombreArticulo = nombreArticulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BigDecimal getPrecioArticulo() {
        return precioArticulo;
    }

    public void setPrecioArticulo(BigDecimal precioArticulo) {
        this.precioArticulo = precioArticulo;
    }

    public String getEstadoArticulo() {
        return estadoArticulo;
    }

    public void setEstadoArticulo(String estadoArticulo) {
        this.estadoArticulo = estadoArticulo;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public String getEmailCliente() {
        return emailCliente;
    }

    public void setEmailCliente(String emailCliente) {
        this.emailCliente = emailCliente;
    }

    public Integer getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Integer idCliente) {
        this.idCliente = idCliente;
    }

    public String getTipoArticulo() {
        return tipoArticulo;
    }

    public void setTipoArticulo(String tipoArticulo) {
        this.tipoArticulo = tipoArticulo;
    }

    public String getUrlImagen() {
        return urlImagen;
    }

    public void setUrlImagen(String urlImagen) {
        this.urlImagen = urlImagen;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public List<String> getImagenes() {
        return imagenes;
    }

    public void setImagenes(List<String> imagenes) {
        this.imagenes = imagenes;
    }
}


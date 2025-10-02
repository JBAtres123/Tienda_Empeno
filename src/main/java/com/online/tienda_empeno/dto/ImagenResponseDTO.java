package com.online.tienda_empeno.dto;

public class ImagenResponseDTO {
    private Integer idImagen;
    private Integer idArticulo;
    private String urlImagen;

    // Getters y Setters
    public Integer getIdImagen() { return idImagen; }
    public void setIdImagen(Integer idImagen) { this.idImagen = idImagen; }

    public Integer getIdArticulo() { return idArticulo; }
    public void setIdArticulo(Integer idArticulo) { this.idArticulo = idArticulo; }

    public String getUrlImagen() { return urlImagen; }
    public void setUrlImagen(String urlImagen) { this.urlImagen = urlImagen; }
}
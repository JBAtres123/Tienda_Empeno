package com.online.tienda_empeno.dto;

public class ValoracionDTO {
    private Integer idProductoTienda;
    private Integer calificacion; // 1-5
    private String comentario;

    // Getters y Setters
    public Integer getIdProductoTienda() {
        return idProductoTienda;
    }

    public void setIdProductoTienda(Integer idProductoTienda) {
        this.idProductoTienda = idProductoTienda;
    }

    public Integer getCalificacion() {
        return calificacion;
    }

    public void setCalificacion(Integer calificacion) {
        this.calificacion = calificacion;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }
}
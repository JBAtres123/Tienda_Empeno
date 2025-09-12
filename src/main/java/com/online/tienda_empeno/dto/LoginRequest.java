package com.online.tienda_empeno.dto;

public class LoginRequest {
    private String usuario;      // correo del cliente
    private String contraseña;   // contraseña en texto

    // Getters y Setters
    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }

    public String getContraseña() { return contraseña; }
    public void setContraseña(String contraseña) { this.contraseña = contraseña; }
}




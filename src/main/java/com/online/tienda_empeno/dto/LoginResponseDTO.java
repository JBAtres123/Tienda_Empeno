package com.online.tienda_empeno.dto;

public class LoginResponseDTO {
    private boolean success;
    private String message;
    private Integer idUsuario;
    private String token; // ← NUEVO
    private String tipoUsuario; // ← NUEVO (Cliente o Administrador)

    // Constructor sin token (mantener compatibilidad)
    public LoginResponseDTO(boolean success, String message, Integer idUsuario) {
        this.success = success;
        this.message = message;
        this.idUsuario = idUsuario;
    }

    // Constructor con token
    public LoginResponseDTO(boolean success, String message, Integer idUsuario, String token, String tipoUsuario) {
        this.success = success;
        this.message = message;
        this.idUsuario = idUsuario;
        this.token = token;
        this.tipoUsuario = tipoUsuario;
    }

    // Getters y Setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Integer getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Integer idUsuario) { this.idUsuario = idUsuario; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getTipoUsuario() { return tipoUsuario; }
    public void setTipoUsuario(String tipoUsuario) { this.tipoUsuario = tipoUsuario; }
}
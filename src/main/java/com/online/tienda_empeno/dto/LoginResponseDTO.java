package com.online.tienda_empeno.dto;

public class LoginResponseDTO {
    private boolean success;
    private String message;
    private Integer idUsuario; // <-- cambio aquí

    public LoginResponseDTO(boolean success, String message, Integer idUsuario) {
        this.success = success;
        this.message = message;
        this.idUsuario = idUsuario;
    }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Integer getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Integer idUsuario) { this.idUsuario = idUsuario; }
}

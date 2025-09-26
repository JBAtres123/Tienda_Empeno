package com.online.tienda_empeno.dto;

public class ClienteResponseDTO {
    private Integer idCliente;
    private String numeroDocumento;
    private String nombreCliente;
    private String apellidoCliente;
    private String emailCliente;
    private DireccionResponseDTO direccion; // puede ser null

    public Integer getIdCliente() { return idCliente; }
    public void setIdCliente(Integer idCliente) { this.idCliente = idCliente; }

    public String getNumeroDocumento() { return numeroDocumento; }
    public void setNumeroDocumento(String numeroDocumento) { this.numeroDocumento = numeroDocumento; }

    public String getNombreCliente() { return nombreCliente; }
    public void setNombreCliente(String nombreCliente) { this.nombreCliente = nombreCliente; }

    public String getApellidoCliente() { return apellidoCliente; }
    public void setApellidoCliente(String apellidoCliente) { this.apellidoCliente = apellidoCliente; }

    public String getEmailCliente() { return emailCliente; }
    public void setEmailCliente(String emailCliente) { this.emailCliente = emailCliente; }

    public DireccionResponseDTO getDireccion() { return direccion; }
    public void setDireccion(DireccionResponseDTO direccion) { this.direccion = direccion; }
}

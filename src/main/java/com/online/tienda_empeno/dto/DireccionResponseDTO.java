package com.online.tienda_empeno.dto;

public class DireccionResponseDTO {
    private Integer idDireccion;
    private CiudadSimpleDTO ciudad;
    private String direccionCliente;
    private String codigoPostal;

    public Integer getIdDireccion() { return idDireccion; }
    public void setIdDireccion(Integer idDireccion) { this.idDireccion = idDireccion; }

    public CiudadSimpleDTO getCiudad() { return ciudad; }
    public void setCiudad(CiudadSimpleDTO ciudad) { this.ciudad = ciudad; }

    public String getDireccionCliente() { return direccionCliente; }
    public void setDireccionCliente(String direccionCliente) { this.direccionCliente = direccionCliente; }

    public String getCodigoPostal() { return codigoPostal; }
    public void setCodigoPostal(String codigoPostal) { this.codigoPostal = codigoPostal; }
}


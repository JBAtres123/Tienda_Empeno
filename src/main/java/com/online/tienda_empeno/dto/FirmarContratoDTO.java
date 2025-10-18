package com.online.tienda_empeno.dto;

public class FirmarContratoDTO {
    private String firmaCliente; // Base64 de la firma digital

    public String getFirmaCliente() { return firmaCliente; }
    public void setFirmaCliente(String firmaCliente) { this.firmaCliente = firmaCliente; }
}
package com.online.tienda_empeno.service;

import com.online.tienda_empeno.dto.*;
import com.online.tienda_empeno.entity.*;
import com.online.tienda_empeno.repository.*;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class ClienteService {

    private final DireccionesRepository direccionesRepository;
    private final CiudadRepository ciudadRepository;
    private final ClienteRepository clienteRepository;
    private final TipoDeDocumentoRepository tipoDeDocumentoRepository;
    private final ContraseñaRepository contraseñaRepository;
    private final TipoUsuarioRepository tipoUsuarioRepository;

    public ClienteService(DireccionesRepository direccionesRepository,
                          CiudadRepository ciudadRepository,
                          ClienteRepository clienteRepository,
                          TipoDeDocumentoRepository tipoDeDocumentoRepository,
                          ContraseñaRepository contraseñaRepository,
                          TipoUsuarioRepository tipoUsuarioRepository) {
        this.direccionesRepository = direccionesRepository;
        this.ciudadRepository = ciudadRepository;
        this.clienteRepository = clienteRepository;
        this.tipoDeDocumentoRepository = tipoDeDocumentoRepository;
        this.contraseñaRepository = contraseñaRepository;
        this.tipoUsuarioRepository = tipoUsuarioRepository;
    }

    // ---- Paso 1: Guardar dirección y devolver DTO limpio ----
    public DireccionResponseDTO registrarDireccion(com.online.tienda_empeno.dto.DireccionDTO dto) {
        Optional<Ciudad> ciudadOpt = ciudadRepository.findById(dto.getIdCiudad());
        if (ciudadOpt.isEmpty()) {
            throw new RuntimeException("Ciudad no encontrada con id " + dto.getIdCiudad());
        }

        Direcciones direccion = new Direcciones();
        direccion.setCiudad(ciudadOpt.get());
        direccion.setDireccionCliente(dto.getDireccionCliente());
        direccion.setCodigoPostal(dto.getCodigoPostal());

        Direcciones saved = direccionesRepository.save(direccion);
        return mapDireccionToDto(saved);
    }

    // ---- Paso 2: Registrar cliente y devolver DTO limpio ----
    public ClienteResponseDTO registrarCliente(com.online.tienda_empeno.dto.ClienteRegistroDTO dto) {
        Optional<TipoDeDocumento> tipoDocOpt = tipoDeDocumentoRepository.findById(dto.getIdTipoDocumento());
        if (tipoDocOpt.isEmpty()) {
            throw new RuntimeException("Tipo de documento no encontrado con id " + dto.getIdTipoDocumento());
        }

        TipoUsuario tipoUsuario = tipoUsuarioRepository.findById(1)
                .orElseThrow(() -> new RuntimeException("Tipo usuario Cliente no encontrado"));

        Contraseña contraseña = new Contraseña();
        contraseña.setTipoUsuario(tipoUsuario);
        contraseña.setContraseña(dto.getContraseña());
        contraseña.setFechaCreacion(new Date());
        contraseña = contraseñaRepository.save(contraseña);

        Direcciones direccion = null;
        if (dto.getIdDireccion() != null) {
            direccion = direccionesRepository.findById(dto.getIdDireccion())
                    .orElseThrow(() -> new RuntimeException("Dirección no encontrada con id " + dto.getIdDireccion()));
        }

        Cliente cliente = new Cliente();
        cliente.setDireccion(direccion);
        cliente.setTipoDocumento(tipoDocOpt.get());
        cliente.setContraseña(contraseña);
        cliente.setTipoUsuario(tipoUsuario);
        cliente.setNumeroDocumento(dto.getNumeroDocumento());
        cliente.setNombreCliente(dto.getNombreCliente());
        cliente.setApellidoCliente(dto.getApellidoCliente());
        cliente.setEmailCliente(dto.getEmailCliente());

        Cliente saved = clienteRepository.save(cliente);
        return mapClienteToDto(saved);
    }

    // ---------- mappers privados ----------
    private DireccionResponseDTO mapDireccionToDto(Direcciones d) {
        DireccionResponseDTO dto = new DireccionResponseDTO();
        dto.setIdDireccion(d.getIdDireccion());
        dto.setDireccionCliente(d.getDireccionCliente());
        dto.setCodigoPostal(d.getCodigoPostal());

        Ciudad c = d.getCiudad();
        if (c != null) {
            CiudadSimpleDTO cDto = new CiudadSimpleDTO();
            cDto.setIdCiudad(c.getIdCiudad());
            cDto.setNombreCiudad(c.getNombreCiudad());
            dto.setCiudad(cDto);
        }
        return dto;
    }

    private ClienteResponseDTO mapClienteToDto(Cliente c) {
        ClienteResponseDTO dto = new ClienteResponseDTO();
        dto.setIdCliente(c.getIdCliente());
        dto.setNumeroDocumento(c.getNumeroDocumento());
        dto.setNombreCliente(c.getNombreCliente());
        dto.setApellidoCliente(c.getApellidoCliente());
        dto.setEmailCliente(c.getEmailCliente());

        if (c.getDireccion() != null) {
            dto.setDireccion(mapDireccionToDto(c.getDireccion()));
        }

        return dto;
    }
}

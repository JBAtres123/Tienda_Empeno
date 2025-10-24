package com.online.tienda_empeno.service;

import com.online.tienda_empeno.dto.*;
import com.online.tienda_empeno.entity.*;
import com.online.tienda_empeno.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ContratosYPagosService {

    private final ContratosRepository contratosRepository;
    private final PrestamosRepository prestamosRepository;
    private final PagosPrestamoRepository pagosPrestamoRepository;
    private final MetodoPagoRepository metodoPagoRepository;
    private final TarjetasRepository tarjetasRepository;
    private final FacturaRepository facturaRepository;
    private final CobranzaRepository cobranzaRepository;
    private final RutaDeCobranzaRepository rutaDeCobranzaRepository;
    private final ArticulosRepository articulosRepository;
    private final AdministradorRepository administradorRepository;
    private final DepartamentoRepository departamentoRepository;

    public ContratosYPagosService(ContratosRepository contratosRepository,
                                  PrestamosRepository prestamosRepository,
                                  PagosPrestamoRepository pagosPrestamoRepository,
                                  MetodoPagoRepository metodoPagoRepository,
                                  TarjetasRepository tarjetasRepository,
                                  FacturaRepository facturaRepository,
                                  CobranzaRepository cobranzaRepository,
                                  RutaDeCobranzaRepository rutaDeCobranzaRepository,
                                  ArticulosRepository articulosRepository,
                                  AdministradorRepository administradorRepository,
                                  DepartamentoRepository departamentoRepository) {
        this.contratosRepository = contratosRepository;
        this.prestamosRepository = prestamosRepository;
        this.pagosPrestamoRepository = pagosPrestamoRepository;
        this.metodoPagoRepository = metodoPagoRepository;
        this.tarjetasRepository = tarjetasRepository;
        this.facturaRepository = facturaRepository;
        this.cobranzaRepository = cobranzaRepository;
        this.rutaDeCobranzaRepository = rutaDeCobranzaRepository;
        this.articulosRepository = articulosRepository;
        this.administradorRepository = administradorRepository;
        this.departamentoRepository = departamentoRepository;
    }

    // ==================== CONTRATOS ====================

    public List<ContratoResponseDTO> listarContratosPendientes(Integer idCliente) {
        List<Contratos> contratos = contratosRepository.findPendientesByClienteId(idCliente);
        return contratos.stream().map(this::mapContratoToDto).collect(Collectors.toList());
    }

    public ContratoResponseDTO obtenerContratoPorId(Integer idContrato, Integer idCliente) {
        Contratos contrato = contratosRepository.findById(idContrato)
                .orElseThrow(() -> new RuntimeException("Contrato no encontrado"));

        if (!contrato.getPrestamo().getCliente().getIdCliente().equals(idCliente)) {
            throw new RuntimeException("No tienes permiso para ver este contrato");
        }
        return mapContratoToDto(contrato);
    }

    @Transactional
    public ContratoResponseDTO firmarContrato(Integer idContrato, FirmarContratoDTO dto, Integer idCliente) {
        Contratos contrato = contratosRepository.findById(idContrato)
                .orElseThrow(() -> new RuntimeException("Contrato no encontrado"));

        if (!contrato.getPrestamo().getCliente().getIdCliente().equals(idCliente)) {
            throw new RuntimeException("No tienes permiso para firmar este contrato");
        }

        if (contrato.getIdEstado() != 10) {
            throw new RuntimeException("Este contrato ya fue firmado o rechazado");
        }

        contrato.setFirmaCliente(dto.getFirmaCliente());
        contrato.setIdEstado(11);
        contrato.setFechaFirma(LocalDateTime.now());
        contratosRepository.save(contrato);

        // Cambiar artículo a estado 5 (En Préstamo)
        Articulos articulo = contrato.getPrestamo().getArticulo();
        articulo.setIdEstado(5);
        articulosRepository.save(articulo);

        // Cambiar préstamo a estado 5 (Activo)
        Prestamos prestamo = contrato.getPrestamo();
        prestamo.setIdEstado(5);
        prestamosRepository.save(prestamo);

        return mapContratoToDto(contrato);
    }

    // ==================== PAGOS CON TARJETA ====================

    @Transactional
    public FacturaResponseDTO pagarConTarjeta(PagoTarjetaDTO dto, Integer idCliente) {
        Prestamos prestamo = prestamosRepository.findById(dto.getIdPrestamo())
                .orElseThrow(() -> new RuntimeException("Préstamo no encontrado"));

        if (!prestamo.getCliente().getIdCliente().equals(idCliente)) {
            throw new RuntimeException("Este préstamo no te pertenece");
        }

        // Validar que el préstamo esté activo (estado 5)
        if (prestamo.getIdEstado() != 5) {
            throw new RuntimeException("El préstamo no está activo para recibir pagos");
        }

        // Validar que haya saldo pendiente
        if (prestamo.getSaldoAdeudado() == null || prestamo.getSaldoAdeudado().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Este préstamo ya está liquidado");
        }

        MetodoPago metodoPago = metodoPagoRepository.findById(1)
                .orElseThrow(() -> new RuntimeException("Método de pago no encontrado"));

        // ✨ CALCULAR MONTO A PAGAR (cuota fija del monto total inicial / plazo)
        BigDecimal montoTotalOriginal = prestamo.getMontoPrestamo()
                .multiply(BigDecimal.ONE.add(prestamo.getTasaInteres()
                        .multiply(BigDecimal.valueOf(prestamo.getPlazoMeses()))
                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)));

        BigDecimal cuotaMensual = montoTotalOriginal
                .divide(BigDecimal.valueOf(prestamo.getPlazoMeses()), 2, RoundingMode.HALF_UP);

        // ✨ CLAVE: Si el saldo restante es MENOR que la cuota, cobrar solo el saldo
        BigDecimal montoPago;
        if (prestamo.getSaldoAdeudado().compareTo(cuotaMensual) < 0) {
            montoPago = prestamo.getSaldoAdeudado();
        } else {
            montoPago = cuotaMensual;
        }

        // Agregar mora si está vencido
        BigDecimal montoMora = BigDecimal.ZERO;
        if (prestamo.getFechaVencimiento() != null && LocalDate.now().isAfter(prestamo.getFechaVencimiento())) {
            montoMora = new BigDecimal("25.00");
        }

        BigDecimal montoTotal = montoPago.add(montoMora);

        // Validación final: el monto total no puede exceder el saldo
        if (montoTotal.compareTo(prestamo.getSaldoAdeudado()) > 0) {
            montoTotal = prestamo.getSaldoAdeudado();
        }

        // 💳 Guardar tarjeta
        Tarjetas tarjeta = new Tarjetas();
        tarjeta.setCliente(prestamo.getCliente());
        tarjeta.setMetodoPago(metodoPago);
        tarjeta.setNumeroTarjeta(enmascararTarjeta(dto.getNumeroTarjeta()));
        tarjeta.setFechaExpiracion(dto.getFechaExpiracion());
        tarjeta.setCvv("***");
        tarjetasRepository.save(tarjeta);

        // 💰 Crear pago
        PagosPrestamo pago = new PagosPrestamo();
        pago.setPrestamo(prestamo);
        pago.setMetodoPago(metodoPago);
        pago.setAdministrador(prestamo.getAdministrador()); // Asignar admin que aprobó el préstamo
        pago.setMonto(montoTotal);
        pago.setMesPago(LocalDate.now());
        PagosPrestamo pagoGuardado = pagosPrestamoRepository.save(pago);

        // ✨ RESTAR DEL SALDO ADEUDADO
        BigDecimal nuevoSaldo = prestamo.getSaldoAdeudado().subtract(montoTotal);
        prestamo.setSaldoAdeudado(nuevoSaldo);

        boolean prestamoCancelado = false;

        // ✨ SI EL SALDO LLEGA A 0, CAMBIAR ESTADOS
        if (nuevoSaldo.compareTo(BigDecimal.ZERO) <= 0) {
            // Artículo a estado 7 (Recuperado)
            Articulos articulo = prestamo.getArticulo();
            articulo.setIdEstado(7);
            articulosRepository.save(articulo);

            // Préstamo a estado 6 (Liquidado)
            prestamo.setIdEstado(6);
            prestamoCancelado = true;
        }

        prestamosRepository.save(prestamo);

        // 🧾 Crear factura
        Factura factura = new Factura();
        factura.setPagoPrestamo(pagoGuardado);
        factura.setFechaPago(LocalDate.now());
        Factura facturaGuardada = facturaRepository.save(factura);

        FacturaResponseDTO facturaDTO = mapFacturaToDto(facturaGuardada);
        facturaDTO.setSaldoRestante(nuevoSaldo);
        facturaDTO.setPrestamoCancelado(prestamoCancelado);

        if (prestamoCancelado) {
            facturaDTO.setMensajeAdicional("¡Felicidades! Has liquidado completamente tu préstamo. Ya puedes recuperar tu artículo.");
        }

        return facturaDTO;
    }

    // ==================== PAGOS EN EFECTIVO ====================

    @Transactional
    public CobranzaResponseDTO pagarEnEfectivo(PagoEfectivoDTO dto, Integer idCliente) {
        Prestamos prestamo = prestamosRepository.findById(dto.getIdPrestamo())
                .orElseThrow(() -> new RuntimeException("Préstamo no encontrado"));

        if (!prestamo.getCliente().getIdCliente().equals(idCliente)) {
            throw new RuntimeException("Este préstamo no te pertenece");
        }

        // Validar que el préstamo esté activo (estado 5)
        if (prestamo.getIdEstado() != 5) {
            throw new RuntimeException("El préstamo no está activo para recibir pagos");
        }

        // Validar que haya saldo pendiente
        if (prestamo.getSaldoAdeudado() == null || prestamo.getSaldoAdeudado().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Este préstamo ya está liquidado");
        }

        MetodoPago metodoPago = metodoPagoRepository.findById(2)
                .orElseThrow(() -> new RuntimeException("Método de pago no encontrado"));

        List<Administradores> cobradores = administradorRepository.findAll().stream()
                .filter(a -> a.getIdRol() == 4).collect(Collectors.toList());
        if (cobradores.isEmpty()) throw new RuntimeException("No hay cobradores disponibles");

        Administradores cobrador = cobradores.get(0);

        // ✨ CALCULAR MONTO A PAGAR (cuota fija del monto total inicial / plazo)
        BigDecimal montoTotalOriginal = prestamo.getMontoPrestamo()
                .multiply(BigDecimal.ONE.add(prestamo.getTasaInteres()
                        .multiply(BigDecimal.valueOf(prestamo.getPlazoMeses()))
                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)));

        BigDecimal cuotaMensual = montoTotalOriginal
                .divide(BigDecimal.valueOf(prestamo.getPlazoMeses()), 2, RoundingMode.HALF_UP);

        // ✨ CLAVE: Si el saldo restante es MENOR que la cuota, cobrar solo el saldo
        BigDecimal montoPago;
        if (prestamo.getSaldoAdeudado().compareTo(cuotaMensual) < 0) {
            montoPago = prestamo.getSaldoAdeudado();
        } else {
            montoPago = cuotaMensual;
        }

        // Agregar mora si está vencido
        BigDecimal montoMora = BigDecimal.ZERO;
        if (prestamo.getFechaVencimiento() != null && LocalDate.now().isAfter(prestamo.getFechaVencimiento())) {
            montoMora = new BigDecimal("25.00");
        }

        BigDecimal montoTotal = montoPago.add(montoMora);

        // Validación final
        if (montoTotal.compareTo(prestamo.getSaldoAdeudado()) > 0) {
            montoTotal = prestamo.getSaldoAdeudado();
        }

        System.out.println("DEBUG: Creando pago - Monto: " + montoTotal + ", Saldo adeudado actual: " + prestamo.getSaldoAdeudado());

        PagosPrestamo pago = new PagosPrestamo();
        pago.setPrestamo(prestamo);
        pago.setMetodoPago(metodoPago);
        pago.setAdministrador(cobrador);
        pago.setMonto(montoTotal);
        pago.setMesPago(LocalDate.now());

        System.out.println("DEBUG: Guardando pago...");
        PagosPrestamo pagoGuardado = pagosPrestamoRepository.save(pago);
        System.out.println("DEBUG: Pago guardado exitosamente con ID: " + pagoGuardado.getIdPagoPrestamo());

        // ✨ RESTAR DEL SALDO ADEUDADO
        System.out.println("DEBUG: Calculando nuevo saldo...");
        BigDecimal nuevoSaldo = prestamo.getSaldoAdeudado().subtract(montoTotal);
        System.out.println("DEBUG: Nuevo saldo: " + nuevoSaldo);
        prestamo.setSaldoAdeudado(nuevoSaldo);

        boolean prestamoCancelado = false;

        // ✨ SI EL SALDO LLEGA A 0, CAMBIAR ESTADOS
        if (nuevoSaldo.compareTo(BigDecimal.ZERO) <= 0) {
            System.out.println("DEBUG: Préstamo liquidado, actualizando estados...");
            // Artículo a estado 7 (Recuperado)
            Articulos articulo = prestamo.getArticulo();
            articulo.setIdEstado(7);
            articulosRepository.save(articulo);

            // Préstamo a estado 6 (Liquidado)
            prestamo.setIdEstado(6);
            prestamoCancelado = true;
        }

        System.out.println("DEBUG: Guardando préstamo actualizado...");
        prestamosRepository.save(prestamo);
        System.out.println("DEBUG: Préstamo guardado exitosamente");

        // Siempre asignar fecha de visita para pago en efectivo (el cobrador debe ir a cobrar)
        LocalDate fechaVisita = LocalDate.now().plusDays(15);
        String comentario;

        if (prestamoCancelado) {
            comentario = String.format(
                    "¡PRÉSTAMO LIQUIDADO!\n\n" +
                            "Estimado/a %s, felicitaciones por completar el pago de su préstamo.\n\n" +
                            "Nuestro cobrador visitará su domicilio el día %s para cobrar el último pago.\n\n" +
                            "Último pago: Q%.2f\n" +
                            "Saldo final: Q0.00\n\n" +
                            "Ya puede recuperar su artículo en nuestra sucursal.\n\n" +
                            "Casa de Empeño - Atención al Cliente",
                    prestamo.getCliente().getNombreCliente(),
                    fechaVisita.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                    montoTotal
            );
        } else {
            comentario = String.format(
                    "Estimado/a %s, nuestro cobrador visitará su domicilio el día %s.\n\n" +
                            "Cuota mensual: Q%.2f\n%s" +
                            "Monto a pagar: Q%.2f\n" +
                            "Saldo restante: Q%.2f\n\n" +
                            "Casa de Empeño - Atención al Cliente",
                    prestamo.getCliente().getNombreCliente(),
                    fechaVisita.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                    cuotaMensual,
                    montoMora.compareTo(BigDecimal.ZERO) > 0 ? String.format("Mora por atraso: Q%.2f\n", montoMora) : "",
                    montoTotal,
                    nuevoSaldo
            );
        }

        System.out.println("DEBUG: Creando cobranza...");
        Cobranza cobranza = new Cobranza();
        cobranza.setPagoPrestamo(pagoGuardado);
        cobranza.setFechaDeVisita(fechaVisita);
        cobranza.setComentario(comentario);
        Cobranza cobranzaGuardada = cobranzaRepository.save(cobranza);
        System.out.println("DEBUG: Cobranza guardada con ID: " + cobranzaGuardada.getIdCobranza());

        System.out.println("DEBUG: Obteniendo departamento del cliente...");
        Integer idDepartamento = obtenerIdDepartamentoCliente(prestamo.getCliente());
        System.out.println("DEBUG: ID Departamento: " + idDepartamento);
        String rutaAsignada = prestamoCancelado ? "PRÉSTAMO LIQUIDADO" : generarRutaPorDepartamento(idDepartamento);
        System.out.println("DEBUG: Ruta asignada: " + rutaAsignada);

        Departamento departamento = null;
        if (idDepartamento != null) {
            departamento = departamentoRepository.findById(idDepartamento).orElse(null);
        }

        System.out.println("DEBUG: Creando ruta de cobranza...");
        RutaDeCobranza ruta = new RutaDeCobranza();
        ruta.setCobranza(cobranzaGuardada);
        ruta.setDepartamento(departamento);
        ruta.setAsignacionRuta(rutaAsignada);
        rutaDeCobranzaRepository.save(ruta);
        System.out.println("DEBUG: Ruta de cobranza guardada exitosamente");

        System.out.println("DEBUG: Creando DTO de respuesta...");
        CobranzaResponseDTO cobranzaDTO = mapCobranzaToDto(cobranzaGuardada, rutaAsignada);
        cobranzaDTO.setSaldoRestante(nuevoSaldo);
        cobranzaDTO.setPrestamoCancelado(prestamoCancelado);

        System.out.println("DEBUG: Proceso completado exitosamente. Retornando DTO.");
        return cobranzaDTO;
    }

    // ==================== LISTAR COBRANZAS DEL CLIENTE ====================

    public List<CobranzaResponseDTO> listarCobranzasCliente(Integer idCliente) {
        List<Cobranza> cobranzas = cobranzaRepository.findByClienteId(idCliente);

        return cobranzas.stream().map(c -> {
            RutaDeCobranza ruta = rutaDeCobranzaRepository.findByCobranzaId(c.getIdCobranza());
            String asignacionRuta = ruta != null ? ruta.getAsignacionRuta() : "Sin asignar";

            CobranzaResponseDTO dto = mapCobranzaToDto(c, asignacionRuta);

            Prestamos prestamo = c.getPagoPrestamo().getPrestamo();
            dto.setSaldoRestante(prestamo.getSaldoAdeudado());
            dto.setPrestamoCancelado(prestamo.getIdEstado() == 6);

            return dto;
        }).collect(Collectors.toList());
    }

    // ==================== HELPERS ====================

    private Integer obtenerIdDepartamentoCliente(Cliente cliente) {
        if (cliente.getDireccion() != null &&
                cliente.getDireccion().getCiudad() != null &&
                cliente.getDireccion().getCiudad().getDepartamento() != null) {
            return cliente.getDireccion().getCiudad().getDepartamento().getIdDepartamento();
        }
        return null;
    }

    private String generarRutaPorDepartamento(Integer idDepartamento) {
        if (idDepartamento == null) return "Ruta General - Sin departamento asignado";
        Departamento departamento = departamentoRepository.findById(idDepartamento).orElse(null);
        if (departamento != null) return "Ruta " + departamento.getNombreDepartamento();
        return "Ruta General";
    }

    private String enmascararTarjeta(String numero) {
        if (numero.length() < 4) return "****";
        return "**** **** **** " + numero.substring(numero.length() - 4);
    }

    private String obtenerDireccionCliente(Cliente cliente) {
        if (cliente.getDireccion() != null) {
            return cliente.getDireccion().getDireccionCliente() + ", " +
                    cliente.getDireccion().getCiudad().getNombreCiudad();
        }
        return "Dirección no registrada";
    }

    private ContratoResponseDTO mapContratoToDto(Contratos c) {
        ContratoResponseDTO dto = new ContratoResponseDTO();
        dto.setIdContrato(c.getIdContrato());
        dto.setIdPrestamo(c.getPrestamo().getIdPrestamo());
        dto.setNombreArticulo(c.getPrestamo().getArticulo().getNombreArticulo());
        dto.setMontoPrestamo(c.getPrestamo().getMontoPrestamo());
        dto.setSaldoAdeudado(c.getPrestamo().getSaldoAdeudado());
        dto.setTasaInteres(c.getPrestamo().getTasaInteres());
        dto.setPlazoMeses(c.getPrestamo().getPlazoMeses());
        dto.setFechaInicio(c.getPrestamo().getFechaInicio());
        dto.setFechaVencimiento(c.getPrestamo().getFechaVencimiento());
        dto.setEstado(obtenerNombreEstadoContrato(c.getIdEstado()));
        dto.setFechaCreacion(c.getFechaCreacion());
        dto.setFechaFirma(c.getFechaFirma());
        dto.setFirmaCliente(c.getFirmaCliente());
        dto.setDocumentoContrato(c.getDocumentoContrato());
        dto.setNombreCliente(c.getPrestamo().getCliente().getNombreCliente() + " " + c.getPrestamo().getCliente().getApellidoCliente());
        dto.setEmailCliente(c.getPrestamo().getCliente().getEmailCliente());
        dto.setDpiCliente(c.getPrestamo().getCliente().getNumeroDocumento());
        return dto;
    }

    private FacturaResponseDTO mapFacturaToDto(Factura f) {
        FacturaResponseDTO dto = new FacturaResponseDTO();
        dto.setIdFactura(f.getIdFactura());
        dto.setIdPagoPrestamo(f.getPagoPrestamo().getIdPagoPrestamo());
        dto.setFechaPago(f.getFechaPago());
        dto.setMonto(f.getPagoPrestamo().getMonto());
        dto.setNombreCliente(f.getPagoPrestamo().getPrestamo().getCliente().getNombreCliente() + " " +
                f.getPagoPrestamo().getPrestamo().getCliente().getApellidoCliente());
        dto.setNombreArticulo(f.getPagoPrestamo().getPrestamo().getArticulo().getNombreArticulo());
        dto.setMetodoPago(f.getPagoPrestamo().getMetodoPago().getNombreMetodoPago());
        dto.setIdPrestamo(f.getPagoPrestamo().getPrestamo().getIdPrestamo());
        return dto;
    }

    private CobranzaResponseDTO mapCobranzaToDto(Cobranza c, String asignacionRuta) {
        CobranzaResponseDTO dto = new CobranzaResponseDTO();
        dto.setIdCobranza(c.getIdCobranza());
        dto.setIdPagoPrestamo(c.getPagoPrestamo().getIdPagoPrestamo());
        dto.setComentario(c.getComentario());
        dto.setFechaDeVisita(c.getFechaDeVisita());
        dto.setMonto(c.getPagoPrestamo().getMonto());
        dto.setNombreCliente(c.getPagoPrestamo().getPrestamo().getCliente().getNombreCliente() + " " +
                c.getPagoPrestamo().getPrestamo().getCliente().getApellidoCliente());
        dto.setDireccionCliente(obtenerDireccionCliente(c.getPagoPrestamo().getPrestamo().getCliente()));

        if (c.getPagoPrestamo().getAdministrador() != null) {
            dto.setNombreCobrador(c.getPagoPrestamo().getAdministrador().getNombreAdmin() + " " +
                    c.getPagoPrestamo().getAdministrador().getApellidoAdmin());
        } else {
            dto.setNombreCobrador("Sin asignar");
        }

        dto.setAsignacionRuta(asignacionRuta);
        return dto;
    }

    private String obtenerNombreEstadoContrato(Integer idEstado) {
        switch (idEstado) {
            case 10: return "Pendiente";
            case 11: return "Firmado";
            case 4: return "Rechazado";
            default: return "Desconocido";
        }
    }
}
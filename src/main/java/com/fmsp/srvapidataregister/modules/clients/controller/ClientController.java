package com.fmsp.srvapidataregister.modules.clients.controller;

import com.fmsp.srvapidataregister.core.payload.ApiResponse;
import com.fmsp.srvapidataregister.core.payload.ErrorItemDTO;
import com.fmsp.srvapidataregister.core.payload.ResponseHandler;
import com.fmsp.srvapidataregister.modules.clients.dto.ClienteDTO;
import com.fmsp.srvapidataregister.modules.clients.dto.ClienteUpdateDTO;
import com.fmsp.srvapidataregister.modules.clients.service.IClienteService;
import com.fmsp.srvapidataregister.modules.clients.service.impl.ClienteService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("client")
public class ClientController {

    private final IClienteService clienteService;

    public ClientController(IClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @PostMapping(value = "/create", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<ApiResponse<Object>> create(
            @RequestPart("cliente") @Valid ClienteDTO clienteDTO,
            @RequestPart(value = "camaraComercio", required = false) MultipartFile camaraComercio,
            @RequestPart(value = "rut", required = false) MultipartFile rut,
            BindingResult result
    ) {
        String uuid = UUID.randomUUID().toString();
        if (result.hasErrors()) {
            List<ErrorItemDTO> errores = result.getFieldErrors().stream()
                    .map(error -> new ErrorItemDTO(
                            "E400",
                            error.getDefaultMessage(),
                            error.getField()))
                    .collect(Collectors.toList());

            return ResponseHandler.badRequestResponse(errores, uuid);
        }

        // Subida opcional de documentos a S3 usando prefijo "documentos/"
        if (camaraComercio != null && !camaraComercio.isEmpty()) {
            String urlCamara = ((ClienteService) clienteService)
                    .uploadDocIfPresent("documentos/camaraComercio_", camaraComercio);
            clienteDTO.setCamaraComercio(urlCamara);
        }
        if (rut != null && !rut.isEmpty()) {
            String urlRut = ((ClienteService) clienteService)
                    .uploadDocIfPresent("documentos/rut_", rut);
            clienteDTO.setRut(urlRut);
        }

        var client = clienteService.createClient(clienteDTO, uuid);
        return ResponseHandler.successResponse(client, uuid);
    }

    @GetMapping("/list")
    public ResponseEntity<ApiResponse<Object>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction
    ) {
        String uuid = UUID.randomUUID().toString();
        var pageResult = ((ClienteService) clienteService)
                .listarClientes(page, size, sortBy, direction);

        var payload = new java.util.HashMap<String, Object>();
        payload.put("items", pageResult.getContent());
        payload.put("page", pageResult.getNumber());
        payload.put("size", pageResult.getSize());
        payload.put("totalElements", pageResult.getTotalElements());
        payload.put("totalPages", pageResult.getTotalPages());
        payload.put("last", pageResult.isLast());
        payload.put("sort", pageResult.getSort().toString());

        return ResponseHandler.successResponse(payload, uuid);
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Object>> search(
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction
    ) {
        String uuid = UUID.randomUUID().toString();

        var pageResult = ((ClienteService) clienteService)
                .searchClientes(q, page, size, sortBy, direction);

        var payload = new java.util.HashMap<String, Object>();
        payload.put("items", pageResult.getContent());
        payload.put("page", pageResult.getNumber());
        payload.put("size", pageResult.getSize());
        payload.put("totalElements", pageResult.getTotalElements());
        payload.put("totalPages", pageResult.getTotalPages());
        payload.put("last", pageResult.isLast());
        payload.put("sort", pageResult.getSort().toString());
        payload.put("query", q);

        return ResponseHandler.successResponse(payload, uuid);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse<Object>> delete(@PathVariable("id") Long id) {
        String uuid = UUID.randomUUID().toString();
        ((ClienteService) clienteService).deleteCliente(id, uuid);

        var payload = new java.util.HashMap<String, Object>();
        payload.put("deletedId", id);
        payload.put("message", "Cliente eliminado correctamente");

        return ResponseHandler.successResponse(payload, uuid);
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<ApiResponse<Object>> getDetail(@PathVariable("id") Long id) {
        String uuid = UUID.randomUUID().toString();
        var detail = ((ClienteService) clienteService).getClienteDetail(id, uuid);
        return ResponseHandler.successResponse(detail, uuid);
    }

    @PutMapping(value = "/update/{id}", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<ApiResponse<Object>> update(
            @PathVariable("id") Long id,
            @RequestPart("cliente") @Valid ClienteUpdateDTO dto,
            @RequestPart(value = "camaraComercio", required = false) MultipartFile camaraComercio,
            @RequestPart(value = "rut", required = false) MultipartFile rut,
            BindingResult result
    ) {
        String uuid = UUID.randomUUID().toString();

        if (result.hasErrors()) {
            List<ErrorItemDTO> errores = result.getFieldErrors().stream()
                    .map(error -> new ErrorItemDTO(
                            "E400",
                            error.getDefaultMessage(),
                            error.getField()))
                    .collect(Collectors.toList());

            return ResponseHandler.badRequestResponse(errores, uuid);
        }

        // Si envían nuevos archivos, los subimos y pisamos URLs
        if (camaraComercio != null && !camaraComercio.isEmpty()) {
            String urlCamara = ((ClienteService) clienteService)
                    .uploadDocIfPresent("documentos/camaraComercio_", camaraComercio);
            dto.setCamaraComercio(urlCamara);
        }
        if (rut != null && !rut.isEmpty()) {
            String urlRut = ((ClienteService) clienteService)
                    .uploadDocIfPresent("documentos/rut_", rut);
            dto.setRut(urlRut);
        }
        // Si el JSON viene con camaraComercio/rut = null o "", en el service ya se pisan esos campos,
        // lo que efectivamente borra la referencia al documento anterior.

        var updated = ((ClienteService) clienteService).updateCliente(id, dto, uuid);
        return ResponseHandler.successResponse(updated, uuid);
    }

}
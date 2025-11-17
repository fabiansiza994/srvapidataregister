package com.fmsp.srvapidataregister.modules.users.controller;

import com.fmsp.srvapidataregister.core.payload.ApiResponse;
import com.fmsp.srvapidataregister.core.payload.ErrorItemDTO;
import com.fmsp.srvapidataregister.core.payload.ResponseHandler;
import com.fmsp.srvapidataregister.modules.users.dto.RecoveryDTO;
import com.fmsp.srvapidataregister.modules.users.dto.RegistroDTO;
import com.fmsp.srvapidataregister.modules.users.dto.UsuarioUpdateDTO;
import com.fmsp.srvapidataregister.modules.users.service.IUsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/user")
@CrossOrigin(origins = "*")
public class UserController {

    private final IUsuarioService usuarioService;

    public UserController(IUsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Object>> register(@RequestBody @Valid RegistroDTO request, BindingResult result) {
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
        var response = usuarioService.registerUsuario(request, uuid);
        return ResponseHandler.successResponse(response, uuid);
    }

    @GetMapping("/list")
    public ResponseEntity<ApiResponse<Object>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction
    ) {
        String uuid = UUID.randomUUID().toString();
        var pageResult = usuarioService.list(page, size, sortBy, direction);

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
        var pageResult = usuarioService.search(q, page, size, sortBy, direction);

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

    @GetMapping("/detail/{id}")
    public ResponseEntity<ApiResponse<Object>> detail(@PathVariable Long id) {
        String uuid = UUID.randomUUID().toString();
        var detail = usuarioService.detail(id, uuid);
        return ResponseHandler.successResponse(detail, uuid);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse<Object>> update(@PathVariable Long id,
                                                      @RequestBody @Valid UsuarioUpdateDTO dto,
                                                      BindingResult result) {
        String uuid = UUID.randomUUID().toString();

        if (result.hasErrors()) {
            List<ErrorItemDTO> errores = result.getFieldErrors().stream()
                    .map(error -> new ErrorItemDTO("E400", error.getDefaultMessage(), error.getField()))
                    .collect(Collectors.toList());
            return ResponseHandler.badRequestResponse(errores, uuid);
        }
        var updated = usuarioService.update(id, dto, uuid);
        return ResponseHandler.successResponse(updated, uuid);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse<Object>> delete(@PathVariable Long id) {
        String uuid = UUID.randomUUID().toString();
        usuarioService.delete(id, uuid);

        var payload = new java.util.HashMap<String, Object>();
        payload.put("deletedId", id);
        payload.put("message", "Usuario eliminado correctamente");
        return ResponseHandler.successResponse(payload, uuid);
    }

    @GetMapping("/profile/{id}")
    public ResponseEntity<ApiResponse<Object>> profile(@PathVariable Long id) {
        String uuid = UUID.randomUUID().toString();
        var profile = usuarioService.profile(id, uuid);
        return ResponseHandler.successResponse(profile, uuid);
    }

    @PostMapping("/recoverAccount")
    public ResponseEntity<ApiResponse<Object>> recoverAccount(@RequestBody RecoveryDTO recoveryDTO) {
        String uuid = UUID.randomUUID().toString();
        var response = usuarioService.recoverAccount(recoveryDTO, uuid);
        return ResponseHandler.successResponse(response, uuid);
    }

}
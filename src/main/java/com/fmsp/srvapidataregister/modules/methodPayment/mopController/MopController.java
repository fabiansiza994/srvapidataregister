package com.fmsp.srvapidataregister.modules.methodPayment.mopController;

import com.fmsp.srvapidataregister.core.payload.ApiResponse;
import com.fmsp.srvapidataregister.core.payload.ResponseHandler;
import com.fmsp.srvapidataregister.modules.methodPayment.dto.FormaPagoCreateDTO;
import com.fmsp.srvapidataregister.modules.methodPayment.dto.FormaPagoDTO;
import com.fmsp.srvapidataregister.modules.methodPayment.service.IMOPService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("mop")
public class MopController {

    private final IMOPService mopService;

    public MopController(IMOPService mopService) {
        this.mopService = mopService;
    }

    @GetMapping("list/{empresaId}")
    public ResponseEntity<ApiResponse<Object>> list(@PathVariable Long empresaId) {
        String uuid = UUID.randomUUID().toString();
        var mop = mopService.findAllByEmpresa(empresaId);
        return ResponseHandler.successResponse(mop, uuid);
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<Object>> create(@RequestBody FormaPagoCreateDTO formaPagoDTO) {
        String uuid = UUID.randomUUID().toString();
        var mop = mopService.save(formaPagoDTO);
        return ResponseHandler.successResponse(mop, uuid);
    }

    @DeleteMapping("delete/{methodId}")
    public ResponseEntity<ApiResponse<Object>> delete(@PathVariable Long methodId) {
        String uuid = UUID.randomUUID().toString();
        mopService.deleteById(methodId);
        return ResponseHandler.successResponse("OK", uuid);
    }

    @PutMapping("/update")
    public ResponseEntity<ApiResponse<Object>> update(@RequestBody FormaPagoDTO formaPagoDTO) {
        String uuid = UUID.randomUUID().toString();
        mopService.update(formaPagoDTO);
        return ResponseHandler.successResponse(formaPagoDTO, uuid);
    }
}

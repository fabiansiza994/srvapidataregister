package com.fmsp.srvapidataregister.modules.jobs.service.impl;

import com.fmsp.srvapidataregister.core.exceptions.CustomServiceException;
import com.fmsp.srvapidataregister.modules.S3.service.S3Service;
import com.fmsp.srvapidataregister.modules.auth.service.PermisoService;
import com.fmsp.srvapidataregister.modules.clients.dto.ClientePlanoDTO;
import com.fmsp.srvapidataregister.modules.clients.service.IClienteService;
import com.fmsp.srvapidataregister.modules.jobs.dto.TrabajoCreateDTO;
import com.fmsp.srvapidataregister.modules.jobs.dto.TrabajoDTO;
import com.fmsp.srvapidataregister.modules.jobs.dto.TrabajoDetailDTO;
import com.fmsp.srvapidataregister.modules.jobs.dto.TrabajoListDTO;
import com.fmsp.srvapidataregister.modules.jobs.entity.Trabajo;
import com.fmsp.srvapidataregister.modules.jobs.repository.TrabajoRepository;
import com.fmsp.srvapidataregister.modules.jobs.service.IJobService;
import com.fmsp.srvapidataregister.modules.methodPayment.dto.FormaPagoDTO;
import com.fmsp.srvapidataregister.modules.methodPayment.service.IMOPService;
import com.fmsp.srvapidataregister.modules.paciente.dto.PacienteDTO;
import com.fmsp.srvapidataregister.modules.paciente.service.IPacienteService;
import com.fmsp.srvapidataregister.modules.users.dto.UsuarioDTO;
import com.fmsp.srvapidataregister.modules.users.service.IUsuarioService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JobServiceImpl implements IJobService {

    private final TrabajoRepository trabajoRepository;
    private final PermisoService permisoService;
    private final IUsuarioService usuarioService;
    private final IClienteService clienteService;
    private final IPacienteService pacienteService;
    private final IMOPService imoService;
    private final S3Service s3Service;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public TrabajoDTO create(TrabajoCreateDTO dto, MultipartFile foto1, MultipartFile foto2, MultipartFile foto3,
                             MultipartFile foto4, String idTx) {

        PacienteDTO paciente = new PacienteDTO();
        TrabajoDTO trabajo = new TrabajoDTO();
        // 1) Usuario actual
        var usuarioDTO = permisoService.usuarioActual()
                .orElseThrow(() -> new CustomServiceException(idTx, "E003", "Usuario no autenticado"));
        UsuarioDTO usuario = usuarioService.getUsuarioById(usuarioDTO.getId())
                .orElseThrow(() -> new CustomServiceException(idTx, "E003", "Usuario no encontrado"));

        // 2) Entidades relacionadas
        ClientePlanoDTO cliente = clienteService.findById(dto.clienteId())
                .orElseThrow(() -> new CustomServiceException(idTx, "E004", "Cliente no encontrado"));
        if(dto.pacienteId() != null){
             paciente = pacienteService.findById(dto.pacienteId())
                    .orElseThrow(() -> new CustomServiceException(idTx, "E005", "Paciente no encontrado"));

            trabajo.setPaciente(paciente.getId());
        }

        FormaPagoDTO formaPago = imoService.findById(dto.formaPagoId())
                .orElseThrow(() -> new CustomServiceException(idTx, "E006", "Forma de pago no encontrada"));


        trabajo.setFecha(dto.fecha());
        trabajo.setValorLabor(dto.valorLabor());
        trabajo.setValorMateriales(dto.valorMateriales());
        trabajo.setValorTotal(dto.valorTotal());
        trabajo.setGanancias(dto.ganancias());
        trabajo.setDescripcionLabor(dto.descripcionLabor());
        trabajo.setCliente(cliente);

        trabajo.setFormaPago(formaPago);
        trabajo.setUsuario(usuario);


        // 4) Subir imágenes si existen (redimensionar + comprimir -> jpg)
        trabajo.setFoto1(uploadIfPresent("trabajos/foto1_", foto1));
        trabajo.setFoto2(uploadIfPresent("trabajos/foto2_", foto2));
        trabajo.setFoto3(uploadIfPresent("trabajos/foto3_", foto3));
        trabajo.setFoto4(uploadIfPresent("trabajos/foto4_", foto4));

        var saved = trabajoRepository.save(modelMapper.map(trabajo, Trabajo.class));

        return modelMapper.map(saved, TrabajoDTO.class);
    }

    @Override
    public long countByCliente_Id(Long clienteId) {
        return trabajoRepository.countByCliente_Id(clienteId);
    }

    @Transactional(readOnly = true)
    public Page<TrabajoListDTO> listarTrabajos(int page, int size, String sortBy, String direction) {

        String idTx = UUID.randomUUID().toString();

        Sort sort = "DESC".equalsIgnoreCase(direction)
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        if (permisoService.hasRole("ADMIN")) {
            Long empresaId = permisoService.empresaIdActualOrNull();
            if (empresaId == null) throw new CustomServiceException(idTx, "E005", "Empresa no asociada");
            return trabajoRepository.findAllByEmpresa(empresaId, pageable)
                    .map(t -> mapTrabajoToListDTO(t));
        }

        if (permisoService.hasRole("USER")) {
            Long grupoId = permisoService.grupoIdActualOrNull();
            if (grupoId == null) throw new CustomServiceException(idTx, "E005", "Grupo no asociado");
            return trabajoRepository.findAllByGrupo(grupoId, pageable)
                    .map(t -> mapTrabajoToListDTO(t));
        }

        throw new CustomServiceException(idTx, "E005", "Rol no permitido");
    }

    private TrabajoListDTO mapTrabajoToListDTO(Trabajo t) {
        TrabajoListDTO dto = modelMapper.map(t, TrabajoListDTO.class);
        if (t.getPacienteObj() != null) {
            dto.setPaciente(modelMapper.map(t.getPacienteObj(),
                    com.fmsp.srvapidataregister.modules.paciente.dto.PacienteDTO.class));
        } else {
            dto.setPaciente(null);
        }
        return dto;
    }

    @Transactional(readOnly = true)
    public Page<TrabajoListDTO> searchTrabajos(String q, int page, int size, String sortBy, String direction) {

        String idTx = UUID.randomUUID().toString();

        if (q == null || q.trim().isEmpty()) {
            return listarTrabajos(page, size, sortBy, direction);
        }
        Sort sort = "DESC".equalsIgnoreCase(direction)
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        String query = q.trim();

        if (permisoService.hasRole("ADMIN")) {
            Long empresaId = permisoService.empresaIdActualOrNull();
            if (empresaId == null) throw new CustomServiceException(idTx, "E005", "Empresa no asociada");
            return trabajoRepository.searchByEmpresa(empresaId, query, pageable)
                    .map(t -> mapTrabajoToListDTO(t));
        }

        if (permisoService.hasRole("USER")) {
            Long grupoId = permisoService.grupoIdActualOrNull();
            if (grupoId == null) throw new CustomServiceException(idTx, "E005", "Grupo no asociado");
            return trabajoRepository.searchByGrupo(grupoId, query, pageable)
                    .map(t -> mapTrabajoToListDTO(t));
        }

        throw new CustomServiceException(idTx, "E005", "Rol no permitido");
    }

    @Transactional(readOnly = true)
    public TrabajoDetailDTO getTrabajoDetail(Long trabajoId, String idTx) {
        var opt = trabajoRepository.findById(trabajoId);
        if (opt.isEmpty()) throw new CustomServiceException(idTx, "E404", "Trabajo no encontrado");
        Trabajo t = opt.get();

        // Alcance por rol
        if (permisoService.hasRole("ADMIN")) {
            Long empresaId = permisoService.empresaIdActualOrNull();
            Long empresaTrabajo = (t.getCliente() != null && t.getCliente().getEmpresa() != null)
                    ? t.getCliente().getEmpresa().getId() : null;
            if (empresaId == null || empresaTrabajo == null || !empresaId.equals(empresaTrabajo)) {
                throw new CustomServiceException(idTx, "E005", "El trabajo no pertenece a tu empresa");
            }
        } else if (permisoService.hasRole("USER")) {
            Long grupoId = permisoService.grupoIdActualOrNull();
            Long grupoTrabajo = (t.getUsuario() != null && t.getUsuario().getGrupo() != null)
                    ? t.getUsuario().getGrupo().getId() : null;
            if (grupoId == null || !grupoId.equals(grupoTrabajo)) {
                throw new CustomServiceException(idTx, "E005", "El trabajo no pertenece a tu grupo");
            }
        } else {
            throw new CustomServiceException(idTx, "E005", "Rol no permitido");
        }

        TrabajoDetailDTO dto = modelMapper.map(t, TrabajoDetailDTO.class);

        // Resolver paciente (en entidad es Long)
        if (t.getPaciente() != null) {
            PacienteDTO paciente = pacienteService.findById(t.getPaciente())
                    .orElse(null);
            dto.setPaciente(paciente);
        }

        return dto;
    }

    // ========= DELETE =========
    @Transactional
    public void deleteTrabajo(Long trabajoId, String idTx) {
        var opt = trabajoRepository.findById(trabajoId);
        if (opt.isEmpty()) throw new CustomServiceException(idTx, "E404", "Trabajo no encontrado");
        Trabajo t = opt.get();

        if (permisoService.hasRole("ADMIN")) {
            Long empresaId = permisoService.empresaIdActualOrNull();
            Long empresaTrabajo = (t.getCliente() != null && t.getCliente().getEmpresa() != null)
                    ? t.getCliente().getEmpresa().getId() : null;
            if (empresaId == null || empresaTrabajo == null || !empresaId.equals(empresaTrabajo)) {
                throw new CustomServiceException(idTx, "E005", "El trabajo no pertenece a tu empresa");
            }
        } else if (permisoService.hasRole("USER")) {
            Long grupoId = permisoService.grupoIdActualOrNull();
            Long grupoTrabajo = (t.getUsuario() != null && t.getUsuario().getGrupo() != null)
                    ? t.getUsuario().getGrupo().getId() : null;
            if (grupoId == null || !grupoId.equals(grupoTrabajo)) {
                throw new CustomServiceException(idTx, "E005", "El trabajo no pertenece a tu grupo");
            }
        } else {
            throw new CustomServiceException(idTx, "E005", "Rol no permitido");
        }

        // (Opcional) eliminar archivos S3 asociados si quieres
        // s3Service.deleteIfNotNull(t.getFoto1()); ...

        trabajoRepository.deleteById(trabajoId);
    }

    /**
     * Sube archivo. Si luce como imagen -> comprime a JPEG 800px, sino sube tal cual.
     */
    private String uploadIfPresent(String keyPrefix, MultipartFile file) {
        try {
            if (file == null || file.isEmpty()) return null;

            byte[] original = file.getBytes();
            String contentType = safeContentType(file);
            boolean isImage = isProbablyImage(contentType, file.getOriginalFilename(), original);

            if (!isImage) {
                return s3Service.uploadFile(keyPrefix, original, "application/octet-stream");
            }

            // Intentamos leer como imagen
            BufferedImage src = tryRead(original);
            if (src == null) {
                // si no se pudo leer, subir original como binario
                return s3Service.uploadFile(keyPrefix, original, "application/octet-stream");
            }

            byte[] jpeg = compressToJpeg(src, 800, 800, 0.8f);
            return s3Service.uploadFile(keyPrefix, jpeg, "image/jpeg");

        } catch (Exception e) {
            try {
                return s3Service.uploadFile(keyPrefix, file.getBytes(), "application/octet-stream");
            } catch (Exception ex) {
                throw new RuntimeException("Error subiendo archivo", ex);
            }
        }
    }

    private static String safeContentType(MultipartFile f) {
        String ct = f.getContentType();
        return (ct == null || ct.isBlank()) ? "application/octet-stream" : ct.toLowerCase(Locale.ROOT);
    }

    /**
     * Heurística: contentType, extensión y firmas JPG/PNG/WEBP + ImageIO.read
     */
    private static boolean isProbablyImage(String contentType, String originalName, byte[] bytes) {
        if (contentType.startsWith("image/")) return true;
        if (hasImageExtension(originalName)) return true;
        if (hasImageMagic(bytes)) return true;

        // último intento: si ImageIO la puede leer, también lo consideramos imagen
        return tryRead(bytes) != null;
    }

    private static boolean hasImageExtension(String name) {
        if (name == null) return false;
        String n = name.toLowerCase(Locale.ROOT);
        return n.endsWith(".jpg") || n.endsWith(".jpeg") || n.endsWith(".png")
                || n.endsWith(".bmp") || n.endsWith(".gif") || n.endsWith(".webp");
    }

    private static boolean hasImageMagic(byte[] b) {
        return isJpegMagic(b) || isPngMagic(b) || isWebpMagic(b) || isGifMagic(b) || isBmpMagic(b);
    }

    private static boolean isJpegMagic(byte[] b) {
        return b != null && b.length >= 3
                && (b[0] & 0xFF) == 0xFF && (b[1] & 0xFF) == 0xD8 && (b[2] & 0xFF) == 0xFF;
    }

    private static boolean isPngMagic(byte[] b) {
        return b != null && b.length >= 8
                && (b[0] & 0xFF) == 0x89 && b[1] == 0x50 && b[2] == 0x4E && b[3] == 0x47
                && b[4] == 0x0D && b[5] == 0x0A && b[6] == 0x1A && b[7] == 0x0A;
    }

    private static boolean isWebpMagic(byte[] b) {
        // "RIFF"...."WEBP"
        return b != null && b.length >= 12
                && b[0] == 'R' && b[1] == 'I' && b[2] == 'F' && b[3] == 'F'
                && b[8] == 'W' && b[9] == 'E' && b[10] == 'B' && b[11] == 'P';
    }

    private static boolean isGifMagic(byte[] b) {
        return b != null && b.length >= 6
                && b[0] == 'G' && b[1] == 'I' && b[2] == 'F' && b[3] == '8'
                && (b[4] == '7' || b[4] == '9') && b[5] == 'a';
    }

    private static boolean isBmpMagic(byte[] b) {
        return b != null && b.length >= 2 && b[0] == 'B' && b[1] == 'M';
    }

    private static BufferedImage tryRead(byte[] bytes) {
        try {
            return ImageIO.read(new ByteArrayInputStream(bytes));
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Comprime/redimensiona a JPEG manteniendo proporción
     */
    private static byte[] compressToJpeg(BufferedImage src, int maxW, int maxH, float quality) throws Exception {
        int w = src.getWidth(), h = src.getHeight();
        double scale = Math.min((double) maxW / w, (double) maxH / h);
        int newW = Math.max(1, (int) Math.round(w * scale));
        int newH = Math.max(1, (int) Math.round(h * scale));

        BufferedImage resized = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = resized.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(src, 0, 0, newW, newH, null);
        g.dispose();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageWriter writer = ImageIO.getImageWritersByFormatName("jpg").next();
        ImageWriteParam params = writer.getDefaultWriteParam();
        params.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        params.setCompressionQuality(quality);

        try (ImageOutputStream ios = ImageIO.createImageOutputStream(baos)) {
            writer.setOutput(ios);
            writer.write(null, new IIOImage(resized, null, null), params);
        } finally {
            writer.dispose();
        }
        return baos.toByteArray();
    }
}
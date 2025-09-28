package com.fmsp.srvapidataregister.modules.paciente.repository;

import com.fmsp.srvapidataregister.modules.paciente.entity.Paciente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PacienteRepository extends JpaRepository<Paciente, Long> {

    // Listar por cliente (FK numérica en Paciente)
    Page<Paciente> findAllByClienteId(Long clienteId, Pageable pageable);

    List<Paciente> findAllByClienteId(Long clienteId);

    long countByClienteId(Long clienteId);

    // ===== Listados por ALCANCE (sin filtro q) =====

    // ADMIN: por empresa
    @Query("""
            SELECT p
              FROM Paciente p
              JOIN Cliente c ON c.id = p.clienteId
             WHERE c.empresa.id = :empresaId
            """)
    Page<Paciente> findAllByEmpresa(@Param("empresaId") Long empresaId, Pageable pageable);

    // USER: por grupo
    @Query("""
            SELECT p
              FROM Paciente p
              JOIN Cliente c ON c.id = p.clienteId
             WHERE c.usuario.grupo.id = :grupoId
            """)
    Page<Paciente> findAllByGrupo(@Param("grupoId") Long grupoId, Pageable pageable);

    // ===== BÚSQUEDA por ALCANCE (con filtro q) =====
    // Ajusta los campos si cambian en tu entidad Paciente
    @Query("""
            SELECT p
              FROM Paciente p
              JOIN Cliente c ON c.id = p.clienteId
             WHERE c.empresa.id = :empresaId
               AND (
                    LOWER(p.nombre)    LIKE LOWER(CONCAT('%', :q, '%'))
                 OR LOWER(p.apellido)  LIKE LOWER(CONCAT('%', :q, '%'))
                 OR LOWER(p.documento) LIKE LOWER(CONCAT('%', :q, '%'))
                 OR LOWER(p.email)     LIKE LOWER(CONCAT('%', :q, '%'))
                 OR LOWER(p.telefono)  LIKE LOWER(CONCAT('%', :q, '%'))
                 OR LOWER(p.direccion) LIKE LOWER(CONCAT('%', :q, '%'))
               )
            """)
    Page<Paciente> searchByEmpresa(@Param("empresaId") Long empresaId,
                                   @Param("q") String q,
                                   Pageable pageable);

    @Query("""
            SELECT p
              FROM Paciente p
              JOIN Cliente c ON c.id = p.clienteId
             WHERE c.usuario.grupo.id = :grupoId
               AND (
                    LOWER(p.nombre)    LIKE LOWER(CONCAT('%', :q, '%'))
                 OR LOWER(p.apellido)  LIKE LOWER(CONCAT('%', :q, '%'))
                 OR LOWER(p.documento) LIKE LOWER(CONCAT('%', :q, '%'))
                 OR LOWER(p.email)     LIKE LOWER(CONCAT('%', :q, '%'))
                 OR LOWER(p.telefono)  LIKE LOWER(CONCAT('%', :q, '%'))
                 OR LOWER(p.direccion) LIKE LOWER(CONCAT('%', :q, '%'))
               )
            """)
    Page<Paciente> searchByGrupo(@Param("grupoId") Long grupoId,
                                 @Param("q") String q,
                                 Pageable pageable);

    Paciente findByClienteId(Long id);
    Optional<Paciente> findByDocumentoAndClienteId(String documento, Long clienteId);
}

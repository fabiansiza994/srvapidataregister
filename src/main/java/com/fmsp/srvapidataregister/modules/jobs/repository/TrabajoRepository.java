package com.fmsp.srvapidataregister.modules.jobs.repository;

import com.fmsp.srvapidataregister.modules.jobs.entity.Trabajo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TrabajoRepository extends JpaRepository<Trabajo, Long> {
    long countByCliente_Id(Long clienteId);

    long countByPaciente(Long pacienteId);

    ;

    // Listar por empresa (vía cliente -> empresa)
    @Query("""
            SELECT t FROM Trabajo t
              JOIN t.cliente c
              JOIN c.empresa e
            WHERE e.id = :empresaId
            """)
    Page<Trabajo> findAllByEmpresa(@Param("empresaId") Long empresaId, Pageable pageable);

    // Listar por grupo (vía usuario -> grupo)
    @Query("""
            SELECT t FROM Trabajo t
              JOIN t.usuario u
              JOIN u.grupo g
            WHERE g.id = :grupoId
            """)
    Page<Trabajo> findAllByGrupo(@Param("grupoId") Long grupoId, Pageable pageable);

    // Buscar por empresa
    @Query("""
            SELECT t FROM Trabajo t
              JOIN t.cliente c
              JOIN c.empresa e
              LEFT JOIN t.usuario u
              LEFT JOIN t.pacienteObj p
            WHERE e.id = :empresaId
              AND (
                LOWER(c.nombre) LIKE LOWER(CONCAT('%', :q, '%')) OR
                LOWER(c.apellido) LIKE LOWER(CONCAT('%', :q, '%')) OR
                LOWER(c.identificacion) LIKE LOWER(CONCAT('%', :q, '%')) OR
                LOWER(t.descripcionLabor) LIKE LOWER(CONCAT('%', :q, '%')) OR
                CAST(t.valorTotal AS string) LIKE CONCAT('%', :q, '%') OR
                LOWER(COALESCE(p.nombre, '')) LIKE LOWER(CONCAT('%', :q, '%')) OR
                LOWER(COALESCE(p.apellido, '')) LIKE LOWER(CONCAT('%', :q, '%')) OR
                LOWER(COALESCE(p.documento, '')) LIKE LOWER(CONCAT('%', :q, '%')) OR
                LOWER(CONCAT(COALESCE(p.nombre, ''), ' ', COALESCE(p.apellido, ''))) LIKE LOWER(CONCAT('%', :q, '%'))
              )
            """)
    Page<Trabajo> searchByEmpresa(@Param("empresaId") Long empresaId,
                                  @Param("q") String q,
                                  Pageable pageable);

    // Buscar por grupo
    @Query("""
            SELECT t FROM Trabajo t
              JOIN t.usuario u
              JOIN u.grupo g
              JOIN t.cliente c
              LEFT JOIN t.pacienteObj p
            WHERE g.id = :grupoId
              AND (
                LOWER(c.nombre) LIKE LOWER(CONCAT('%', :q, '%')) OR
                LOWER(c.apellido) LIKE LOWER(CONCAT('%', :q, '%')) OR
                LOWER(c.identificacion) LIKE LOWER(CONCAT('%', :q, '%')) OR
                LOWER(t.descripcionLabor) LIKE LOWER(CONCAT('%', :q, '%')) OR
                CAST(t.valorTotal AS string) LIKE CONCAT('%', :q, '%') OR
                LOWER(COALESCE(p.nombre, '')) LIKE LOWER(CONCAT('%', :q, '%')) OR
                        LOWER(COALESCE(p.apellido, '')) LIKE LOWER(CONCAT('%', :q, '%')) OR
                        LOWER(COALESCE(p.documento, '')) LIKE LOWER(CONCAT('%', :q, '%')) OR
                        LOWER(CONCAT(COALESCE(p.nombre, ''), ' ', COALESCE(p.apellido, ''))) LIKE LOWER(CONCAT('%', :q, '%'))
              )
            """)
    Page<Trabajo> searchByGrupo(@Param("grupoId") Long grupoId,
                                @Param("q") String q,
                                Pageable pageable);
}

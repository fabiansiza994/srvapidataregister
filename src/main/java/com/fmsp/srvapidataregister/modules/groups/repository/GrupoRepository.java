package com.fmsp.srvapidataregister.modules.groups.repository;

import com.fmsp.srvapidataregister.modules.groups.entity.Grupo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface GrupoRepository extends JpaRepository<Grupo, Long> {
    Page<Grupo> findAllByEmpresa_Id(Long empresaId, Pageable pageable);

    boolean existsByNombreIgnoreCaseAndEmpresa_Id(String nombre, Long empresaId);

    boolean existsByNombreIgnoreCaseAndEmpresa_IdAndIdNot(String nombre, Long empresaId, Long excludeId);

    @Query("""
           SELECT g FROM Grupo g
             JOIN g.empresa e
           WHERE e.id = :empresaId
             AND (
                 LOWER(g.nombre) LIKE LOWER(CONCAT('%', :q, '%'))
             )
           """)
    Page<Grupo> searchByEmpresa(@Param("empresaId") Long empresaId,
                                @Param("q") String q,
                                Pageable pageable);
}

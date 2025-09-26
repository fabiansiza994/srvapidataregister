package com.fmsp.srvapidataregister.modules.clients.repository;

import com.fmsp.srvapidataregister.modules.clients.entity.Cliente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    boolean existsByIdentificacionAndEmpresa_IdAndIdNot(String identificacion, Long empresaId, Long excludeId);
    // ✔ Clientes del mismo grupo (vía usuario.grupo)
    List<Cliente> findAllByUsuario_Grupo_Id(Long grupoId);

    // ✔ Clientes de la misma empresa (Cliente tiene empresa directa)
    List<Cliente> findAllByEmpresa_Id(Long empresaId);

    // (opcionales) versiones con paginación
    Page<Cliente> findAllByUsuario_Grupo_Id(Long grupoId, Pageable pageable);
    Page<Cliente> findAllByEmpresa_Id(Long empresaId, Pageable pageable);

    // (opcionales) consultas livianas para autorización
    @Query("select c.usuario.grupo.id from Cliente c where c.id = :clienteId")
    Optional<Long> findGrupoIdByClienteId(@Param("clienteId") Long clienteId);

    @Query("select c.usuario.id from Cliente c where c.id = :clienteId")
    Optional<Long> findCreatedByUserId(@Param("clienteId") Long clienteId);

    @Query("""
      SELECT c FROM Cliente c
      WHERE c.usuario.grupo.empresa.id = :empresaId
        AND (
          LOWER(c.nombre) LIKE LOWER(CONCAT('%', :q, '%'))
          OR LOWER(c.identificacion) LIKE LOWER(CONCAT('%', :q, '%'))
        )
    """)
    Page<Cliente> searchByEmpresa(Long empresaId, String q, Pageable pageable);

    @Query("""
      SELECT c FROM Cliente c
      WHERE c.usuario.grupo.id = :grupoId
        AND (
          LOWER(c.nombre) LIKE LOWER(CONCAT('%', :q, '%'))
          OR LOWER(c.identificacion) LIKE LOWER(CONCAT('%', :q, '%'))
        )
    """)
    Page<Cliente> searchByGrupo(Long grupoId, String q, Pageable pageable);
}
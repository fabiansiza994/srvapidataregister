package com.fmsp.srvapidataregister.modules.users.repository;

import com.fmsp.srvapidataregister.modules.users.entity.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByUsuario(String usuario);

    boolean existsByEmail(String email);

    boolean existsByUsuarioIgnoreCase(String usuario);

    // validaciones de duplicados por empresa (para update)
    boolean existsByEmailAndGrupo_Empresa_IdAndIdNot(String email, Long empresaId, Long excludeId);

    boolean existsByUsuarioIgnoreCaseAndGrupo_Empresa_IdAndIdNot(String usuario, Long empresaId, Long excludeId);

    // paginados por alcance
    Page<Usuario> findAllByGrupo_Empresa_Id(Long empresaId, Pageable pageable);

    Page<Usuario> findAllByGrupo_Id(Long grupoId, Pageable pageable);

    @Query("""
            SELECT u FROM Usuario u
              JOIN u.grupo g
              JOIN g.empresa e
            WHERE e.id = :empresaId
              AND (
                LOWER(u.nombre)   LIKE LOWER(CONCAT('%', :q, '%')) OR
                LOWER(u.apellido) LIKE LOWER(CONCAT('%', :q, '%')) OR
                LOWER(u.usuario)  LIKE LOWER(CONCAT('%', :q, '%')) OR
                LOWER(u.email)    LIKE LOWER(CONCAT('%', :q, '%'))
              )
            """)
    Page<Usuario> searchByEmpresa(@Param("empresaId") Long empresaId,
                                  @Param("q") String q,
                                  Pageable pageable);

    @Query("""
            SELECT u FROM Usuario u
              JOIN u.grupo g
            WHERE g.id = :grupoId
              AND (
                LOWER(u.nombre)   LIKE LOWER(CONCAT('%', :q, '%')) OR
                LOWER(u.apellido) LIKE LOWER(CONCAT('%', :q, '%')) OR
                LOWER(u.usuario)  LIKE LOWER(CONCAT('%', :q, '%')) OR
                LOWER(u.email)    LIKE LOWER(CONCAT('%', :q, '%'))
              )
            """)
    Page<Usuario> searchByGrupo(@Param("grupoId") Long grupoId,
                                @Param("q") String q,
                                Pageable pageable);

    @EntityGraph(attributePaths = {"grupo", "grupo.empresa", "rol"})
    @Query("SELECT u FROM Usuario u WHERE u.id = :id")
    java.util.Optional<Usuario> fetchDetail(@Param("id") Long id);

    @EntityGraph(attributePaths = {"grupo", "grupo.empresa", "rol"})
    @Query("SELECT u FROM Usuario u WHERE LOWER(u.usuario) = LOWER(:username)")
    java.util.Optional<Usuario> findByUsuarioWithRelations(@Param("username") String username);
}
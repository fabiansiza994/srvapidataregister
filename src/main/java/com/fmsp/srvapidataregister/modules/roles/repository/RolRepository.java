package com.fmsp.srvapidataregister.modules.roles.repository;

import com.fmsp.srvapidataregister.modules.roles.entity.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RolRepository extends JpaRepository<Rol, Long> {
}

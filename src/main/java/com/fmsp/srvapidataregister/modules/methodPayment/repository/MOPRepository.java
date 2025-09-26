package com.fmsp.srvapidataregister.modules.methodPayment.repository;

import com.fmsp.srvapidataregister.modules.methodPayment.entity.FormaPago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface MOPRepository extends JpaRepository<FormaPago, Long> {
    @Transactional
    @Modifying
    @Query("UPDATE FormaPago f SET f.formaPago = :name WHERE f.id = :id")
    int update(@Param("name") String name, @Param("id") Long id);

    List<FormaPago> findAllByEmpresa_Id(Long id);
}
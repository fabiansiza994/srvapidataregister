package com.fmsp.srvapidataregister.modules.codes.entity.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter @Getter
public class CodeDTO {
    private Long id;
    private String userEmail;
    private String telephone;
    private String code;
    private String status;
    private Date createDate;
}

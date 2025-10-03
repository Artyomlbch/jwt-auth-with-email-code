package com.artyom.jwtsms.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.util.Date;

@Builder
@AllArgsConstructor
@Getter
@Setter
public class ExceptionResponse {

    private int status;
    private String message;
    private String details;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date timestamp;

}

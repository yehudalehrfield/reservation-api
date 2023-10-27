package com.yl.reservation.host.exception;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties({"cause", "stackTrace","message","localizedMessage","suppressed"})
public class HostException extends RuntimeException{
    private HttpStatus status;
    private String message;
}

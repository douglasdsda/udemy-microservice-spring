package br.com.cursoudemy.productapi.config.exceptions;

import lombok.Data;

@Data
public class ExceptionDetails {
    private int status;
    private String message;
}

package com.patrimonio.api.exception;

public class RegraNegocioException extends RuntimeException{

    public RegraNegocioException(String mensagem){
        super(mensagem);
    }
}

package main.java.com.joao.empresa.exceptions;

public class EmpresaNaoEncontradaException extends RuntimeException{

    public EmpresaNaoEncontradaException(String msg){
        super(msg);
    }
}

package net.byunchov.bblog.users.exceptions;

public class InvalidEmailException extends RuntimeException{
    public InvalidEmailException(String message) {
        super(message);
    }

}

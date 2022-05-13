package dds.monedero.exceptions;

public class MaximaCantidadDepositosException extends RuntimeException {

  public MaximaCantidadDepositosException(String message) {
    super("Ya excedio los " + message + " depositos diarios");
  }

}
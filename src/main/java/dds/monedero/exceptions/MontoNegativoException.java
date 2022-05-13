package dds.monedero.exceptions;

public class MontoNegativoException extends RuntimeException {
  public MontoNegativoException(String cuanto) {
    super(cuanto + ": el monto a ingresar debe ser un valor positivo");
  }
}
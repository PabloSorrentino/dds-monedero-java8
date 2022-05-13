package dds.monedero.model;

import dds.monedero.exceptions.MaximaCantidadDepositosException;
import dds.monedero.exceptions.MaximoExtraccionDiarioException;
import dds.monedero.exceptions.MontoNegativoException;
import dds.monedero.exceptions.SaldoMenorException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Cuenta {

  private double saldo;
  private double LIMITE_EXTRACCION_DIARIO = 1000;
  private List<Movimiento> movimientos = new ArrayList<>();

  public Cuenta(double montoInicial) {
    saldo = montoInicial;
  }

  public void poner(double cuanto) {
   this.checkearCantidadDepositosPermitidos();
   this.agregarSaldo(cuanto);
   this.agregarMovimiento(LocalDate.now(), cuanto, true);
  }

  private void checkearCantidadDepositosPermitidos(){
    if (getMovimientos().stream().filter(movimiento -> movimiento.isDeposito()).count() >= 3) {
      throw new MaximaCantidadDepositosException("Ya excedio los " + 3 + " depositos diarios");
    }
  }

  private void checkearMontoPositivo(double cuanto) {
    if (cuanto <= 0) {
      throw new MontoNegativoException(cuanto + ": el monto a ingresar debe ser un valor positivo");
    }
  }

  private void agregarSaldo(double cuanto) {
    this.checkearMontoPositivo(cuanto);
    this.saldo += cuanto;
  }

  private void checkearMontoFinalDespuesDeSacar(double cuanto) {
    if (this.saldo - cuanto < 0) {
      throw new SaldoMenorException("No puede sacar mas de " + this.saldo + " $");
    }
  }

  public void sacar(double cuanto) {
    this.checkearMontoPositivo(cuanto);
    this.checkLimiteDiarioExtraccion(cuanto);
    this.quitarSaldo(cuanto);
    this.agregarMovimiento(LocalDate.now(), cuanto, false);
  }

  private void quitarSaldo(double cuanto) {
    this.checkearMontoFinalDespuesDeSacar(cuanto);
    this.saldo -= cuanto;
  }

  private void checkLimiteDiarioExtraccion(double cuanto){
    double montoExtraidoHoy = getMontoExtraidoA(LocalDate.now());
    double limite = LIMITE_EXTRACCION_DIARIO - montoExtraidoHoy;
    if (cuanto > limite) {
      throw new MaximoExtraccionDiarioException("No puede extraer mas de $ " + LIMITE_EXTRACCION_DIARIO
          + " diarios, límite: " + limite);
    }
  }

  public void agregarMovimiento(LocalDate fecha, double cuanto, boolean esDeposito) {
    Movimiento movimiento = new Movimiento(fecha, cuanto, esDeposito);
    movimientos.add(movimiento);
  }

  public double getMontoExtraidoA(LocalDate fecha) {
    return getMovimientos().stream()
        .filter(movimiento -> !movimiento.isDeposito() && movimiento.getFecha().equals(fecha))
        .mapToDouble(Movimiento::getMonto)
        .sum();
  }

  public List<Movimiento> getMovimientos() {
    return movimientos;
  }

  public double getSaldo() {
    return saldo;
  }

}

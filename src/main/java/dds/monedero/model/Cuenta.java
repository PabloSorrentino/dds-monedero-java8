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
  private double DEPOSITOS_MAXIMO_DIAROS = 3;
  private List<Movimiento> extracciones = new ArrayList<>();
  private List<Movimiento> depositos = new ArrayList<>();

  public Cuenta(double montoInicial) {
    saldo = montoInicial;
  }

  public void poner(double cuanto) {
   this.checkearCantidadDepositosPermitidos();
   this.agregarSaldo(cuanto);
   this.agregarDeposito(cuanto);
  }

  private void checkearCantidadDepositosPermitidos(){
    if (depositos.stream().filter(movimiento -> movimiento.getFecha().getDayOfYear() == LocalDate.now().getDayOfYear()).count() >= DEPOSITOS_MAXIMO_DIAROS) {
      throw new MaximaCantidadDepositosException(Double.toString(DEPOSITOS_MAXIMO_DIAROS));
    }
  }

  private void checkearMontoPositivo(double cuanto) {
    if (cuanto <= 0) {
      throw new MontoNegativoException(Double.toString(cuanto));
    }
  }

  private void agregarSaldo(double cuanto) {
    this.checkearMontoPositivo(cuanto);
    this.saldo += cuanto;
  }

  private void checkearMontoFinalDespuesDeSacar(double cuanto) {
    if (this.saldo - cuanto < 0) {
      throw new SaldoMenorException(Double.toString(saldo));
    }
  }

  public void sacar(double cuanto) {
    this.checkearMontoPositivo(cuanto);
    this.checkLimiteDiarioExtraccion(cuanto);
    this.quitarSaldo(cuanto);
    this.agregarExtraccion(cuanto);
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

  private void agregarDeposito(double cuanto) {
    Deposito deposito = new Deposito(LocalDate.now(), cuanto);
    depositos.add(deposito);
  }

  private void agregarExtraccion(double cuanto) {
    Extraccion extraccion = new Extraccion(LocalDate.now(), cuanto);
    extracciones.add(extraccion);
  }

  public double getMontoExtraidoA(LocalDate fecha) {
    return extracciones.stream()
        .filter(movimiento -> movimiento.getFecha().equals(fecha))
        .mapToDouble(Movimiento::getMonto)
        .sum();
  }

  public List<Movimiento> getDepositos() {
    return depositos;
  }

  public List<Movimiento> getExtracciones() {
    return extracciones;
  }

  public double getSaldo() {
    return saldo;
  }

}

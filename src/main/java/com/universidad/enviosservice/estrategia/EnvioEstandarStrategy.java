package com.universidad.enviosservice.estrategia;

public class EnvioEstandarStrategy implements EstrategiaEnvio {

    private static final TarifaPorTramos TARIFA = new TarifaPorTramos(0.5, 0, 0.8, 1.2);

    @Override
    public double calcular(double pesoKg, double distanciaKm) {
        double costo = TARIFA.costoBase(pesoKg, distanciaKm);
        if (distanciaKm > 500) {
            costo += 10;
        }
        return costo;
    }
}

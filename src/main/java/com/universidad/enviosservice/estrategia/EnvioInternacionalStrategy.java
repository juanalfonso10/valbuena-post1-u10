package com.universidad.enviosservice.estrategia;

public class EnvioInternacionalStrategy implements EstrategiaEnvio {

    private static final TarifaPorTramos TARIFA = new TarifaPorTramos(1.8, 50, 3.0, 4.5);

    @Override
    public double calcular(double pesoKg, double distanciaKm) {
        double costo = TARIFA.costoBase(pesoKg, distanciaKm);
        if (distanciaKm > 5000) {
            costo += 100;
        }
        return costo;
    }
}

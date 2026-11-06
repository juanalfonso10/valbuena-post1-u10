package com.universidad.enviosservice.estrategia;

public class EnvioOvernightStrategy implements EstrategiaEnvio {
    @Override
    public double calcular(double pesoKg, double distanciaKm) {
        if (distanciaKm > 300) {
            throw new IllegalArgumentException("OVERNIGHT no disponible para mas de 300 km");
        }
        if (pesoKg <= 5) {
            return distanciaKm * 2.0 + 20;
        } else if (pesoKg <= 20) {
            return distanciaKm * 2.0 + 20 + (pesoKg - 5) * 2.5;
        } else {
            return distanciaKm * 2.0 + 20 + 15 * 2.5 + (pesoKg - 20) * 3.0;
        }
    }
}

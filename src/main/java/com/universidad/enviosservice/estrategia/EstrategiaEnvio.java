package com.universidad.enviosservice.estrategia;

/**
 * Strategy: cada metodo de envio calcula su costo base con una formula propia.
 * Los ajustes transversales (fragilidad y tipo de cliente) quedan en el orquestador.
 */
public interface EstrategiaEnvio {
    double calcular(double pesoKg, double distanciaKm);
}

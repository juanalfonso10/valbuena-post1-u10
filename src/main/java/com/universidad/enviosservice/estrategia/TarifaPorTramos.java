package com.universidad.enviosservice.estrategia;

/**
 * Formula por tramos de peso compartida por todos los metodos de envio:
 * cargo por kilometro + cargo fijo, mas un cargo por kilo en el tramo 5-20 kg
 * y otro por cada kilo que exceda los 20 kg. Cada estrategia aporta sus tarifas.
 */
public record TarifaPorTramos(double porKm, double cargoFijo,
                              double porKgTramoMedio, double porKgTramoAlto) {

    private static final double LIMITE_TRAMO_BAJO_KG = 5;
    private static final double LIMITE_TRAMO_MEDIO_KG = 20;

    public double costoBase(double pesoKg, double distanciaKm) {
        double costo = distanciaKm * porKm + cargoFijo;
        if (pesoKg <= LIMITE_TRAMO_BAJO_KG) {
            return costo;
        } else if (pesoKg <= LIMITE_TRAMO_MEDIO_KG) {
            return costo + (pesoKg - LIMITE_TRAMO_BAJO_KG) * porKgTramoMedio;
        } else {
            return costo + (LIMITE_TRAMO_MEDIO_KG - LIMITE_TRAMO_BAJO_KG) * porKgTramoMedio
                + (pesoKg - LIMITE_TRAMO_MEDIO_KG) * porKgTramoAlto;
        }
    }
}

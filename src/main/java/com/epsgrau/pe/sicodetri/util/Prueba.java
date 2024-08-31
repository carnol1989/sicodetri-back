package com.epsgrau.pe.sicodetri.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Prueba {

    public static void main(String[] args) {
        // Valores de entrada
        BigDecimal depreciationFinancial = new BigDecimal("76371.39");
        BigDecimal depreciationTax = new BigDecimal("114557.086041667000000");
        BigDecimal netTaxValue = new BigDecimal("8133553.108958330000000"); // Valor neto tributario inicial

        // Arreglo para almacenar los valores netos tributarios calculados
        BigDecimal[] netTaxValues = new BigDecimal[192]; // Suponiendo 60 periodos
        netTaxValues[0] = netTaxValue;

        for (int i = 1; i < netTaxValues.length; i++) {
            // Calcular el nuevo valor neto tributario
            netTaxValues[i] = netTaxValues[i - 1].subtract(depreciationTax);

            // Redondear a dos decimales
            netTaxValues[i] = netTaxValues[i].setScale(2, RoundingMode.HALF_UP);

            // Verificar si el valor neto tributario es 0.00 y detener el bucle si es así
            //System.out.println(netTaxValues[i].abs());
            //System.out.println(BigDecimal.ZERO);
            if (netTaxValues[i].setScale(0, RoundingMode.DOWN).abs()
                    .compareTo(BigDecimal.ZERO) == 0) {
                System.out.println("El cálculo se detuvo en el periodo " + (i + 1) + " porque el valor neto tributario es 0.00");
                break;
            }

            //System.out.println(netTaxValues[i]);
        }

        // Imprimir los resultados
        for (int i = 0; i < netTaxValues.length; i++) {
            System.out.println("Periodo " + (i + 1) + ": Valor Neto Tributario = " + netTaxValues[i]);
        }
    }

}

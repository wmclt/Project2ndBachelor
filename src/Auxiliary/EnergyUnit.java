package Auxiliary;

import be.kuleuven.cs.som.annotate.*;

/**
 * @author 	Brecht J.J. Gosselé & William E.R.J. Mauclet
 * 			2BiR: wtk-cws (Gosselé) en cws-elt(Mauclet)
 * @version	3.0
 */
@Value
public enum EnergyUnit {

	JOULE("J"), WATTSECOND("Ws"), KJOULE("kJ"), SUGARCUBE("sgc"), HORSEPOWER_FORTNIGHT("hpFn");

	
	@Raw
	private EnergyUnit(String symbol) {
		this.symbol = symbol;
	}

	@Basic @Raw @Immutable
	public String getSymbol() {
		return this.symbol;
	}

	private final String symbol;

	/**
	 * Variable referencing a two-dimensional array registering
	 * conversion rates between energy units. The first level is
	 * indexed by the ordinal number of the energy unit to convert
	 * from; the ordinal number to convert to is used to index
	 * the second level.
	 */
	private static double[][] conversionRates = new double[5][5];

	public double toEnergyUnit(EnergyUnit other)
			throws IllegalArgumentException {
		if (other == null)
			throw new IllegalArgumentException("Non effective EnergyUnit!");
		if (conversionRates[this.ordinal()][other.ordinal()] == 0)
			conversionRates[this.ordinal()][other.ordinal()] = roundToClosestDouble(1 / conversionRates[other.ordinal()][this.ordinal()]);
		return conversionRates[this.ordinal()][other.ordinal()];
	}

	static {
		// Initialization of the upper part of the conversion table.
		// Other rates are computed and registered the first time
		// they are queried.
		conversionRates[JOULE.ordinal()][JOULE.ordinal()] = 1;
		conversionRates[JOULE.ordinal()][WATTSECOND.ordinal()] = 1;
		conversionRates[JOULE.ordinal()][KJOULE.ordinal()] = 0.001;
		conversionRates[JOULE.ordinal()][SUGARCUBE.ordinal()] = 1.818E-5;
		conversionRates[JOULE.ordinal()][HORSEPOWER_FORTNIGHT.ordinal()] = 5.256E-10;
		conversionRates[WATTSECOND.ordinal()][WATTSECOND.ordinal()] = 1;
		conversionRates[WATTSECOND.ordinal()][KJOULE.ordinal()] = 0.001;
		conversionRates[WATTSECOND.ordinal()][SUGARCUBE.ordinal()] = 1.818E-5;
		conversionRates[WATTSECOND.ordinal()][HORSEPOWER_FORTNIGHT.ordinal()] = 5.256E-10;
		conversionRates[KJOULE.ordinal()][KJOULE.ordinal()] = 1;
		conversionRates[KJOULE.ordinal()][SUGARCUBE.ordinal()] = 0.01818;
		conversionRates[KJOULE.ordinal()][HORSEPOWER_FORTNIGHT.ordinal()] = 5.256E-7;
		conversionRates[SUGARCUBE.ordinal()][SUGARCUBE.ordinal()] = 1;
		conversionRates[SUGARCUBE.ordinal()][HORSEPOWER_FORTNIGHT.ordinal()] = 2.891E-5;
		conversionRates[HORSEPOWER_FORTNIGHT.ordinal()][HORSEPOWER_FORTNIGHT.ordinal()] = 1;
		
	}
	
	private static double roundToClosestDouble(double value){
		double result = value*100;
		result = Math.round(result);
		return result/100;
	}
	
}
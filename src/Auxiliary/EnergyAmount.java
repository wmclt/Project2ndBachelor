package Auxiliary;

import be.kuleuven.cs.som.annotate.*;

/**
 * @invar	The amount of an instance of EnergyAmount is always positive.
 * 			|getAmount() >= 0
 * 
 * @author 	Brecht Gosselé & William Mauclet
 * 			2BiR: wtk-cws (Gosselé) en cws-elt(Mauclet)
 * @version	3.0
 */
public class EnergyAmount implements Comparable<EnergyAmount> {
	
	private EnergyUnit energyUnit;
	/**
	 * @note	The aspects related to amount have been programmed in a total way.
	 */
	private double amount;
	public final static EnergyAmount JOULE_0 = new EnergyAmount(0,EnergyUnit.JOULE);
	public final static EnergyAmount WATTS_0 = new EnergyAmount(0,EnergyUnit.WATTSECOND);

	@Raw
	public EnergyAmount(double amount, EnergyUnit EnergyUnit){
		setAmount(amount);
		setEnergyUnit(EnergyUnit);
	}
	
	@Basic @Raw
	private double getAmount() {
		return this.amount;
	}
	
	public double getAmountInSI_unit() {
		return getAmount() * getEnergyUnit().toEnergyUnit(EnergyUnit.JOULE);
	}
	
	public EnergyAmount toEnergyUnit(EnergyUnit energyUnit) throws NullPointerException{
		if(energyUnit == null)
			throw new NullPointerException("Error 404: energy unit not found!");
		double conversion = getEnergyUnit().toEnergyUnit(energyUnit);
		double convertedAmount = getAmount()*conversion;
		return new EnergyAmount(convertedAmount, energyUnit);
	}
	
	public double getAmountInSpecifiedUnit(EnergyUnit unit){
		return toEnergyUnit(unit).getAmount();
	}

	/**
	 * @note	Programmed in a total way.
	 */
	@Raw
	private void setAmount(double amount){
		this.amount = Math.min(Math.abs(amount), Double.MAX_VALUE);
	}
	
	@Raw
	private void setEnergyUnit(EnergyUnit EnergyUnit) throws IllegalArgumentException {
		if (!isValidEnergyUnit(EnergyUnit))
			throw new IllegalArgumentException();
		this.energyUnit = EnergyUnit;
	}

	@Basic @Raw
	public EnergyUnit getEnergyUnit() {
		return this.energyUnit;
	}

	public static boolean isValidEnergyUnit(EnergyUnit energyUnit) {
		return energyUnit != null;
	}
	
	public static EnergyAmount getMax(EnergyAmount first, EnergyAmount second){
		if(first.compareTo(second) >= 0)
			return first;
		else 
			return second;
	}
	
	public static EnergyAmount getMin(EnergyAmount first, EnergyAmount second) {
		if(first.compareTo(second) < 0)
			return first;
		else 
			return second;
		
	}
	
	public EnergyAmount add(EnergyAmount other) throws NullPointerException{
		if (other == null)
			throw new NullPointerException("Non-effective energy amount!");
		double sum = 0;
		if (other.getEnergyUnit() != getEnergyUnit())
			add(other.toEnergyUnit(getEnergyUnit()));
		else{
			sum = Math.min(this.getAmount() + other.getAmount(), Double.MAX_VALUE);
			}
		return new EnergyAmount(sum, getEnergyUnit());
	}
	
	@Raw
	public EnergyAmount substract(EnergyAmount other) throws NullPointerException{
		if (other == null)
			throw new NullPointerException("Non-effective energy amount!");
		double substraction = 0;
		if (other.getEnergyUnit() != getEnergyUnit())
			substract(other.toEnergyUnit(getEnergyUnit()));
		else{
			substraction = this.getAmount() - other.getAmount();
			}
		return new EnergyAmount(substraction, getEnergyUnit());
	}
	
	/**
	 * This method compareTos the value of this energy amount with another energy amount, taking into account the unit in which the other energy
	 * amount is expressed. 
	 * If this energy amount is of lower value than the other, the method returns -1. 
	 * If this energy amount is equal to the other, the method returns 0.
	 * If this energy amount is bigger than the other, the method returns 1.
	 */
	@Override
	public int compareTo(EnergyAmount other){
		assert other != null;
		if(getAmountInSI_unit() < other.getAmountInSI_unit())
			return -1;
		if(getAmountInSI_unit() == other.getAmountInSI_unit())
			return 0;
		else
			return 1;
	}
	
	
	public boolean hasSameValueAs(EnergyAmount other) {
		assert other != null;
		return this.equals(other.toEnergyUnit(this.getEnergyUnit()));
	}

	@Override
	public boolean equals(Object other) {
		if (other == null)
			return false;
		if(this.getClass() != other.getClass())
			return false;
		EnergyAmount otherAmount = (EnergyAmount) other;
		return (getAmount() == otherAmount.getAmount())
				&& (getEnergyUnit() == otherAmount.getEnergyUnit());
	}
	
	@Override
	public int hashCode() {
		return ((Double) getAmount()).hashCode() + getEnergyUnit().hashCode();
	}
	
	@Override
	public String toString() {
		return getAmount() + getEnergyUnit().getSymbol();
	}
	
	public boolean isStrictPositive() {
		return getAmount() > 0;
	}
	
	public EnergyAmount rescale(double scalar){
		double product = Math.min(getAmount() * scalar, Double.MAX_VALUE);
		return new EnergyAmount(product, getEnergyUnit());
	}
}

package Items;

import core.EnergyRelated;
import Auxiliary.EnergyAmount;
import Auxiliary.EnergyUnit;
import be.kuleuven.cs.som.annotate.Basic;
import be.kuleuven.cs.som.annotate.Immutable;
import be.kuleuven.cs.som.annotate.Model;
import be.kuleuven.cs.som.annotate.Raw;

/**
 * @author 	Brecht Gosselé & William Mauclet
 * 		   	2BiR: wtk-cws (Gosselé) en cws-elt(Mauclet)
 * @version	3.0
 */
public abstract class EnergyHolder extends Item implements EnergyRelated{

	private EnergyAmount energy;
	private static final EnergyUnit standardUnit = EnergyUnit.WATTSECOND;
	private static final EnergyAmount energyGainedWhenHit = new EnergyAmount(500, getStandardUnit());
	private EnergyAmount capacity;
	
	
	/**
	 * ...
	 * 
	 * @param 	energy
	 * 			...
	 * @param 	capacity
	 * 			...
	 * @param	weight
	 * 			...
	 * @post	...
	 * 			|new.getWeight() == Math.abs(weight)
	 * @post	...
	 * 			|new.getCapacity() == capacity
	 * @post	...
	 * 			|new.getEnergy() == energy)
	 */
	public EnergyHolder(EnergyAmount energy, EnergyAmount capacity, int weight){
		super(weight);
		setCapacity(capacity);
		setEnergy(energy);
	}
	
	/**
	 * ...
	 * 
	 * @pre		...
	 * 			|isValidCapacity(capacity)
	 * @param 	capacity
	 * 			...
	 * @post	...
	 * 			|new.getCapacity() == capacity
	 */
	private void setCapacity(EnergyAmount capacity) {
		assert isValidCapacity(capacity);
		this.capacity = capacity;		
	}

	/**
	 * ...
	 * 
	 * @param 	capacity
	 * 			...
	 * @return	...
	 * 			|result == capacity.isStrictPositive()
	 */
	private boolean isValidCapacity(EnergyAmount capacity) {
		return capacity.isStrictPositive();
	}
	
	/**
	 * ...
	 * 
	 * @param	Energy
	 * 			...
	 * @return	...
	 * 			|result == (energy.isPositive()) && (energy.compare(getCapacity()) <= 0)
	 */
	public boolean canHaveAsEnergy(EnergyAmount energy){
		return energy.compareTo(getCapacity()) <= 0;	
	}

	/**
	 * ...
	 */
	@Basic @Immutable
	public static EnergyUnit getStandardUnit() {
		return standardUnit;
	}

	
	/**
	 * ...
	 */
	@Basic
	public EnergyAmount getCapacity() {
		return capacity;
	}

	/**
	 * ...
	 */
	@Basic
	public EnergyAmount getEnergy() {
		return energy;
	}
	
	/**
	 * ...
	 *
	 * @pre		...
	 * 			|canHaveAsEnergy(energy)
	 * @param 	energy
	 * 			...
	 * @post	...
	 * 			|new.getEnergy() == energy
	 */
	@Raw  @Model 
	private void setEnergy(EnergyAmount energy) {
		assert canHaveAsEnergy(energy);
		this.energy = energy;
	}
	
	/**
	 * ...
	 * 
	 * @pre		...
	 * 			|canHaveAsEnergy(getEnergy().substract(energy))
	 * @param 	energy
	 * 			...
	 * @effect	...
	 * 			|setEnergy(getEnergy().substract(energy))
	 */
	public void consumeEnergy(EnergyAmount energy) {
		assert canHaveAsEnergy(getEnergy().substract(energy));
		setEnergy(getEnergy().substract(energy));
	}

	/**
	 * ...
	 */
	public EnergyAmount getEnergyGainWhenHit() {
		return energyGainedWhenHit;
	}

	/**
	 * ...
	 * 
	 * @effect	...
	 * 			|let 
	 * 			|	EnergyAmount newEnergy = EnergyAmount.getMin(getCapacity(), getEnergy().add(getEnergyGainWhenHit()))
	 * 			|in
	 * 			|	setEnergy(newEnergy)
	 */
	@Override
	public void hit() {
		EnergyAmount newEnergy = EnergyAmount.getMin(getCapacity(), getEnergy().add(getEnergyGainWhenHit()));
		setEnergy(newEnergy);
	}
	
}

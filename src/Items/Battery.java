package Items;

import Auxiliary.EnergyAmount;
import myPackage.Robot;

/**
 * ...
 * 
 * @invar	The capacity of each battery must be a valid capacity for a battery.
 * 			|isLegalCapacity(getCapacity())
 * @invar	The energy of each battery must be a valid energy-value for a battery given its capacity.
 * 			|isLegalEnergy(getEnergy())
 * 
 * @author 	Brecht J.J. Gosselé & William E.R.J. Mauclet
 * 			2Bir: wtk-cws (Gosselé) en cws-elt(Mauclet)
 * @version	3.0
 */
public class Battery extends EnergyHolder {
	
	private static EnergyAmount standardCapacity = new EnergyAmount(5000, getStandardUnit());
	
	/**
	 * ...
	 * 
	 * @param 	energy
	 * 			...
	 * @param	weight
	 * 			...
	 * @post	...
	 * 			|new.getWeight() == Math.abs(weight)
	 * @post	...
	 * 			|new.getEnergy() == energy
	 * @post	...
	 * 			|new.getCapacity() == getStandardCapacity()
	 */
	public Battery(EnergyAmount energy, int weight) {
		super(energy, getStandardCapacity(), weight);
	}
	
	
	/**
	 * ...
	 * 
	 * @return	...
	 * 			|let
	 * 			|	Battery clone = new Battery(getEnergy(), getWeight())
	 * 			|	clone.setPosition(getBoard(), getPosition())
	 * 			|in
	 * 			|	result == clone
	 */
	@Override
	public Battery clone() {
		Battery clone = new Battery(getEnergy(), getWeight());
		clone.setPosition(getBoard(), getPosition());
		return clone;
	}
	
	/**
     * ...
     * 
     * @param 	robot
     * 			....
     * @effect	...
     * 			|let
     * 			|	EnergyAmount transfer = EnergyAmount.getMin(robot.getCapacity().substract(robot.getEnergy()), getEnergy())
     * 			|in
     * 			|	consumeEnergy(transfer)
     * 			|	robot.charge(transfer)
     * @effect	...
     * 			|if(getEnergy().hasSameValueAs(EnergyAmount.JOULE_0))
     *  		|	robot.removeItemFromLoad(this)
     *  		|	terminate()
     */
    public void useOn(Robot robot) throws IllegalArgumentException{        
        EnergyAmount transfer = EnergyAmount.getMin(robot.getCapacity().substract(robot.getEnergy()), getEnergy());
        consumeEnergy(transfer);
        robot.charge(transfer);
        if(getEnergy().hasSameValueAs(EnergyAmount.JOULE_0)){
        	robot.dropItem(this);
        	terminate();
        }
    }
    
    /**
     * ...
     */
    public static EnergyAmount getStandardCapacity() {
    	return standardCapacity;
    }
    

}

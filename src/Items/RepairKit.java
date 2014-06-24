package Items;

import core.Robot;
import Auxiliary.EnergyAmount;
import be.kuleuven.cs.som.annotate.Model;

/**
 * ...
 * 
 * @author 	Brecht J.J. Gosselé & William E.R.J. Mauclet
 * 			2Bir: wtk-cws (Gosselé) en cws-elt(Mauclet)
 * @version	3.0
 *
 */
public class RepairKit extends EnergyHolder{
	
	private static EnergyAmount standardCapacity = new EnergyAmount(Double.MAX_VALUE, getStandardUnit());
	
	/**
	 * ...
	 * 
	 * @param 	energy
	 * 			...
	 * @param	weight
	 * 			...
	 * @effect	...
	 * 			|super(energy, getStandardCapacity(), weight)
	 */
	public RepairKit(EnergyAmount energy, int weight) {
		super(energy, getStandardCapacity(), weight);
	}
	
	/**
	 * ...
	 * 
	 * @return	...
	 * 			|let
	 * 			|	RepairKit clone = new RepairKit(getEnergy(), getWeight())
	 * 			|	clone.setPosition(getBoard(), getPosition())
	 * 			|in
	 * 			|	result == clone
	 */
	@Override
	public RepairKit clone() {
		RepairKit clone = new RepairKit(getEnergy(), getWeight());
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
     * 			|	EnergyAmount transfer = EnergyAmount.getMin(Robot.getMaxCapacity().substract(robot.getCapacity()), getEnergy().rescale(0.5))
     * 			|in
     * 			|	consumeEnergy(transfer.rescale(2))
     * 			|	robot.setCapacity(robot.getCapacity().add(transfer))
     * @effect	...
     * 			|if(getEnergy().hasSameValueAs(EnergyAmount.JOULE_0))
     * 			|	robot.removeItemFromLoad(this)
     * 			|	terminate()
     */
    @Model
    public void useOn(Robot robot) throws IllegalArgumentException{
        EnergyAmount transfer = EnergyAmount.getMin(Robot.getMaxCapacity().substract(robot.getCapacity()), getEnergy().rescale(0.5));
        consumeEnergy(transfer.rescale(2));
        robot.setCapacity(robot.getCapacity().add(transfer));
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

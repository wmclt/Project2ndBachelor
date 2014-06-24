package Conditions;

import core.Robot;
import be.kuleuven.cs.som.annotate.*;
import Auxiliary.EnergyAmount;
import Auxiliary.EnergyUnit;

/**
 * A class representing energy-at-least conditions, conditions
 * that check whether a robot has at least a certain amount of energy.
 * @author 	Brecht J.J. Gosselé & William E.R.J. Mauclet
 * 			2BiR: wtk-cws (Gosselé) en cws-elt(Mauclet)
 * @version	3.0
 *
 */
public class EnergyAtLeastCondition implements Condition {

	private EnergyAmount energy;
	
	/**
	 * Create a new energy-at-least condition is created, with the given reference energy.
	 * @pre		The given double energy is a positive value.
	 * @param	energy
	 * 			The reference energy (in Ws) of this new energy-at-least condition.
	 * @effect	The setEnergy() method is summoned for a new energy amount object of the given value in Ws.
	 */
	public EnergyAtLeastCondition(double energy){
		setEnergy(new EnergyAmount(energy, EnergyUnit.WATTSECOND));
	}
	
	/**
	 * Return the reference energy amount of this energy-at-least condition.
	 * @return	The reference energy amount of this energy-at-least condition.
	 */
	@Basic @Immutable
	private EnergyAmount getEnergy() {
		return energy;
	}
	
	/**
	 * Sets the reference energy amount to the given energy amount.
	 * @param	energy
	 * 			The new energy amount of this energy-at-least condition.
	 * @post	The reference energy amount of this energy-at-least condition equals the given energy.
	 */
	@Model
	private void setEnergy(EnergyAmount energy){
		this.energy = energy;
	}
	
	/**
	 * Evaluate the given robot's energy-level to the reference energy of this energy-at-least condition.
	 * @param	robot
	 * 			The robot on which to perform this check.
	 * @return	A boolean true if the given robot's energy level is equal to, or bigger than the reference
	 * 			energy amount of this energy-at-least condition. Returns a boolean false if not.
	 */
	@Override
	public boolean evaluate(Robot robot) {
		return robot.getEnergy().compareTo(getEnergy()) >= 0;
	}
	
	/**
	 * Returns a string representation of this energy-at-least condition.
	 * @return	A string representation of the true condition, namely "energy-at-least ", followed by the
	 * 			string representation of its reference energy amount, and the whole enclosed by brackets.
	 */
	@Override
	public String toString() {
		return "(energy-at-least " + getEnergy().toString() + ")";
	}
}

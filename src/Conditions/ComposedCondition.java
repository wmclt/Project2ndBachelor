package Conditions;

import java.util.ArrayList;

import core.Robot;

import be.kuleuven.cs.som.annotate.Basic;
import be.kuleuven.cs.som.annotate.Immutable;
import be.kuleuven.cs.som.annotate.Model;

/**
 * A class representing conditions that consist of multiple (sub)conditions that can be evaluated together.
 * @author 	Brecht Gosselé & William Mauclet
 * 			2BiR: wtk-cws (Gosselé) en cws-elt(Mauclet)
 * @version	3.0
 *
 */
public abstract class ComposedCondition implements Condition {

	protected ArrayList<Condition> subconditions;
	
	/**
	 * Create a new composed condition, with the given subconditions
	 * @param 	conditions
	 * 			The list of conditions that will be the subconditions of this new composed condition.
	 * @effect	The method setSubconditions is summoned for the given arraylist conditions.
	 */
	public ComposedCondition(ArrayList<Condition> conditions) {
		setSubconditions(conditions);
	}
	
	/**
	 * Returns the list of subconditions of this composed condition.
	 */
	@Basic @Immutable
	protected ArrayList<Condition> getSubconditions() {
		return subconditions;
	}
	
	/**
	 * Set this composed condition's subconditions to the given list of conditions.
	 * @param	conditions
	 * 			The new list of subconditions of this composed condition.
	 * @post	The subconditions of this composed condition equals the given conditions.
	 */
	@Model
	private void setSubconditions(ArrayList<Condition> conditions){
		this.subconditions = conditions;
	}
	
	/**
	 * Returns whether this combination of conditions holds for the given robot.
	 * @param	robot
	 * 			The robot on which to perform the check.
	 * @return	A boolean true if the combination of conditions holds for the given robot, a boolean false otherwise.
	 */
	@Override
	public abstract boolean evaluate(Robot robot);
	
	/**
	 * Returns a string representation of this combination of conditions.
	 * @return	A string representation of this combination of conditions.
	 */
	@Override
	public abstract String toString();

}

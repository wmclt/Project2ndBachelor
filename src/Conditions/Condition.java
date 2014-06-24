package Conditions;

import core.Robot;

/**
 * An interface of conditions.
 * @author 	Brecht J.J. Gosselé & William E.R.J. Mauclet
 * 			2BiR: wtk-cws (Gosselé) en cws-elt(Mauclet)
 * @version	3.0
 *
 */
public interface Condition {
	
	/**
	 * Returns whether this condition holds for the given robot.
	 * @param	robot
	 * 			The robot on which to perform the check.
	 * @return	A boolean true if the condition holds for the given robot, a boolean false otherwise.
	 */
	public abstract boolean evaluate(Robot robot);
	
	/**
	 * Returns a string representation of this condition.
	 * @return	A string representation of this condition.
	 */
	public abstract String toString();
}

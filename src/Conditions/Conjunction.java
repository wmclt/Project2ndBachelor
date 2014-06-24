package Conditions;

import java.util.ArrayList;

import core.Robot;

/**
 * A class of composed conditions treating their subcommands as a conjunction of conditions.
 * That is, a conjunction evaluates to true if all of its subconditions do so.
 * @author 	Brecht J.J. Gosselé & William E.R.J. Mauclet
 * 			2BiR: wtk-cws (Gosselé) en cws-elt(Mauclet)
 * @version	3.0
 *
 */
public class Conjunction extends ComposedCondition {

	/**
	 * Create a new conjunction with the given subconditions.
	 * @param 	conditions
	 * 			The list of conditions that will be the subconditions of this new conjunction.
	 * @effect	The constructor of the superclass ComposedCondition is summoned for the given arraylist conditions.
	 */
	public Conjunction (ArrayList<Condition> conditions){
		super(conditions);
	}
	
	/**
	 * Evaluates this conjunction.
	 * @param	robot
	 * 			The robot for which to evaluate the subconditions.
	 * @return	A boolean true if all of this conjunction's subconditions evaluate to true.
	 * 			A boolean false if not.
	 */
	@Override
	public boolean evaluate(Robot robot) {
		for(Condition condition: getSubconditions()){
			if(!condition.evaluate(robot))
				return false;
		}
		return true;	
	}
	
	/**
	 * Returns a string representation of this conjunction.
	 * @return	A string representation of this conjunction, namely the word "and", followed by the respective
	 * 			string representations of all the subconditions, and the whole enclosed by brackets.
	 */
	@Override
	public String toString() {
		String string = "(and";
		for(Condition condition: getSubconditions()){
			string += " " + condition.toString();
		}
		string += ")";
		return string;
	}
	
}

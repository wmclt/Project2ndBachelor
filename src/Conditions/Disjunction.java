package Conditions;

import java.util.ArrayList;

import myPackage.Robot;

/**
 * A class of composed conditions treating their subcommands as a disjunction of conditions.
 * That is, a disjunction evaluates to true if at least one of its subconditions does so.
 * @author 	Brecht J.J. Gosselé & William E.R.J. Mauclet
 * 			2BiR: wtk-cws (Gosselé) en cws-elt(Mauclet)
 * @version	3.0
 *
 */
public class Disjunction extends ComposedCondition {

	/**
	 * Create a new disjunction with the given subconditions.
	 * @param 	conditions
	 * 			The list of conditions that will be the subconditions of this new disjunction.
	 * @effect	The constructor of the superclass ComposedCondition is summoned for the given arraylist conditions.
	 */
	public Disjunction(ArrayList<Condition> conditions) {
		super(conditions);
	}
	
	/**
	 * Evaluates this disjunction.
	 * @param	robot
	 * 			The robot for which to evaluate the subconditions.
	 * @return	A boolean true if at least one of this disjunction's subconditions evaluates to true.
	 * 			A boolean false if not.
	 */
	@Override
	public boolean evaluate(Robot robot) {
		for(Condition condition: getSubconditions()){
			if(condition.evaluate(robot))
				return true;
		}
		return false;	
	}
	
	/**
	 * Returns a string representation of this disjunction.
	 * @return	A string representation of this disjunction, namely the word "or", followed by the respective
	 * 			string representations of all the subconditions, and the whole enclosed by brackets.
	 */
	@Override
	public String toString() {
		String string = "(or";
		for(Condition condition: getSubconditions()){
			string += " " + condition.toString();
		}
		string += ")";
		return string;
	}
}

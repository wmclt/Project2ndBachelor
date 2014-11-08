package Conditions;

import core.Robot;
import be.kuleuven.cs.som.annotate.*;

/**
 * A class of commands representing negations of commands. That is, a negation evaluates to true if its
 * negated command evaluates to false.
 * @author 	Brecht Gosselé & William Mauclet
 * 			2BiR: wtk-cws (Gosselé) en cws-elt(Mauclet)
 * @version	3.0
 *
 */
public class Negation implements Condition {

	private Condition negatedCondition;

	/**
	 * Create a new negation, with the given condition as negated condition.
	 * @param	condition
	 * 			The negated condition of this new negation.
	 * @effect	The setNegatedCondition() method is summoned for the given condition.
	 */
	public Negation(Condition condition){
		setNegatedCondition(condition);
	}
	
	/**
	 * Returns the negated condition of this negation.
	 * @return	The negated condition of this negation.
	 */
	@Basic @Immutable
	private Condition getNegatedCondition() {
		return negatedCondition;
	}
	
	/**
	 * Set the negated condition of this negation to the given condition.
	 * @param	condition
	 * 			The new negated condition of this negation.
	 * @post	The negated condition of this negation equals the given condition.
	 */
	@Model
	private void setNegatedCondition(Condition condition) {
		this.negatedCondition = condition;
	}
	
	/**
	 * Evaluates the given negation.
	 * @param	robot
	 * 			The robot for which to evaluate the negated condition of this negation.
	 * @return	A boolean true if the negated condition evaluates to false for the given robot,
	 * 			a boolean false is otherwise.
	 */
	@Override
	public boolean evaluate(Robot robot) {
		return !getNegatedCondition().evaluate(robot);
	}
	
	/**
	 * Returns a string representation of this negation.
	 * @return	A string representation of this negation, namely the word "not", followed by the
	 * 			string representations of the negated command, and the whole enclosed by brackets.
	 */
	@Override
	public String toString() {
		return "(not " + getNegatedCondition().toString() + ")";
	}

}

package Commands;

import Conditions.Condition;
import be.kuleuven.cs.som.annotate.Basic;
import be.kuleuven.cs.som.annotate.Immutable;
import be.kuleuven.cs.som.annotate.Model;
import myPackage.Robot;

/**
 * A class representing while-command: these consist of a condition, and a command to execute as long as the conditions holds.
 * @author 	Brecht J.J. Gosselé & William E.R.J. Mauclet
 * 			2BiR: wtk-cws (Gosselé) en cws-elt(Mauclet)
 * @version	3.0
 */
public class Iteration implements Command {

	private Condition condition;
	private Command command;

	/**
	 * A new iteration is created, with the given condition and command.
	 * @param	condition
	 * 			The condition of this new iteration.
	 * @param	command
	 * 			The command of this new iteration.
	 * @effect	The method setCondition() is summoned for the given parameter condition.
	 * @effect	The method setCommand() is summoned for the given parameter command.
	 */
	public Iteration(Condition condition, Command command) {
		setCondition(condition);
		setCommand(command);
	}
	
	/**
	 * Return the condition that need be checked before every iteration.
	 */
	@Basic @Immutable
	private Condition getCondition() {
		return condition;
	}
	
	/**
	 * @pre		The given condition is not null.
	 * @param	condition
	 * 			The new condition of this iteration.
	 * @post	This iteration's condition equals the given condition.
	 */
	@Model
	private void setCondition(Condition condition) {
		assert condition != null;
		this.condition = condition;
	}
	
	/**
	 * Return the command that need be executed before every iteration.
	 */
	@Basic @Immutable
	private Command getCommand() {
		return command;
	}
	
	/**
	 * @pre		The given command is not null.
	 * @param	command
	 * 			The new command of this iteration.
	 * @post	This iteration's command equals the given command.
	 */
	@Model
	private void setCommand(Command command) {
		assert command != null;
		this.command = command;
	}
	
	/**
	 * Executes this iteration.
	 * @effect	If and only if the condition of this iteration evaluates to true, the command is executed as long as
	 * 			the command is executed as long as the condition holds.
	 */
	@Override
	public void execute(Robot robot) {
		while(getCondition().evaluate(robot)) 
			getCommand().execute(robot);
	}
	
	/**
	 * Returns a string representation of this iteration.
	 * @return	A string representation of this iteration, consisting of the word "while", followed
	 * 			by the respective string representations of the condition and the command, and the
	 * 			whole enclosed by brackets.
	 */
	@Override
	public String toString(){
		String string = "(while ";
		string += getCondition().toString();
		string += " ";
		string += getCommand().toString();
		string += ")";
		return string;
	}
	
	/**
	 * Returns the number of basic commands in this iteration.
	 * @return	An integer, namely the result of the getNbBasicCommands()-method of this iteration's command.
	 */
	@Override
	public int getNbBasicCommands() {
		return getCommand().getNbBasicCommands();
	}
	
	/**
	 * Returns whether this command contains a BasicCommand the given robot has not yet executed, and can execute.
	 * @param	robot
	 * 			The robot for which to check if it can execute a basic command in this iteration.
	 * @param	lastStep
	 * 			An integer defining unambiguously the last basic command the robot has executed of this iteration.
	 * @return	Returns a boolean true if this iteration contains a next BasicCommand the given Robot can execute
	 * 			in view of the last step the robot has executed of this iteration, and the state of the robot itself. 
	 * 			Returns a boolean false if this is not the case.
	 */
	@Override
	public boolean containsNextStep(Robot robot, int lastStep) {
		if(lastStep == 0){
			if(getCondition().evaluate(robot))
				return(getCommand().containsNextStep(robot, lastStep));
			else
				return false;						
		}
		else if(!getCommand().containsNextStep(robot, lastStep))
				return(this.containsNextStep(robot, 0));
		else
			return true;
	}
	
	/**
	 * Returns the step number of the next BasicCommand the robot can execute in this iteration.
	 * @pre		This iteration contains a next basic command the given robot can execute.
	 * @param	robot
	 * 			The robot for which to look up the next basic command it can execute in this iteration.
	 * @param	lastStep
	 * 			An integer defining unambiguously the last basic command the robot has executed of this iteration.
	 * @return	An integer indicating the stepNb of the next executable BasicCommand in this command for the given robot.
	 * 			This will depend on the last step the robot has executed of this iteration, and on the state of the robot itself.
	 */
	@Override
	public int getStepNbOfNextStep(Robot robot, int lastStep) {
		if(lastStep == 0){
			return getCommand().getStepNbOfNextStep(robot, 0);
		}
		else if(!getCommand().containsNextStep(robot, lastStep))
			return(this.getStepNbOfNextStep(robot, 0));
		else{			
			return getCommand().getStepNbOfNextStep(robot, lastStep);
		}
	}
	
	/**
	 * Executes the BasicCommand indicated by the parameter nextStep.
	 * @param	robot
	 * 			The robot that should execute a step of this iteration.
	 * @param	nextStep
	 * 			An integer defining unambiguously which basic command the robot has to execute of this iteration.
	 * @effect	Executes the BasicCommand indicated by the parameter nextStep.
	 */
	@Override
	public void executeStep(Robot robot, int nextStep) {
		getCommand().executeStep(robot, nextStep);
	}

}

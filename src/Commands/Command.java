package Commands;

import myPackage.Robot;

/**
 * An interface of commands, an ordered structure of instructions that can be executed by a robot.
 * @author 	Brecht J.J. Gosselé & William E.R.J. Mauclet
 * 			2BiR: wtk-cws (Gosselé) en cws-elt(Mauclet)
 * @version	3.0
 */

public interface Command {
	
	/**
	 * Executes this command.
	 * @param	robot
	 * 			The robot that has to execute this command.
	 */
	public void execute(Robot robot);
	
	/**
	 * Returns a string representation of this command.
	 * @return A String representing this command.
	 */
	public String toString();
	
	/**
	 * Counts and returns the number of basic commands in this command.
	 * @return The number of basic commands in this command.
	 */
	public int getNbBasicCommands();
	
	/**
	 * Returns whether this command contains a BasicCommand the given robot has not yet executed, and can execute.
	 * @param	robot
	 * 			The robot for which to check if it can execute a basic command in this command.
	 * @param	lastStep
	 * 			An integer defining unambiguously the last basic command the robot has executed of this command.
	 * @return	Returns a boolean true if this command contains a next BasicCommand the given robot can execute
	 * 			in view of the last step the robot has executed of this command and of the state of the robot itself.
	 * 			Returns a boolean false if this is not the case.
	 */
	public boolean containsNextStep(Robot robot, int lastStep);
	
	/**
	 * Returns the step number of the next basic command the robot can execute in this command.
	 * @pre 	This command contains a next basic command the given robot can execute.
	 * @param	robot
	 * 			The robot for which to look up the next basic command it can execute in this command.
	 * @param	lastStep
	 * 			An integer defining unambiguously the last basic command the robot has executed of this command.
	 * @return	An integer indicating the stepNb of the next executable BasicCommand in this command for the given robot.
	 * 			This will depend on the last step the robot has executed of this command, and on the state of the robot itself.
	 */
	public int getStepNbOfNextStep(Robot robot, int lastStep);
	
	/**
	 * Executes the BasicCommand indicated by the parameter nextStep.
	 * @param	robot
	 * 			The robot that should execute a step of this command.
	 * @param	nextStep
	 * 			An integer defining unambiguously which basic command the robot has to execute of this command.
	 * @effect	Executes the BasicCommand indicated by the parameter nextStep.
	 */
	public void executeStep(Robot robot, int stepNb);
}


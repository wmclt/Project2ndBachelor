package Commands;

import java.util.ArrayList;

import be.kuleuven.cs.som.annotate.*;

import myPackage.Robot;

/**
 * A class representing a sequence command, i.e. a list of commands to be executed consecutively.
 * @author 	Brecht J.J. Gosselé & William E.R.J. Mauclet
 * 			2BiR: wtk-cws (Gosselé) en cws-elt(Mauclet)
 * @version	3.0
 *
 */
public class Sequence implements Command {

	private ArrayList<Command> comSeq;
	
	/**
	 * A new sequence is created, with the given command-sequence.
	 * @param	comSeq
	 * 			The command-sequence of the new sequence object.
	 * @effect	The method setComSeq() is summoned with the given parameter comSeq.
	 */
	public Sequence(ArrayList<Command> comSeq){
		setComSeq(comSeq);
	}
	
	/**
	 * Return an ArrayList of all the commands in the sequence in the right order.
	 */
	@Basic @Immutable
	private ArrayList<Command> getComSeq() {
		return comSeq;
	}
	
	/**
	 * Set the field comSeq to the given parameter comSeq.
	 * @pre		The parameter comSeq is not null.
	 * @param	comSeq
	 * 			The new command-sequence of this sequence.
	 * @post	The value of the field comSeq of this sequence equals the parameter comSeq.
	 */
	@Model
	private void setComSeq(ArrayList<Command> comSeq){
		assert comSeq != null;
		this.comSeq = comSeq;
	}
	
	/**
	 * Execute this sequence of commands.
	 * @param	robot
	 * 			The robot that has to execute this sequence.
	 * @effect	The execute-method is summoned (with the same robot as argument) for all the commands in this sequence,
	 * 			in the order they are stored in the command-sequence.
	 */
	@Override
	public void execute(Robot robot) {
		for(int i = 0; i < getComSeq().size(); i++)
			getComSeq().get(i).execute(robot);
	}

	/**
	 * Returns a string representation of this sequence.
	 * @return	A string representation of this sequence, namely the word "seq", followed by the respective string
	 * 			representations of the commands in this sequence, and the whole surrounded by brackets.
	 */
	@Override	
	public String toString() {
		String sequence = "(seq";
		for(int i = 0; i < getComSeq().size(); i++)
			sequence += " " + getComSeq().get(i).toString();
		sequence +=  ")";
		return sequence;
	}
	
	/**
	 * Returns the number of basic commands contained in this sequence.
	 * @return 	The sum of the number of basic commands in each of the commands in this sequence.
	 */
	@Override
	public int getNbBasicCommands() {
		int NbBasicCommands = 0;
		for(int i = 0; i < getComSeq().size(); i++)
			NbBasicCommands += getComSeq().get(i).getNbBasicCommands();
		return NbBasicCommands;
	}
	
	/**
	 * Returns whether this sequence contains a basic command the given robot has not yet executed, and can execute.
	 * @param	robot
	 * 			The robot for which to check if it can execute a basic command in this sequence.
	 * @param	lastStep
	 * 			An integer defining unambiguously the last basic command the robot has executed of this sequence.
	 * @return	Returns a boolean true if this sequence contains a next basic command the given robot can execute
	 * 			in view of the last step the robot has executed of this sequence, and of the state of the robot itself.
	 * 			Returns a boolean false if this is not the case.
	 */
	@Override
	public boolean containsNextStep(Robot robot, int lastStep) {
		int sumBasicCommands = 0;
		for(int i = 0; i < getComSeq().size(); i++){
			int newSumBasicCommands = sumBasicCommands + getComSeq().get(i).getNbBasicCommands();
			if((lastStep <= newSumBasicCommands) && getComSeq().get(i).containsNextStep(robot, lastStep - sumBasicCommands))
				return true;
			else{
				sumBasicCommands = newSumBasicCommands;
			}
		}
		return false;
	}
	
	/**
	 * Returns the step number of the next basic command the robot can execute in this sequence.
	 * @pre 	This sequence contains a next basic command the given robot can execute.
	 * @param	robot
	 * 			The robot for which to look up the next basic command it can execute in this sequence.
	 * @param	lastStep
	 * 			An integer defining unambiguously the last basic command the robot has executed of this sequence.
	 * @return	An integer indicating the stepNb of the next executable basic command in this command for the given robot.
	 * 			This will depend on the last step the robot has executed of this sequence, and on the state of the robot itself.
	 */
	@Override
	public int getStepNbOfNextStep(Robot robot, int lastStep) {
		int res = 0;
		int sumBasicCommands = 0;
		for(int i = 0; i < getComSeq().size(); i++){
			int newSumBasicCommands = sumBasicCommands + getComSeq().get(i).getNbBasicCommands();
			if((lastStep <= newSumBasicCommands) && getComSeq().get(i).containsNextStep(robot, lastStep - sumBasicCommands)){				
				res = sumBasicCommands + getComSeq().get(i).getStepNbOfNextStep(robot, lastStep - sumBasicCommands);
			}
			else{
				sumBasicCommands = newSumBasicCommands;
			}
		}
		return res;		
	}
	
	/**
	 * Executes the BasicCommand indicated by the parameter nextStep.
	 * @param	robot
	 * 			The robot that should execute a step of this sequence.
	 * @param	nextStep
	 * 			An integer defining unambiguously which basic command the robot has to execute of this sequence.
	 * @effect	Executes the BasicCommand indicated by the parameter nextStep.
	 */
	@Override
	public void executeStep(Robot robot, int nextStep) {
		int sumBasicCommands = 0;
		int i = 0;
		boolean commandExecuted = false;
		while (i < comSeq.size() && !commandExecuted){
			int newSum = sumBasicCommands + getComSeq().get(i).getNbBasicCommands();
			if(nextStep <= newSum){
				getComSeq().get(i).executeStep(robot, nextStep - sumBasicCommands);
				commandExecuted = true;
			}
			else {
				sumBasicCommands = newSum;
				i++;
			}
		}
	}

}

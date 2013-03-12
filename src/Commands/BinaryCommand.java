package Commands;

import Conditions.Condition;
import be.kuleuven.cs.som.annotate.Basic;
import be.kuleuven.cs.som.annotate.Immutable;
import be.kuleuven.cs.som.annotate.Model;
import myPackage.Robot;

/**
 * A class representing a binary command: these consist of a condition, a command to execute if the condition evaluates to true,
 * and a command to execute if the condition executes to false.
 * @author 	Brecht J.J. Gosselé & William E.R.J. Mauclet
 * 			2BiR: wtk-cws (Gosselé) en cws-elt(Mauclet)
 * @version	3.0
 *
 */
public class BinaryCommand implements Command {

	private Condition condition;
	private Command	todoIfTrue;
	private Command	todoIfFalse;	
	
	/**
	 * A new binary command is created, with the given condition and commands.
	 * @param	condition
	 * 			The condition of this new binary command.
	 * @param	todoIfTrue
	 * 			The todoIfTrue command of this new binary command.
	 * @param	todoIfFalse
	 * 			The todoIfFalse command of this new binary command.
	 * @effect	The method setCondition is summoned for the given parameter condition.
	 * @effect	The method setTodoIfTrue is summoned for the given parameter todoIfTrue.
	 * @effect	The method setTodoIfFalse is summoned for the given parameter todoIfFalse.
	 */
	
	public BinaryCommand(Condition condition, Command todoIfTrue, Command todoIfFalse){
		setCondition(condition);
		setTodoIfTrue(todoIfTrue);
		setTodoIfFalse(todoIfFalse);
	}
	
	/**
	 * Return the condition that defines this binary command.
	 */
	@Basic @Immutable
	private Condition getCondition() {
		return condition;
	}
	
	/**
	 * @pre		The given parameter condition is not null.
	 * @param	condition
	 * 			The new condition of this binary command.		
	 * @post	This binary command's condition equals the given condition.
	 */
	@Model
	private void setCondition(Condition condition) {
		assert condition != null;
		this.condition = condition;
	}

	/**
	 * Return the command that need be executed if testing the condition returns true.
	 */
	@Basic @Immutable
	private Command getTodoIfTrue() {
		return todoIfTrue;
	}
	
	/**
	 * @pre		The given parameter todoIfTrue is not null.
	 * @param	todoIfTrue
	 * 			The new todoIfTrue command of this binary command.
	 * @post	This binary command's todoIfTrue equals the given todoIfTrue.
	 */
	@Model
	private void setTodoIfTrue(Command todoIfTrue) {
		assert todoIfTrue != null;
		this.todoIfTrue = todoIfTrue;
	}
	

	/**
	 * Return the command that need be executed if testing the condition returns false.
	 */
	@Basic @Immutable
	private Command getTodoIfFalse() {
		return todoIfFalse;
	}

	/**
	 * @pre		The given parameter todoIfFalse is not null.
	 * @param	todoIfFalse
	 * 			The new todoIfFalse command of this binaryCommand.
	 * @post 	This binary command's todoIfFalse equals the given todoIfFalse.
	 */
	@Model
	private void setTodoIfFalse(Command todoIfFalse) {
		assert todoIfFalse != null;
		this.todoIfFalse = todoIfFalse;
	}
	
	
	/**
	 * Returns a string representation of this command
	 * @return 	Returns a string representation of this binary command, namely the word "if",
	 * 			followed by the respective string representations of the condition, todoIfTrue and todoIfFalse,
	 * 			and the whole enclosed by brackets.
	 */
	@Override
	public String toString() {
		String binaryCommand = "(if ";
		binaryCommand += getCondition().toString() +" " + getTodoIfTrue().toString() + " " + getTodoIfFalse().toString()+")";
		return binaryCommand;
	}
	
	
	/**
	 * Evaluates the condition of this binary command and then executes the correspondent command.
	 * @param	robot
	 * 			The robot that should execute this command.
	 * @effect	If the condition evaluates to true, the todoIfTrue command is executed for the given robot.
	 * @effect	If the condition evaluates to false, the getTodoIfFalse() command is executed for the given robot.
	 */
	@Override
	public void execute(Robot robot) {
		if(getCondition().evaluate(robot))
			getTodoIfTrue().execute(robot);
		else
			getTodoIfFalse().execute(robot);
	}
	
	
	/**
	 * Returns the number of BasicCommands contained in this binary command.
	 * @return	Returns the sum of the todoIfTrue's number of BasicCommands, and the todoIfFalse's.
	 */
	@Override
	public int getNbBasicCommands() {
		return getTodoIfTrue().getNbBasicCommands() + getTodoIfFalse().getNbBasicCommands();
	}	

	/**
	 * Returns whether this binary command contains a BasicCommand the given robot has not yet executed, and can execute.
	 * @param	robot
	 * 			The robot for which to check if it can execute a basic command in this binary command.
	 * @param	lastStep
	 * 			An integer defining unambiguously the last basic command the robot has executed of this binary command.
	 * @return	Returns a boolean true if this binary command contains a next BasicCommand the given robot can execute
	 * 			in view of the last step the robot has executed of this binary command and of the state of the robot itself.
	 * 			Returns a boolean false if this is not the case.
	 */
	@Override
	public boolean containsNextStep(Robot robot, int lastStep) {
		if(lastStep == 0){
			if(getCondition().evaluate(robot))
				return getTodoIfTrue().containsNextStep(robot, lastStep);
			else
				return getTodoIfFalse().containsNextStep(robot, lastStep);
		}
		else if(lastStep <= getTodoIfTrue().getNbBasicCommands())
			return getTodoIfTrue().containsNextStep(robot, lastStep);
		else
			return getTodoIfFalse().containsNextStep(robot, lastStep - getTodoIfTrue().getNbBasicCommands());
	}
	
	/**
	 * Returns the step number of the next BasicCommand the robot can execute in this binary command.
	 * @pre 	This binary command contains a next basic command the given robot can execute.
	 * @param	robot
	 * 			The robot for which to look up the next basic command it can execute in this binary command.
	 * @param	lastStep
	 * 			An integer defining unambiguously the last basic command the robot has executed of this binary command.
	 * @return	An integer indicating the step-number of the next executable BasicCommand in this command for the given robot.
	 * 			This will depend on the last step the robot has executed of this binary command, and on the state of the robot itself.
	 */
	@Override
	public int getStepNbOfNextStep(Robot robot, int lastStep) {
		if(lastStep == 0){
			if(getCondition().evaluate(robot)){
				return getTodoIfTrue().getStepNbOfNextStep(robot, 0);
			}
			else{
				return getTodoIfTrue().getNbBasicCommands() + getTodoIfFalse().getStepNbOfNextStep(robot, 0);
			}
		}
		else if(lastStep <= getTodoIfTrue().getNbBasicCommands()){
			return getTodoIfTrue().getStepNbOfNextStep(robot, lastStep);
		}
		else{
			return getTodoIfTrue().getNbBasicCommands() + getTodoIfFalse().getStepNbOfNextStep(robot, lastStep - getTodoIfTrue().getNbBasicCommands());
		}
	}

	
	/**
	 * Executes the given step of this command.
	 * @param	robot
	 * 			The robot that should execute a step of this binary command.
	 * @param	nextStep
	 * 			An integer defining unambiguously which basic command the robot has to execute of this binary command.
	 * @effect	If the parameter is smaller than or equal to the number of BasicCommands in the todoIfTrue command,
	 * 			the executeStep method of the todoIfTrue command is summoned with the initial parameter nextStep.
	 * 			Otherwise, the executeStep method of the todoIfFalse method is summoned with the initial parameter nextStep,
	 * 			diminished by the number of BasicCommands in the todoIfTrue command.
	 */
	@Override
	public void executeStep(Robot robot, int nextStep) {
		if(nextStep <= getTodoIfTrue().getNbBasicCommands())
			getTodoIfTrue().executeStep(robot, nextStep);
		else
			getTodoIfFalse().executeStep(robot, nextStep - getTodoIfTrue().getNbBasicCommands());		
	}
}

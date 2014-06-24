package Commands;

import java.util.NoSuchElementException;

import core.Robot;

import Items.Item;


/**
 * An enumeration of all the possible basic commands.
 * As for now, these are: moving, turning (both clock- and counterclockwise), shooting, and picking up and using an item.
 * @author 	Brecht J.J. Gosselé & William E.R.J. Mauclet
 * 			2BiR: wtk-cws (Gosselé) en cws-elt(Mauclet)
 * @version	3.0
 *
 */
public enum BasicCommand implements Command {
	
	
	MOVE {
		/**
		 * Move the robot one step forward.
		 * @param	robot
		 * 			The robot that has to execute this command.
		 * @effect	The given robot moves one step forward. 		
		 */
		@Override
		public void execute(Robot robot){
			try{
				if(robot.getEnergy().compareTo(robot.getEnergyToMove()) != -1)
					robot.move();
			}
			catch(NullPointerException exc){
				System.err.println(exc.getMessage());
			}
			catch (IllegalStateException exc){
				System.err.println(exc.getMessage());
			}
			catch (IllegalArgumentException exc){
				System.err.println(exc.getMessage());
			}
		}
		
		/**
		 * Returns a string representation of this command.
		 * @return The string representation of this command, namely "(move)"
		 */
		@Override
		public String toString() {
			return "(move)";
		}
		
	},
	
	PICKUP_AND_USE {
		/**
		 * Lets the given robot pick up and use an item.
		 * @param	robot
		 * 			The robot that has to execute this command.
		 * @effect	The given robot picks up and use a random item on its current position,
		 * 			provided there is an item on the same position.
		 */
		@Override
		public void execute(Robot robot) throws NullPointerException, IllegalArgumentException, IllegalStateException{
			try{
				Item item = robot.getBoard().getRandomItemOnPosition(robot.getPosition());
				if(item != null)
					robot.pickUp(item);
					robot.use(item);
			}
			catch(NullPointerException exc){
				assert false;
			}
			catch(IllegalArgumentException exc){
				System.err.println(exc.getMessage());
			}
			catch(IllegalStateException exc){
				assert false;
			}
			catch(NoSuchElementException exc){
				System.err.println(exc.getMessage());
			}
		}
		
		/**
		 * Returns a string representation of this command
		 * @return The string representation of this command, namely "(pickup-and-use)".
		 */
		@Override
		public String toString() {
			return "(pickup-and-use)";
		}
	},
	
	SHOOT {
		/**
		 * The given robot shoots in the direction it is facing.
		 * @param	robot
		 * 			The robot that has to execute this command.
		 * @effect	The given robot shoots in the direction it is facing, provided it has the energy to do so.
		 */
		@Override
		public void execute(Robot robot){
			try{
				if(robot.getEnergy().compareTo(Robot.getEnergyToShoot()) != -1)
					robot.shoot();
			}
			catch(NullPointerException exc){
				System.err.println(exc.getMessage());
			}
		}
		
		/**
		 * Returns a string representation of this command
		 * @return	The string representation of this command, namely "(shoot)".
		 */
		@Override
		public String toString() {
			return "(shoot)";
		}
	},
	
	TURN_CLOCKWISE {
		/**
		 * The given robot turns in clockwise direction.
		 * @param	robot
		 * 			The robot that has to execute this command.
		 * @effect	The given robot turns in clockwise direction, provided it has the energy to do so.
		 */
		@Override
		public void execute(Robot robot){
			if(robot.getEnergy().compareTo(Robot.getEnergyToTurn()) != -1 )
				robot.turnClockwise();
		}
		
		/**
		 * Returns a string representation of this command
		 * @return The string representation of this command, namely "(turn clockwise)".
		 */
		@Override
		public String toString() {
			return "(turn clockwise)";
		}
	},
	
	TURN_COUNTERCLOCKWISE {
		/**
		 * The given robot turns in counterclockwise direction
		 * @param	robot
		 * 			The robot that has to execute this command.
		 * @effect	The given robot turns in counterclockwise direction, provided it has the energy to do so.
		 */
		@Override
		public void execute(Robot robot){
			if(robot.getEnergy().compareTo(Robot.getEnergyToTurn()) != -1)
				robot.turnCounterclockwise();
		}
		
		/**
		 * Returns a string representation of this command
		 * @return 	The string representation of this command, namely "(turn counterclockwise)".
		 */
		@Override
		public String toString() {
			return "(turn counterclockwise)";
			}
		
	};
	
	/**
	 * Returns the number of BasicCommands this command consists of.
	 * @return	The number of BasicCommands this command consists of, namely 1.
	 */
	@Override
	public int getNbBasicCommands(){
		return 1;
	}
	
	
	/**
	 * Returns whether this command contains a BasicCommand the given robot has not yet executed, and can execute.
	 * @return	A boolean indicating whether this BasicCommand contains a BasicCommand the given robot has not yet executed.
	 * 			This will always be true if the robot has not yet executed this basic command, thus if and only if the parameter lastStep equals 0.
	 */
	@Override
	public boolean containsNextStep(Robot robot, int lastStep){
		return lastStep == 0;
	}
	
	
	/**
	 * Returns the step number of the next BasicCommand the robot can execute in this command.
	 * @pre 	This basic command contains a next basic command the given robot can execute (i.e. the robot has not yet executed this command).
	 * @return	An integer indicating the stepNb of the next executable BasicCommand in this command for the given robot.
	 * 			This will always be the first step of this basic command, thus the returned integer always equals 1.
	 */
	@Override
	public int getStepNbOfNextStep(Robot robot, int lastStep){
		return 1;
	}
	
	
	/**
	 * Executes the given step of this command
	 * @effect	Executes this command.
	 */
	@Override 
	public void executeStep(Robot robot, int stepNb){
		this.execute(robot);
	}
		
}

package Conditions;

import Auxiliary.Position;
import myPackage.Robot;
import Auxiliary.Orientation;
import Items.Item;
import myPackage.Wall;

/**
 * An enumeration of all the basic conditions that can be used in programs.
 * As for now, these are: (the trivial conditions) true and false, wall (to check whether there is a wall right of a robot),
 * at-item (to check whether there is an item on the same position as a robot), and can-hit-robot.
 * @author 	Brecht J.J. Gosselé & William E.R.J. Mauclet
 * 			2BiR: wtk-cws (Gosselé) en cws-elt(Mauclet)
 * @version	3.0
 *
 */
public enum BasicCondition implements Condition {
	
	TRUE {
		/**
		 * Returns whether the true condition for the given robot (trivial case).
		 * @param	robot
		 * 			The robot on which to perform the check.
		 * @return	The boolean value true.
		 */
		@Override
		public boolean evaluate(Robot robot) {
			return true;
		}
		
		/**
		 * Returns a string representation of the true condition.
		 * @return	A string representation of the true condition, namely "(true)"
		 */
		@Override
		public String toString() {
			return "(true)";
		}
	},
	
	FALSE {
		/**
		 * Returns whether the false condition holds for the given robot (trivial case).
		 * @param	robot
		 * 			The robot on which to perform the check.
		 * @return	The boolean value false.
		 */
		@Override
		public boolean evaluate(Robot robot) {
			return false;
		}
		
		/**
		 * Returns a string representation of the false condition.
		 * @return	A string representation of the true condition, namely "(false)"
		 */
		@Override
		public String toString() {
			return "(false)";
		}
	},

	WALL {
		/**
		 * Returns whether there is a wall to the right of the given robot.
		 * @param	robot
		 * 			The robot on which to perform the check.
		 * @return	A boolean true if there is a wall to the right of the given robot, a boolean false if not.
		 */
		@Override
		public boolean evaluate(Robot robot) {
			if(robot.getBoard() == null)
				return false;
			Position starBoard = robot.getSquareInDirection(1,Orientation.turnRight(robot.getOrientation()));
			if(starBoard == null)
				return false;
			return robot.getBoard().containsEntityOfSpecifiedKindOnPosition(starBoard, Wall.class);
		}
		
		/**
		 * Returns a string representation of the wall condition.
		 * @return	A string representation of the true condition, namely "(wall)"
		 */
		@Override
		public String toString() {
			return "(wall)";
		}
		
		
	},
	
	AT_ITEM {
		/**
		 * Returns whether there is an item on the same position as the robot.
		 * @param	robot
		 * 			The robot on which to perform the check.
		 * @return	A boolean true if there is an item on the same position as the given robot, a boolean false if not.
		 */
		@Override
		public boolean evaluate(Robot robot){
			try{
				return robot.getBoard().containsEntityOfSpecifiedKindOnPosition(robot.getPosition(), Item.class);
			}
			
			catch(NullPointerException exc) {
				return false;
			}
		}
		
		/**
		 * Returns a string representation of the at-item condition.
		 * @return	A string representation of the true condition, namely "(at-item)"
		 */
		@Override
		public String toString() {
			return "(at-item)";
		}
	},
	
	CAN_HIT_ROBOT {
		/**
		 * Returns whether there is another robot standing in front of the given robot, which the given robot can hit by shooting.
		 * @param	robot
		 * 			The robot on which to perform the check.
		 * @return	A boolean true if there is another robot standing in front of the given robot, a boolean false if not.
		 */
		@Override
		public boolean evaluate(Robot robot) {
			if(robot.getBoard() == null)
				return false;
			try {
				return (robot.getBoard().containsEntityOfSpecifiedKindOnPosition(robot.getBoard().returnFirstOccupiedPositionInDirection(robot), Robot.class));
			}
			catch(NullPointerException exc) {return false;}
		}
		
		/**
		 * Returns a string representation of the can-hit-robot condition.
		 * @return	A string representation of the true condition, namely "(can-hit-robot)"
		 */
		@Override
		public String toString() {
			return "(can-hit-robot)";
		}
	};	
}

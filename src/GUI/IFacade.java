package GUI;

import java.io.Writer;
import java.util.Set;

/**
 * Implement this interface to connect your code to the user interface.
 * 
 * <ul>
 * <li>Connect your classes to the user interface by creating a class named <code>Facade</code> that implements <code>IFacade</code>. The header
 *     of the class <code>Facade</code> should look as follows:
 *     <p><code>class Facade implements IFacade&ltBoardImpl, RobotImpl, WallImpl, BatteryImpl, RepairKitImpl, SurpriseBoxImpl&gt { ... }</code></p>
 *     The code snippet shown above assumes that your classes representing boards, robots, walls and batteries are respectively named
 *     <code>BoardImpl</code>, <code>RobotImpl</code>, <code>WallImpl</code>, <code>BatteryImpl</code>, <code>RepairKitImpl</code> and <code>SurpriseBoxImpl</code>. Consult the
 *     <a href="http://docs.oracle.com/javase/tutorial/java/IandI/createinterface.html">Java tutorial</a> for more information on interfaces.</li>
 * <li>Modify the code between <code>&ltbegin&gt</code> and <code>&ltend&gt</code> in RoboRally.java: instantiate the generic arguments with
 *     your own classes and replace <code>new roborally.model.Facade()</code> with <code>new yourpackage.Facade()</code>.
 * <li>You may assume that only non-null objects returned by <code>createBoard</code>, <code>createRobot</code>, <code>createWall</code> and <code>createBattery</code>
 *     are passed to <code>putRobot</code>, <code>getBatteryX</code>, <code>getWallY</code>, <code>move</code>, etc.</li>
 * <li>The methods in this interface should not throw exceptions (unless specified otherwise in the documentation of a method). Prevent precondition violations for nominal methods (by checking before calling a method that its precondition holds)
 *   and catch exceptions for defensive methods. If a problem occurs (e.g. insufficient energy to move, trying to use a battery not held by the robot, ...), do not modify the program state and print an error message on standard error (<code>System.err</code>).</li>
 * <li>The rules described above and the documentation described below for each method apply only to the class implementing IFacade. Your classes for representing boards, robots, walls and batteries should follow the rules described in the assignment.</li>
 * <li>Do not modify the signatures of the methods defined in this interface. You can however add additional methods, as long as these additional methods do not overload the existing ones. Each additional method should of course
 *     be implemented in your class <code>Facade</code>.</li>
 * </ul> 
 */
public interface IFacade<Board, Robot, Wall, Battery, RepairKit, SurpriseBox> {
	
	/**
	 * Create a new board with the given <code>width</code> and <code>height</code>. 
	 * 
	 * This method must return <code>null</code> if the given <code>width</code> and <code>height</code> are invalid. 
	 */
	public Board createBoard(long width, long height);
	
	/**
	 * Merge <code>board1</code> and <code>board2</code>. 
	 */
	public void merge(Board board1, Board board2);
	
	/**
	 * Create a new battery with initial energy equal to <code>initialEnergy</code> and weight equal to <code>weight</code>. 
	 * 
	 * This method must return <code>null</code> if the given parameters are invalid (e.g. negative weight). 
	 */
	public Battery createBattery(double initialEnergy, int weight);
	
	/**
	 * Put <code>battery</code> at position (<code>x</code>, <code>y</code>) on <code>board</code> (if possible).
	 */
	public void putBattery(Board board, long x, long y, Battery battery);
	
	/**
	 * Return the x-coordinate of <code>battery</code>.
	 * 
	 * This method must throw <code>IllegalStateException</code> if <code>battery</code> is not placed on a board.
	 */
	public long getBatteryX(Battery battery) throws IllegalStateException;
	
	/**
	 * Return the y-coordinate of <code>battery</code>.
	 * 
	 * This method must throw <code>IllegalStateException</code> if <code>battery</code> is not placed on a board.
	 */
	public long getBatteryY(Battery battery) throws IllegalStateException;
	
	/**
	 * Create a new repair kit that repairs <code>repairAmount</code>. 
	 * 
	 * This method must return <code>null</code> if the given parameters are invalid (e.g. negative <code>repairAmount</code>). 
	 */
	public RepairKit createRepairKit(double repairAmount, int weight);
	
	/**
	 * Put <code>repairKit</code> at position (<code>x</code>, <code>y</code>) on <code>board</code> (if possible).
	 */
	public void putRepairKit(Board board, long x, long y, RepairKit repairKit);
	
	/**
	 * Return the x-coordinate of <code>repairKit</code>.
	 * 
	 * This method must throw <code>IllegalStateException</code> if <code>repairKit</code> is not placed on a board.
	 */
	public long getRepairKitX(RepairKit repairKit) throws IllegalStateException;
	
	/**
	 * Return the y-coordinate of <code>repairKit</code>.
	 * 
	 * This method must throw <code>IllegalStateException</code> if <code>repairKit</code> is not placed on a board.
	 */
	public long getRepairKitY(RepairKit repairKit) throws IllegalStateException;
	
	/**
	 * Create a new surprise box with weighing <code>weight</code>. 
	 * 
	 * This method must return <code>null</code> if the given parameters are invalid (e.g. negative <code>weight</code>). 
	 */
	public SurpriseBox createSurpriseBox(int weight);
	
	/**
	 * Put <code>surpriseBox</code> at position (<code>x</code>, <code>y</code>) on <code>board</code> (if possible).
	 */
	public void putSurpriseBox(Board board, long x, long y, SurpriseBox surpriseBox);
	
	/**
	 * Return the x-coordinate of <code>surpriseBox</code>.
	 * 
	 * This method must throw <code>IllegalStateException</code> if <code>surpriseBox</code> is not placed on a board.
	 */
	public long getSurpriseBoxX(SurpriseBox surpriseBox) throws IllegalStateException;
	
	/**
	 * Return the y-coordinate of <code>surpriseBox</code>.
	 * 
	 * This method must throw <code>IllegalStateException</code> if <code>surpriseBox</code> is not placed on a board.
	 */
	public long getSurpriseBoxY(SurpriseBox surpriseBox) throws IllegalStateException;
	
	/** 
	 * Create a new Robot looking at <code>orientation</code> with <code>energy</code> watt-second.
	 * 
	 * This method must return <code>null</code> if the given parameters are invalid (e.g. negative energy). 
	 *  
	 * <p>0, 1, 2, 3 respectively represent up, right, down and left.</p>
	 */
	public Robot createRobot(int orientation, double initialEnergy);
	
	/**
	 * Put <code>robot</code> at position (<code>x</code>, <code>y</code>) on <code>board</code> (if possible).
	 */
	public void putRobot(Board board, long x, long y, Robot robot);
	
	/**
	 * Return the x-coordinate of <code>robot</code>.
	 * 
	 * This method must throw <code>IllegalStateException</code> if <code>robot</code> is not placed on a board.
	 */
	public long getRobotX(Robot robot) throws IllegalStateException;
	
	/**
	 * Return the y-coordinate of <code>robot</code>.
	 * 
	 * This method must throw <code>IllegalStateException</code> if <code>robot</code> is not placed on a board.
	 */
	public long getRobotY(Robot robot) throws IllegalStateException;
	
	/**
	 * Return the orientation (either 0, 1, 2 or 3) of <code>robot</code>. 
	 * 
	 * <p>0, 1, 2, 3 respectively represent up, right, down and left.</p>
	 */
	public int getOrientation(Robot robot);
	
	/**
	 * Return the current energy in watt-second of <code>robot</code>.
	 */
	public double getEnergy(Robot robot);
	
	/**
	 * Move <code>robot</code> one step in its current direction if the robot has sufficient energy. Do not modify the state of the robot
	 * if it has insufficient energy.
	 */
	public void move(Robot robot);
	
	/**
	 * Turn <code>robot</code> 90 degrees in clockwise direction if the robot has sufficient energy. Do not modify the state of the robot
	 * if it has insufficient energy.
	 */
	public void turn(Robot robot);
	
	/**
	 * Make <code>robot</code> pick up <code>battery</code> (if possible).
	 */
	public void pickUpBattery(Robot robot, Battery battery);
	
	/**
	 * Make <code>robot</code> use <code>battery</code> (if possible).
	 */
	public void useBattery(Robot robot, Battery battery);
	
	/**
	 * Make <code>robot</code> drop <code>battery</code> (if possible).
	 */
	public void dropBattery(Robot robot, Battery battery);
	
	/**
	 * Make <code>robot</code> pick up <code>repairKit</code> (if possible).
	 */
	public void pickUpRepairKit(Robot robot, RepairKit repairKit);
	
	/**
	 * Make <code>robot</code> use <code>repairKit</code> (if possible).
	 */
	public void useRepairKit(Robot robot, RepairKit repairKit);
	
	/**
	 * Make <code>robot</code> drop <code>repairKit</code> (if possible).
	 */
	public void dropRepairKit(Robot robot, RepairKit repairKit);
	
	/**
	 * Make <code>robot</code> pick up <code>surpriseBox</code> (if possible).
	 */
	public void pickUpSurpriseBox(Robot robot, SurpriseBox surpriseBox);
	
	/**
	 * Make <code>robot</code> use <code>surpriseBox</code> (if possible).
	 */
	public void useSurpriseBox(Robot robot, SurpriseBox surpriseBox);
	
	/**
	 * Make <code>robot</code> drop <code>surpriseBox</code> (if possible).
	 */
	public void dropSurpriseBox(Robot robot, SurpriseBox surpriseBox);
	
	/**
	 * Transfer all items possessed by <code>from</code> to <code>to</code>.  
	 */
	public void transferItems(Robot from, Robot to);	
	
	/**
	 * Return whether your implementation of <code>isMinimalCostToReach</code> takes into account other robots, walls and turning (required to score 17+). The return
	 * value of this method determines the expected return value of <code>isMinimalCostToReach</code> in the test suite.
	 * 
	 * This method must return either 0 or 1.
	 */
	public int isMinimalCostToReach17Plus(); 
	
	/**
	 * Return the minimal amount of energy required for <code>robot</code> to reach (<code>x</code>, </code>y</code>) taking into account the robot's current load and energy level. Do not take into account
	 * shooting and picking up/using/dropping batteries. 
	 * <p>
	 * The expected return value of this method depends on <code>isMinimalCostToReach17Plus</code>:
	 * <ul>
	 * <li>If <code>isMinimalCostToReach17Plus</code> returns <code>0</code>, then <code>getMinimalCostToReach</code> will only be called if there are no obstacles in the rectangle
	 * covering <code>robot</code> and the given position. Moreover, the result of this method should not include the energy required for turning.</li>
	 * <li>If <code>isMinimalCostToReach17Plus</code> returns <code>1</code>, then <code>getMinimalCostToReach</code> must take into account obstacles (i.e. walls, other robots) and the 
	 * fact that turning consumes energy. This method must return <code>-1</code> if the given position is not reachable given the current amount of energy.</li>
	 * </ul>
	 * </p>
	 * In any case, this method must return <code>-1</code> if <code>robot</code> is not placed on a board.
	 */
	public double getMinimalCostToReach(Robot robot, long x, long y);
	
	/**
	 * Return whether your implementation of <code>moveNextTo</code> takes into account other robots, walls and the fact that turning consumes energy (required to score 18+). The return
	 * value of this method determines the expected effect of <code>moveNextTo</code> in the test suite.
	 * 
	 * This method must return either 0 or 1.
	 */
	public int isMoveNextTo18Plus(); 
	
	/**
	 * Move <code>robot</code> as close as possible (expressed as the manhattan distance) to <code>other</code> given their current energy and load. If multiple optimal (in distance) solutions
	 * exist, select the solution that requires the least amount of total energy. Both robots can move and turn to end up closer to each other. Do not take into account shooting and picking up/using/dropping
	 * batteries.  
	 * <p>
	 * The expected return value of this method depends on <code>isMoveNextTo18Plus</code>:
	 * <ul>
	 * <li>If <code>isMoveNextTo18Plus</code> returns <code>0</code>, then <code>moveNextTo</code> will only be called if there are no obstacles in the rectangle
	 * covering <code>robot</code> and <code>other</code>. Moreover, your implementation must be optimal only in the number of moves (i.e. ignore the fact that turning consumes energy).</li>
	 * <li>If <code>isMoveNextTo18Plus</code> returns <code>1</code>, then <code>moveNextTo</code> must take into account obstacles (i.e. walls, other robots) and the 
	 * fact that turning consumes energy.</li>
	 * </ul>
	 * </p>
	 * Do not change the state if <code>robot</code> and <code>other</code> are not located on the same board.
	 */
	public void moveNextTo(Robot robot, Robot other);
	
	/**
	 * Make <code>robot</code> shoot in the orientation it is currently facing (if possible).
	 * 
	 * Students working on their own are allowed to throw <code>UnsupportedOperationException</code>.
	 */
	public void shoot(Robot robot) throws UnsupportedOperationException;
	
	/**
	 * Create a new wall.
	 * 
	 * Students working on their own are allowed to throw <code>UnsupportedOperationException</code>.
	 */
	public Wall createWall() throws UnsupportedOperationException;
	
	/**
	 * Put <code>robot</code> at position (<code>x</code>, <code>y</code>) on <code>board</code> (if possible).
	 * 
	 * Students working on their own are allowed to throw <code>UnsupportedOperationException</code>.
	 */
	public void putWall(Board board, long x, long y, Wall wall) throws UnsupportedOperationException;
	
	/**
	 * Return the x-coordinate of <code>wall</code>.
	 * 
	 * This method must throw <code>IllegalStateException</code> if <code>wall</code> is not placed on a board.
	 * 
	 * Students working on their own are allowed to throw <code>UnsupportedOperationException</code>.
	 */
	public long getWallX(Wall wall) throws IllegalStateException, UnsupportedOperationException;
	
	/**
	 * Return the y-coordinate of <code>wall</code>.
	 * 
	 * This method must throw <code>IllegalStateException</code> if <code>wall</code> is not placed on a board.
	 * 
	 * Students working on their own are allowed to throw <code>UnsupportedOperationException</code>.
	 */
	public long getWallY(Wall wall) throws IllegalStateException, UnsupportedOperationException;
	
	/**
	 * Return a set containing all robots on <code>board</code>.
	 */
	public Set<Robot> getRobots(Board board);

	/**
	 * Return a set containing all walls on <code>board</code>.
	 * 
	 * Students working on their own are allowed to throw <code>UnsupportedOperationException</code>.
	 */
	public Set<Wall> getWalls(Board board) throws UnsupportedOperationException;
	
	/**
	 * Return a set containing all repair kits on <code>board</code>.
	 */
	public Set<RepairKit> getRepairKits(Board board);
	
	/**
	 * Return a set containing all surprise boxes on <code>board</code>.
	 */
	public Set<SurpriseBox> getSurpriseBoxes(Board board);
	
	/**
	 * Return a set containing all batteries on <code>board</code>.
	 */
	public Set<Battery> getBatteries(Board board);
	
	/**
	 * Load the program stored at <code>path</code> and assign it to <code>robot</code>.
	 * 
	 * Return <code>0</code> if the operation completed successfully; otherwise, return a negative number.
	 */
	public int loadProgramFromFile(Robot robot, String path);
	
	/**
	 * Save the program of <code>robot</code> in a file at <code>path</code>.
	 * 
	 * Return <code>0</code> if the operation completed successfully; otherwise, return a negative number.
	 */
	public int saveProgramToFile(Robot robot, String path);
	
	/**
	 * Pretty print the program of <code>robot</code> via <code>writer</code>.
	 */
	public void prettyPrintProgram(Robot robot, Writer writer);
	
	/**
	 * Execute <code>n</code> basic steps in the program of <code>robot</code>.
	 * 
	 * <p>For example, consider the program (seq (move) (shoot)). The first step performs a move command,
	 * the second step performs a shoot command and all subsequent steps have no effect.</p> 
	 * 
	 * <p>Note that if n equals 1, then only the move command is executed. The next call to stepn then starts
	 * with the shoot command.</p>
	 */
	public void stepn(Robot robot, int n);	
}

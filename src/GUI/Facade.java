package GUI;

import Auxiliary.*;
import Items.*;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Set;
import java.util.HashSet;

import core.*;



/**
 * @note 	Opgemerkte fouten :
 * 			- in IFacade : in vorige versie moest getMinimalCostToReach() (double) -2 returene als de positie niet bereikbaar was door gebrek aan energie van de 
 * 			  robot en (double) -1 als de positie niet bereikbaar was omwille van obstacles. De implementatie van deze Facade gooit -1 in beide gevallen. RoboRally houdt 
 * 			  nog altijd rekening met zowel -1 als -2, echter.
 * 		    - in RoboRally : lijnen 412 en 414 stond er words[2], wat een ArrayOutOfBoundsException opgooiden omwille van het feit dat er maar 2 Objecten waren in words[]
 */

public class Facade implements IFacade<Board, Robot, Wall, Battery, RepairKit, SurpriseBox> {

	@Override
	public Board createBoard(long width, long height) {
		try{
			Board board = new Board(width, height);
			return board;
		}
		catch(IllegalArgumentException err) {
			System.err.println(err.getMessage());
			return null;
		}
	}

	@Override
	public void merge(Board board1, Board board2) {
		try {
			//board2 is terminated after merge().
			merge(board1,board2);
		}
		catch(NullPointerException exc) {System.err.println(exc.getMessage());}
		catch(IllegalStateException exc) {System.err.println(exc.getMessage());}
	}

	/**
	 * @note	Since all aspects related to the weight of an item must be worked out in a total way, negative weight-inputs are accepted 
	 * 			as given parameters. (contrary to what's stated in the documentation of IFacade, see assignment RoboRally (part 3) & documentation IFacade)
	 */
	@Override
	public Battery createBattery(double initialEnergy, int weight) {
		EnergyAmount initialEnergyAmount = new EnergyAmount(initialEnergy, EnergyUnit.WATTSECOND);
		if((initialEnergy < 0) || (initialEnergyAmount.compareTo(Battery.getStandardCapacity())  == 1))
			{System.err.println("Invalid initial energy given!");
			return null; 
			}
		Battery battery = new Battery(initialEnergyAmount, weight);
			return battery; 
	}

	@Override
	public void putBattery(Board board, long x, long y, Battery battery) {
		assert (board != null) ;
		try{
			board.putEntity(Position.returnUniquePosition(x, y),battery);
		}
		catch(IllegalStateException exc) {System.err.println(exc.getMessage());}
		catch(IllegalArgumentException exc) {System.err.println(exc.getMessage());}
		catch(NullPointerException exc) {System.err.println(exc.getMessage()); }
	}	
	
	@Override
	public long getBatteryX(Battery battery) throws IllegalStateException {
		if(battery.getPosition() == null)
			throw new IllegalStateException("Battery is not placed on a board.");
		return battery.getPosition().getX();
	}

	@Override
	public long getBatteryY(Battery battery) throws IllegalStateException {
		if(battery.getPosition() == null)
			throw new IllegalStateException("Battery is not placed on a board.");
		return battery.getPosition().getY();
	}
	
	@Override
	public RepairKit createRepairKit(double repairAmount, int weight) {
		EnergyAmount initialEnergyAmount = new EnergyAmount(repairAmount, EnergyUnit.WATTSECOND);
		if(repairAmount < 0)
			{System.err.println("Invalid repair amount given!");
			return null;
		}
		RepairKit repairKit = new RepairKit(initialEnergyAmount, weight);
			return repairKit; 
	}
	
	@Override
	public void putRepairKit(Board board, long x, long y, RepairKit repairKit){
		assert (board != null) ;
		try{
			board.putEntity(Position.returnUniquePosition(x, y),repairKit);
		}
		catch(IllegalStateException exc) {System.err.println(exc.getMessage());}
		catch(IllegalArgumentException exc) {System.err.println(exc.getMessage());}
		catch(NullPointerException exc) {System.err.println(exc.getMessage()); }
	}
	
	@Override
	public long getRepairKitX(RepairKit repairKit) throws IllegalStateException{
		if(repairKit.getPosition() == null)
			throw new IllegalStateException("Repair kit is not placed on a board.");
		return repairKit.getPosition().getX();
	}
	
	@Override
	public long getRepairKitY(RepairKit repairKit) throws IllegalStateException{
		if(repairKit.getPosition() == null)
			throw new IllegalStateException("Repair kit is not placed on a board.");
		return repairKit.getPosition().getY();
	}
	
	/**
	 * @note	Since all aspects related to the weight of an item must be worked out in a total way, negative weight-inputs are accepted 
	 * 			as given parameters. (contrary to what's stated in the documentation of IFacade, see assignment RoboRally (part 3) & documentation IFacade)
	 */
	@Override
	public SurpriseBox createSurpriseBox(int weight){
		return new SurpriseBox(weight);
	}
	
	@Override
	public void putSurpriseBox(Board board, long x, long y, SurpriseBox surpriseBox){
		assert (board != null) ;
		try{
			board.putEntity(Position.returnUniquePosition(x, y),surpriseBox);
		}
		catch(IllegalStateException exc) {System.err.println(exc.getMessage());}
		catch(IllegalArgumentException exc) {System.err.println(exc.getMessage());}
		catch(NullPointerException exc) {System.err.println(exc.getMessage()); }
	}
	
	@Override
	public long getSurpriseBoxX(SurpriseBox surpriseBox) throws IllegalStateException {
		if(surpriseBox.getPosition() == null)
			throw new IllegalStateException("Surprisebox is not placed on a board.");
		return surpriseBox.getPosition().getX();
	}
	
	@Override
	public long getSurpriseBoxY(SurpriseBox surpriseBox) throws IllegalStateException {
		if(surpriseBox.getPosition() == null)
			throw new IllegalStateException("Surprisebox is not placed on a board.");
		return surpriseBox.getPosition().getY();
	}
	
	@Override
	public Robot createRobot(int orientation, double initialEnergy) {
		EnergyAmount initialEnergyAmount = new EnergyAmount(initialEnergy, EnergyUnit.WATTSECOND);
		if(!(initialEnergy > 0) || (initialEnergyAmount.compareTo(Robot.getMaxCapacity()) == 1))
			{System.err.println("Invalid initial energy given!");
			return null;
			}
		Robot robot = new Robot(Orientation.getOrientation(orientation), initialEnergyAmount);
		return robot;
	}

	@Override
	public void putRobot(Board board, long x, long y, Robot robot) {
		assert (board != null) ;
		try {
			board.putEntity(Position.returnUniquePosition(x, y),robot);
		}
		catch(IllegalStateException exc) {System.err.println(exc.getMessage());}
		catch(IllegalArgumentException exc) {System.err.println(exc.getMessage());}
		catch(NullPointerException exc) {System.err.println(exc.getMessage());}
	}

	@Override
	public long getRobotX(Robot robot) throws IllegalStateException {
		if(robot.getPosition() == null)
			throw new IllegalStateException("Battery is not placed on a board.");
		return robot.getPosition().getX();
	}

	@Override
	public long getRobotY(Robot robot) throws IllegalStateException {
		if(robot.getPosition() == null)
			throw new IllegalStateException("Battery is not placed on a board.");
		return robot.getPosition().getY();
	}

	@Override
	public int getOrientation(Robot robot) {
		return robot.getOrientation().getIntOrientation();
	}

	@Override
	public double getEnergy(Robot robot) {
		return robot.getEnergy().getAmountInSpecifiedUnit(EnergyUnit.WATTSECOND);
	}

	@Override
	public void move(Robot robot) {
		if(robot.getEnergy().compareTo(robot.getEnergyToMove()) == -1) {
			System.err.println("Insufficient energy to move!");
		}
		else {
			try{
				robot.move();
			}
			catch(NullPointerException exc) {System.err.println(exc.getMessage());}
			catch(IllegalStateException exc) {System.err.println(exc.getMessage());}
		}
	}

	@Override
	public void turn(Robot robot) {
		if(robot.getEnergy().compareTo(Robot.getEnergyToTurn()) == -1)
			System.err.println("Insufficient energy-amount to turn!");
		else
			robot.turnClockwise();
	}

	@Override
	public void pickUpBattery(Robot robot, Battery battery) {
		try {
			robot.pickUp(battery);
		}
		catch(NullPointerException exc) {System.err.println(exc.getMessage());}
		catch(IllegalStateException exc) {System.err.println(exc.getMessage());}
	}

	@Override
	public void useBattery(Robot robot, Battery battery) {
		try{
			robot.use(battery);
		}
		catch(IllegalArgumentException exc) {System.err.println(exc.getMessage());}
	}

	@Override
	public void dropBattery(Robot robot, Battery battery) {
		try{
			robot.dropItem(battery);
		}
		catch(IllegalArgumentException exc) {System.err.println(exc.getMessage());}
		catch(NullPointerException exc) {System.err.println(exc.getMessage());}
	}
	
	@Override
	public void pickUpRepairKit(Robot robot, RepairKit repairKit) {
		try {
			robot.pickUp(repairKit);
		}
		catch(NullPointerException exc) {System.err.println(exc.getMessage());}
		catch(IllegalStateException exc) {System.err.println(exc.getMessage());}
	}

	@Override
	public void useRepairKit(Robot robot, RepairKit repairKit) {
		try{
			robot.use(repairKit);
		}
		catch(IllegalArgumentException exc) {System.err.println(exc.getMessage());}
	}

	@Override
	public void dropRepairKit(Robot robot, RepairKit repairKit) {
		try{
			robot.dropItem(repairKit);
		}
		catch(IllegalArgumentException exc) {System.err.println(exc.getMessage());}
		catch(NullPointerException exc) {System.err.println(exc.getMessage());}
	}
	
	@Override
	public void pickUpSurpriseBox(Robot robot, SurpriseBox surpriseBox) {
		try {
			robot.pickUp(surpriseBox);
		}
		catch(NullPointerException exc) {System.err.println(exc.getMessage());}
		catch(IllegalStateException exc) {System.err.println(exc.getMessage());}
	}

	@Override
	public void useSurpriseBox(Robot robot, SurpriseBox surpriseBox) {
		try{
			robot.use(surpriseBox);
		}
		catch(NullPointerException exc) {System.err.println(exc.getMessage()); }
		//Following exceptions should never occur.
		catch(IllegalArgumentException exc) {assert false;} 
		catch(IllegalStateException exc) {assert false;}
	}

	@Override
	public void dropSurpriseBox(Robot robot, SurpriseBox surpriseBox) {
		try{
			robot.dropItem(surpriseBox);
		}
		catch(IllegalArgumentException exc) {System.err.println(exc.getMessage());}
		catch(NullPointerException exc) {System.err.println(exc.getMessage());}
	}
	
	@Override
	public void transferItems(Robot from, Robot to) {
		try {
			from.transferItems(to);
		}
		catch(IllegalArgumentException exc) {System.err.println(exc.getMessage());}
	}

	@Override
	public int isMinimalCostToReach17Plus() {
		return 1;
	}

	@Override
	public double getMinimalCostToReach(Robot robot, long x, long y) {
		Position position = Position.returnUniquePosition(x,y);
		try {
			return robot.getEnergyRequiredToReach(position).getAmountInSpecifiedUnit(EnergyUnit.WATTSECOND);
		}
		catch(IllegalStateException exc) {
			return Double.parseDouble(exc.getMessage());
		}
		catch(IllegalArgumentException exc) {
			System.err.println(exc.getMessage());
			return -1;
		}
	}

	@Override
	public int isMoveNextTo18Plus() {
		return 1;
	}

	@Override
	public void moveNextTo(Robot robot, Robot other) {
		try{
			Robot.moveNextTo(robot,other);
		}
		catch(IllegalArgumentException exc) {System.err.println(exc.getMessage());}
		catch(NullPointerException exc) {System.err.println(exc.getMessage());}

	}

	@Override
	public void shoot(Robot robot) throws UnsupportedOperationException {
		if(robot.getEnergy().compareTo(Robot.getEnergyToShoot()) == -1)
			System.err.println("Insufficient energy to shoot.");
		else {
			try {
				robot.shoot();
				}
			catch(NullPointerException exc) {System.err.println(exc.getMessage());}
		}
	}

	@Override
	public Wall createWall() throws UnsupportedOperationException {
		return new Wall();
	}

	@Override
	public void putWall(Board board, long x, long y, Wall wall)
			throws UnsupportedOperationException {
		//assuming "wall" was meant instead of robot in the documentation of IFacade ;-)
		assert board != null;
		try{
			board.putEntity(Position.returnUniquePosition(x, y), wall);
		}
		catch(IllegalStateException exc) {System.err.println(exc.getMessage());}
		catch(NullPointerException exc) {System.err.println(exc.getMessage());}
		catch(IllegalArgumentException exc) {System.err.println(exc.getMessage());}
	}

	@Override
	public long getWallX(Wall wall) throws IllegalStateException, UnsupportedOperationException {
		if(wall.getBoard() == null)
			throw new IllegalStateException("This wall is not placed on a board!");
		else {
			return wall.getPosition().getX();
		}

	}

	@Override
	public long getWallY(Wall wall) throws IllegalStateException, UnsupportedOperationException {
		if(wall.getBoard() == null)
			throw new IllegalStateException("This wall is not placed on a board!");
		else {
			return wall.getPosition().getY();
		}
	}
	
	@Override
	public Set<Robot> getRobots(Board board) {
		assert board != null;
		HashSet<Robot> robots = new HashSet<Robot>();
		for(Entity entity : board.getEntitiesOfSpecifiedKindOnBoard(Robot.class))
			robots.add((Robot) entity);
		return robots;
	}
	
	@Override
	public Set<Wall> getWalls(Board board) throws UnsupportedOperationException {
		assert board != null;
		HashSet<Wall> walls = new HashSet<Wall>();
		for(Entity entity : board.getEntitiesOfSpecifiedKindOnBoard(Wall.class))
			walls.add((Wall) entity);
		return walls;
	}
	
	@Override
	public Set<RepairKit> getRepairKits(Board board) {
		assert board != null;
		HashSet<RepairKit> repairKits = new HashSet<RepairKit>();
		for(Entity entity : board.getEntitiesOfSpecifiedKindOnBoard(RepairKit.class))
			repairKits.add((RepairKit) entity);
		return repairKits;
	}
	
	@Override
	public Set<SurpriseBox> getSurpriseBoxes(Board board) {
		assert board != null;
		HashSet<SurpriseBox> surpriseBoxes = new HashSet<SurpriseBox>();
		for(Entity entity : board.getEntitiesOfSpecifiedKindOnBoard(SurpriseBox.class))
			surpriseBoxes.add((SurpriseBox) entity);
		return surpriseBoxes;
	}

	@Override
	public Set<Battery> getBatteries(Board board) {
		assert board != null;
		HashSet<Battery> batteries = new HashSet<Battery>();
		for(Entity entity : board.getEntitiesOfSpecifiedKindOnBoard(Battery.class))
			batteries.add((Battery) entity);
		return batteries;
	}

	@Override
	public int loadProgramFromFile(Robot robot, String path) {
		try {
			robot.addProgram(path);
			return 0;
		}
		catch(FileNotFoundException exc) {
			return            -                              42                                      ;
		}
	}

	@Override
	public int saveProgramToFile(Robot robot, String path) {
		try{
			FileWriter f = new FileWriter(path);
			PrintWriter w = new PrintWriter(f);
			w.println(robot.getProgram().toString());
			w.close();
			return 0;
		}
		catch(Exception exc){return (int) - Math.PI;}
	}

	@Override
	public void prettyPrintProgram(Robot robot, Writer writer) {
		try {
			writer.write(robot.getProgram().toString());
			writer.flush();
		}
		catch(IOException exc) {System.err.println(exc.getMessage());}
		
		
	}

	@Override
	public void stepn(Robot robot, int n) {
		try {
			robot.executeNSteps(n);
		}
		catch(IllegalStateException exc) {System.err.println(exc.getMessage());}
	}
}

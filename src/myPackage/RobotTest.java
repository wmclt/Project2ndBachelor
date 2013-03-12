package myPackage;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;

import org.junit.*;
import Auxiliary.*;
import Items.*;

public class RobotTest {

	
	
	Robot robotOrientationUPEnergy10000;
	Robot robotOrientationLEFTEnergy10000;
	Robot robotOrientationUPEnergy6000;
	Robot robotOrientationUPEnergy75;
	EnergyHolder batteryE2500W2500;
	Item batteryE2500W3000;
	Item batteryE2500W3500;
	Board boardH500W500;
	
	@Before
	public void setUp() throws Exception {
		robotOrientationUPEnergy10000 = new Robot(Orientation.UP, new EnergyAmount(10000, EnergyUnit.WATTSECOND));
		robotOrientationUPEnergy10000 = new Robot(Orientation.UP,new EnergyAmount(10000, EnergyUnit.WATTSECOND));
		robotOrientationLEFTEnergy10000 = new Robot(Orientation.LEFT,new EnergyAmount(10000, EnergyUnit.WATTSECOND));
		robotOrientationUPEnergy75 = new Robot(Orientation.UP,new EnergyAmount(75, EnergyUnit.WATTSECOND));
		robotOrientationUPEnergy6000 = new Robot(Orientation.UP,new EnergyAmount(6000, EnergyUnit.WATTSECOND));
		batteryE2500W2500 = new Battery(new EnergyAmount(2500, EnergyUnit.WATTSECOND),2500);
		batteryE2500W3000 = new Battery(new EnergyAmount(2500, EnergyUnit.WATTSECOND),3000);
		batteryE2500W3500 = new Battery(new EnergyAmount(2500, EnergyUnit.WATTSECOND),3500);
		boardH500W500 = new Board(500,500);
	}
	
	@Test
	public void constructor_CorrectCase(){
		Robot bot = new Robot(Orientation.DOWN,new EnergyAmount(10000, EnergyUnit.WATTSECOND));
		assertEquals(Orientation.DOWN , bot.getOrientation());
		assertEquals(bot.getEnergy(),new EnergyAmount(10000, EnergyUnit.WATTSECOND));
		assertEquals(0.5, bot.getFractionEnergy(),0.0);
	}
	
	@Test
	public void isLegalCapacity_tooLargeCapacity()
	{
		assertFalse(Robot.isValidCapacity(new EnergyAmount(25000, EnergyUnit.WATTSECOND)));
	}
	
	@Test
	public void recharge_positiveAmountLessThanMax() {
		Robot robot = new Robot(Orientation.UP, new EnergyAmount(8000, EnergyUnit.WATTSECOND));
		robot.charge(new EnergyAmount(1000, EnergyUnit.WATTSECOND));
		assertEquals(robot.getEnergy(),new EnergyAmount(9000, EnergyUnit.WATTSECOND));
	}
		
	@Test
	public void getOrientation_negativeOrientation(){
		Robot bot = new Robot(Orientation.getOrientation(-5),new EnergyAmount(10000, EnergyUnit.WATTSECOND));
		assertEquals(bot.getOrientation(),Orientation.LEFT);
	}
		
	@Test
	public void turnLeft_startOrientation0(){
		robotOrientationUPEnergy10000.turnCounterclockwise();
		assertEquals(robotOrientationUPEnergy10000.getOrientation(),Orientation.LEFT);
		assertEquals(robotOrientationUPEnergy10000.getEnergy(),new EnergyAmount(10000, EnergyUnit.WATTSECOND).substract(Robot.getEnergyToTurn()));
	}
	
	@Test
	public void getOrientation_tooHighOrientation(){
		Robot bot = new Robot(Orientation.getOrientation(7),new EnergyAmount(10000, EnergyUnit.WATTSECOND));
		assertEquals(bot.getOrientation(),Orientation.LEFT);
	}
	
	@Test
	public void turnRight_startOrientation3(){
		robotOrientationLEFTEnergy10000.turnClockwise();
		assertEquals(robotOrientationLEFTEnergy10000.getEnergy(),new EnergyAmount(10000, EnergyUnit.WATTSECOND).substract(Robot.getEnergyToTurn()));
		assertEquals(robotOrientationLEFTEnergy10000.getOrientation(),Orientation.UP);
	}
		
	@Test
	public void isObstacleFor_Wall(){
		Wall wall = new Wall();
		assertTrue(robotOrientationUPEnergy10000.isObstacleFor(wall));
	}
	
	@Test
	public void isObstacleFor_Robot(){
		assertTrue(robotOrientationUPEnergy10000.isObstacleFor(robotOrientationUPEnergy75));
	}
	
	@Test
	public void isObstacleFor_Item(){
		Battery battery = new Battery(new EnergyAmount(2500, EnergyUnit.WATTSECOND),5689);
		assertFalse(robotOrientationUPEnergy10000.isObstacleFor(battery));
	}
	
	@Test
	public void useBattery_CorrectCase(){
		boardH500W500.putEntity(Position.returnUniquePosition(2,2), robotOrientationUPEnergy6000);
		boardH500W500.putEntity(Position.returnUniquePosition(2,2), batteryE2500W2500);
		robotOrientationUPEnergy6000.pickUp(batteryE2500W2500);
		robotOrientationUPEnergy6000.use(batteryE2500W2500);
		assertEquals(robotOrientationUPEnergy6000.getEnergy(),new EnergyAmount(8500, EnergyUnit.WATTSECOND));
		assertEquals(batteryE2500W2500.getEnergy(),EnergyAmount.WATTS_0);
	}
	
	@Test
	public void useBattery_CorrectCaseMoreEnergyInBatteryThanNeeded(){
		Robot robot = new Robot(Orientation.UP,new EnergyAmount(18000, EnergyUnit.WATTSECOND));
		boardH500W500.putEntity(Position.returnUniquePosition(2,2), robot);
		boardH500W500.putEntity(Position.returnUniquePosition(2,2), batteryE2500W2500);
		robot.pickUp(batteryE2500W2500);
		robot.use(batteryE2500W2500);
		assertEquals(robot.getEnergy(),new EnergyAmount(20000, EnergyUnit.WATTSECOND));
		assertEquals(batteryE2500W2500.getEnergy(),new EnergyAmount(500, EnergyUnit.WATTSECOND));
	}
	
	@Test
	public void useBattery_BatteryTerminated(){
		boardH500W500.putEntity(Position.returnUniquePosition(2,2), robotOrientationUPEnergy10000);
		boardH500W500.putEntity(Position.returnUniquePosition(2,2), batteryE2500W2500);
		robotOrientationUPEnergy10000.pickUp(batteryE2500W2500);
		batteryE2500W2500.terminate();
		assertTrue(batteryE2500W2500.isTerminated());
		try{
		robotOrientationUPEnergy10000.use(batteryE2500W2500);
		}
		catch(IllegalArgumentException exc){
			assertEquals(exc.getMessage(),"Item is terminated!");
			assertTrue(( robotOrientationUPEnergy10000).getCarryingsOfSpecifiedKindInRobot(Item.class).isEmpty());
			assertEquals(robotOrientationUPEnergy10000.getEnergy(),new EnergyAmount(10000, EnergyUnit.WATTSECOND));
		}
	}
	
	@Test
	public void useBattery_BatteryNotInLoad(){
		boardH500W500.putEntity(Position.returnUniquePosition(2,2), robotOrientationUPEnergy10000);
		boardH500W500.putEntity(Position.returnUniquePosition(2,2), batteryE2500W2500);
		try{
		robotOrientationUPEnergy10000.use(batteryE2500W2500);
		}
		catch(IllegalArgumentException exc){
			assertEquals(exc.getMessage(),"No such item found in robot's load.");
			assertTrue(robotOrientationUPEnergy10000.getCarryingsOfSpecifiedKindInRobot(Item.class).isEmpty());
			assertEquals(robotOrientationUPEnergy10000.getEnergy(),new EnergyAmount(10000, EnergyUnit.WATTSECOND));
		}
	}
	
	@Test
	public void pickUp_notOnSamePosition(){
		boardH500W500.putEntity(Position.returnUniquePosition(2,2), robotOrientationUPEnergy10000);
		boardH500W500.putEntity(Position.returnUniquePosition(2,3), batteryE2500W2500);
		try{
			robotOrientationUPEnergy10000.pickUp(batteryE2500W2500);
		}
		catch(IllegalStateException exc){
			assertEquals(exc.getMessage(),"Not on the same position!");
			assertTrue(robotOrientationUPEnergy10000.getCarryingsOfSpecifiedKindInRobot(Item.class).isEmpty());
		}
	}
	
	@Test
	public void pickUp_samePositionDifferentBoard(){
		Board board2 = new Board(300,300);
		boardH500W500.putEntity(Position.returnUniquePosition(2,2), robotOrientationUPEnergy10000);
		board2.putEntity(Position.returnUniquePosition(2,2), batteryE2500W2500);
		try{
			robotOrientationUPEnergy10000.pickUp(batteryE2500W2500);
		}
		catch(IllegalStateException exc){
			assertEquals(exc.getMessage(),"Not on the same position!");
			assertTrue(robotOrientationUPEnergy10000.getCarryingsOfSpecifiedKindInRobot(Item.class).isEmpty());
		}
	}
	
	@Test
	public void pickUp_FocusOnEnergyRequiredToReach(){
		boardH500W500.putEntity(Position.returnUniquePosition(2,2), robotOrientationUPEnergy10000);
		boardH500W500.putEntity(Position.returnUniquePosition(2,2), batteryE2500W3000);
		boardH500W500.putEntity(Position.returnUniquePosition(2,2), batteryE2500W3500);
		boardH500W500.putEntity(Position.returnUniquePosition(2,2), batteryE2500W2500);
		robotOrientationUPEnergy10000.pickUp(batteryE2500W2500);
		assertEquals(robotOrientationUPEnergy10000.getEnergyToMove(),new EnergyAmount(600, EnergyUnit.WATTSECOND));
		robotOrientationUPEnergy10000.pickUp(batteryE2500W3000);
		assertEquals(robotOrientationUPEnergy10000.getEnergyToMove(),new EnergyAmount(750, EnergyUnit.WATTSECOND));
		robotOrientationUPEnergy10000.pickUp(batteryE2500W3500);
		assertEquals(robotOrientationUPEnergy10000.getEnergyToMove(),new EnergyAmount(950, EnergyUnit.WATTSECOND));
		robotOrientationUPEnergy10000.dropItem(batteryE2500W2500);
		assertEquals(robotOrientationUPEnergy10000.getEnergyToMove(),new EnergyAmount(800, EnergyUnit.WATTSECOND));
		
	}
	
	@Test
	public void dropItem_CorrectCase(){
		boardH500W500.putEntity(Position.returnUniquePosition(2,2), robotOrientationUPEnergy10000);
		boardH500W500.putEntity(Position.returnUniquePosition(2,2), batteryE2500W2500);
		robotOrientationUPEnergy10000.pickUp(batteryE2500W2500);
		assertEquals(robotOrientationUPEnergy10000.getCarryingsOfSpecifiedKindInRobot(Battery.class).size(),1);
		assertTrue(batteryE2500W2500.getBoard() == null);
		assertTrue(batteryE2500W2500.getPosition() == null);
		robotOrientationUPEnergy10000.dropItem(batteryE2500W2500);
		assertFalse(robotOrientationUPEnergy10000.carriesItem(batteryE2500W2500));
		assertEquals(batteryE2500W2500.getBoard(),boardH500W500);
		assertEquals(batteryE2500W2500.getPosition(),Position.returnUniquePosition(2, 2));
	}
	
	@Test (expected = IllegalArgumentException.class) 
	public void dropItem_ItemNotOnBoard(){
		robotOrientationLEFTEnergy10000.dropItem(batteryE2500W2500);
	}
	
	@Test 
	public void dropItem_notOnBoard(){
		boardH500W500.putEntity(Position.returnUniquePosition(2,2), robotOrientationUPEnergy10000);
		boardH500W500.putEntity(Position.returnUniquePosition(2,2), batteryE2500W2500);
		robotOrientationUPEnergy10000.pickUp(batteryE2500W2500);
		assertTrue(robotOrientationUPEnergy10000.carriesItem(batteryE2500W2500));
		assertTrue(batteryE2500W2500.getBoard() == null);
		assertTrue(batteryE2500W2500.getPosition() == null);
		boardH500W500.removeEntity(robotOrientationUPEnergy10000);
		robotOrientationUPEnergy10000.dropItem(batteryE2500W2500);
		assertFalse(robotOrientationUPEnergy10000.carriesItem(batteryE2500W2500));
	}
	
	@Test
	public void sortLoad(){
		boardH500W500.putEntity(Position.returnUniquePosition(2,2), robotOrientationUPEnergy10000);
		boardH500W500.putEntity(Position.returnUniquePosition(2,2), batteryE2500W3000);
		boardH500W500.putEntity(Position.returnUniquePosition(2,2), batteryE2500W3500);
		boardH500W500.putEntity(Position.returnUniquePosition(2,2), batteryE2500W2500);
		robotOrientationUPEnergy10000.pickUp(batteryE2500W2500);
		robotOrientationUPEnergy10000.pickUp(batteryE2500W3000);
		robotOrientationUPEnergy10000.pickUp(batteryE2500W3500);
		assertEquals(robotOrientationUPEnergy10000.getHeaviestItem(1).getWeight(),3500);
		assertEquals(robotOrientationUPEnergy10000.getHeaviestItem(2).getWeight(),3000);
		assertEquals(robotOrientationUPEnergy10000.getHeaviestItem(3).getWeight(),2500);
		try{
			robotOrientationUPEnergy10000.getHeaviestItem(5);
		}
		catch(IllegalArgumentException exc){
			assertEquals(exc.getMessage(),"The given index 5 is bigger than the number of items carried by the robot!");
		}
		robotOrientationUPEnergy10000.dropItem(batteryE2500W3000);
		assertEquals(robotOrientationUPEnergy10000.getHeaviestItem(2).getWeight(),2500);
	}
	
	@Test
	public void shoot_TargetHit_OneEntityOnImpactPosition(){
		boardH500W500.putEntity(Position.returnUniquePosition(8,2), robotOrientationLEFTEnergy10000);
		boardH500W500.putEntity(Position.returnUniquePosition(2,2), robotOrientationUPEnergy75);
		robotOrientationLEFTEnergy10000.shoot();
		assertEquals(robotOrientationLEFTEnergy10000.getEnergy(),new EnergyAmount(9000, EnergyUnit.WATTSECOND));
		assertEquals(robotOrientationUPEnergy75.getCapacity(),new EnergyAmount(16000, EnergyUnit.WATTSECOND));
	}
	
	@Test
	public void shoot_NoTargetHit(){
		boardH500W500.putEntity(Position.returnUniquePosition(2,2), robotOrientationUPEnergy10000);
		try{
			robotOrientationUPEnergy10000.shoot();
		}
		catch(NullPointerException exc){
			assertEquals(robotOrientationUPEnergy10000.getEnergy(),new EnergyAmount(9000, EnergyUnit.WATTSECOND));
			assertEquals(exc.getMessage(),"Robot could not hit anything in front of it!");
		}
	}
	
	@Test
	public void move_CorrectCase(){
		boardH500W500.putEntity(Position.returnUniquePosition(2,2), robotOrientationUPEnergy10000);
		robotOrientationUPEnergy10000.move();
		assertEquals(robotOrientationUPEnergy10000.getPosition(),Position.returnUniquePosition(2, 1));
		assertEquals(robotOrientationUPEnergy10000.getEnergy(),new EnergyAmount(9500, EnergyUnit.WATTSECOND));
		assertFalse(boardH500W500.containsEntity(Position.returnUniquePosition(2, 2)));
	}
	
	@Test
	public void move_FrontSquareObstructed(){
		boardH500W500.putEntity(Position.returnUniquePosition(2,1), new Wall());
		boardH500W500.putEntity(Position.returnUniquePosition(2,2), robotOrientationUPEnergy10000);
		try{
			robotOrientationUPEnergy10000.move();
		}
		catch(IllegalArgumentException exc){
			assertEquals(robotOrientationUPEnergy10000.getPosition(),Position.returnUniquePosition(2, 2));
			assertEquals(robotOrientationUPEnergy10000.getEnergy(),new EnergyAmount(10000, EnergyUnit.WATTSECOND));
		}
	}
	
	@Test
	public void move_notOnBoard(){
		try{
			robotOrientationLEFTEnergy10000.move();
		}
		catch(NullPointerException exc){
			assertEquals(exc.getMessage(),"404: no board found!");
		}
	}
	
	@Test
	public void move_onEdgeOfBoard(){
		boardH500W500.putEntity(Position.returnUniquePosition(0,0), robotOrientationUPEnergy10000);
		try{
			robotOrientationUPEnergy10000.move();
		}
		catch(IllegalStateException exc){
			assertEquals(exc.getMessage(),"Position in front of the robot is out-of-bounds!");
		}
	}
	
	@Test
	public void getEnergyRequiredToReach_targetUnreachable(){
		Board board = new Board(5,5);
		board.putEntity(Position.returnUniquePosition(0,0),robotOrientationUPEnergy10000);
		board.putEntity(Position.returnUniquePosition(2,2), new Wall());
		board.putEntity(Position.returnUniquePosition(3,2), new Wall());
		board.putEntity(Position.returnUniquePosition(2,3), new Wall());
		board.putEntity(Position.returnUniquePosition(2,4), new Wall());
		board.putEntity(Position.returnUniquePosition(2,5), new Wall());
		board.putEntity(Position.returnUniquePosition(4,2), new Wall());
		board.putEntity(Position.returnUniquePosition(5,2), new Wall());
		try{
			robotOrientationUPEnergy10000.getEnergyRequiredToReach(Position.returnUniquePosition(5, 5));
		}
		catch (IllegalStateException exc){
			assertEquals(Double.parseDouble(exc.getMessage()), -1, 0);
		}
		
	}
	
	@Test
	public void getEnergyRequiredToReach_notOnBoard(){
		try{
			robotOrientationUPEnergy10000.getEnergyRequiredToReach(Position.returnUniquePosition(3, 3));
		}
		catch (IllegalStateException exc){
			assertEquals(Double.parseDouble(exc.getMessage()), -1, 0);
		}
	}
	
	@Test
	public void getEnergyRequiredToReach_simpleYetCorrectCase(){
		boardH500W500.putEntity(Position.returnUniquePosition(2,2), robotOrientationUPEnergy10000);
		boardH500W500.putEntity(Position.returnUniquePosition(3,2), robotOrientationUPEnergy75);
		assertEquals(new EnergyAmount(2200, EnergyUnit.WATTSECOND),robotOrientationUPEnergy10000.getEnergyRequiredToReach(Position.returnUniquePosition(4, 2)));
		try{
			robotOrientationUPEnergy75.getEnergyRequiredToReach(Position.returnUniquePosition(4, 2));
		}
		catch (IllegalStateException exc){
			assertEquals(Double.parseDouble(exc.getMessage()), -1, 0);
		}
	}
	
	@Test
	public void moveNextTo_SufficientEnergy(){
		Board board = new Board(200,200);
		board.putEntity(Position.returnUniquePosition(42,44), robotOrientationUPEnergy10000);
		board.putEntity(Position.returnUniquePosition(45,41), robotOrientationLEFTEnergy10000);
		Robot.moveNextTo(robotOrientationUPEnergy10000, robotOrientationLEFTEnergy10000);
		assertEquals(new EnergyAmount(20000, EnergyUnit.WATTSECOND).substract(robotOrientationUPEnergy10000.getEnergy()).substract(robotOrientationLEFTEnergy10000.getEnergy()),new EnergyAmount(2500, EnergyUnit.WATTSECOND));
		assertEquals(1,Position.getDistance(robotOrientationLEFTEnergy10000.getPosition(), robotOrientationUPEnergy10000.getPosition()));
	}
	
	@Test
	public void moveNextTo_SufficientEnergyWithItem(){
		Board board = new Board(50,50);		
		board.putEntity(Position.returnUniquePosition(2,4), batteryE2500W3000);
		board.putEntity(Position.returnUniquePosition(2,4), robotOrientationUPEnergy10000);
		robotOrientationUPEnergy10000.pickUp(batteryE2500W3000);
		board.putEntity(Position.returnUniquePosition(5,1), batteryE2500W3500);
		board.putEntity(Position.returnUniquePosition(5,1), robotOrientationLEFTEnergy10000);
		robotOrientationLEFTEnergy10000.pickUp(batteryE2500W3500);
		Robot.moveNextTo(robotOrientationUPEnergy10000, robotOrientationLEFTEnergy10000);		
		assertEquals(new EnergyAmount(20000, EnergyUnit.WATTSECOND).substract(robotOrientationUPEnergy10000.getEnergy()).substract(robotOrientationLEFTEnergy10000.getEnergy()),new EnergyAmount(3250, EnergyUnit.WATTSECOND));
		assertEquals(1,Position.getDistance(robotOrientationLEFTEnergy10000.getPosition(), robotOrientationUPEnergy10000.getPosition()));
	}
	
	@Test
	public void moveNextTo_InsufficientEnergy(){
		Board board = new Board(6,4);
		Wall wall1 = new Wall();
		Wall wall2 = new Wall();
		Wall wall3 = new Wall();
		Wall wall4 = new Wall();
		Wall wall5 = new Wall();
		Wall wall6 = new Wall();
		Wall wall7 = new Wall();
		Wall wall8 = new Wall();
		Robot robot1 = new Robot(Orientation.RIGHT,new EnergyAmount(3500, EnergyUnit.WATTSECOND));
		Robot robot2 = new Robot(Orientation.UP,new EnergyAmount(3500, EnergyUnit.WATTSECOND));
		board.putEntity(Position.returnUniquePosition(3,4), wall1);
		board.putEntity(Position.returnUniquePosition(3,3), wall2);
		board.putEntity(Position.returnUniquePosition(3,2), wall3);
		board.putEntity(Position.returnUniquePosition(3,1), wall4);
		board.putEntity(Position.returnUniquePosition(1,1), wall5);
		board.putEntity(Position.returnUniquePosition(2,1), wall6);
		board.putEntity(Position.returnUniquePosition(4,1), wall7);
		board.putEntity(Position.returnUniquePosition(5,1), wall8);
		board.putEntity(Position.returnUniquePosition(1,3), robot1);
		board.putEntity(Position.returnUniquePosition(4,3), robot2);
		Robot.moveNextTo(robot1, robot2);
		assertEquals(robot1.getPosition(),Position.returnUniquePosition(2, 3));
		assertEquals(robot2.getPosition(),Position.returnUniquePosition(4, 3));
		assertEquals(robot1.getEnergy(),new EnergyAmount(3000, EnergyUnit.WATTSECOND));
		assertEquals(robot2.getEnergy(),new EnergyAmount(3500, EnergyUnit.WATTSECOND));
	}
	
	@Test
	public void cloneTest(){
		Board board = new Board(10,10);		
		board.putEntity(Position.returnUniquePosition(2,4), batteryE2500W3000);
		board.putEntity(Position.returnUniquePosition(2,4), robotOrientationLEFTEnergy10000);
		robotOrientationLEFTEnergy10000.pickUp(batteryE2500W3000);
		Robot bot = robotOrientationLEFTEnergy10000.clone();
		assertEquals(bot.getOrientation(),robotOrientationLEFTEnergy10000.getOrientation());
		assertEquals(bot.getEnergy(),robotOrientationLEFTEnergy10000.getEnergy());
		assertFalse(bot == robotOrientationLEFTEnergy10000);
		assertEquals(bot.getEnergyToMove(),robotOrientationLEFTEnergy10000.getEnergyToMove());
	}
	
	@Test
	public void terminateRobot() {
		boardH500W500.putEntity(Position.returnUniquePosition(1,1), batteryE2500W2500);
		boardH500W500.putEntity(Position.returnUniquePosition(1,1), robotOrientationLEFTEnergy10000);
		robotOrientationLEFTEnergy10000.pickUp(batteryE2500W2500);
		robotOrientationLEFTEnergy10000.terminate();
		assertTrue(robotOrientationLEFTEnergy10000.isTerminated());
		assertTrue(batteryE2500W2500.isTerminated());
	}
	
	@Test
	public void canHaveAsEnergy_incorrectCases() {
		assertFalse(robotOrientationLEFTEnergy10000.canHaveAsEnergy(new EnergyAmount(25000,EnergyUnit.WATTSECOND)));
	}
	
	@Test (expected = NullPointerException.class)
	public void pickup_itemNoBoard() throws Exception{
		boardH500W500.putEntity(Position.returnUniquePosition(1, 2), robotOrientationUPEnergy6000);
		robotOrientationUPEnergy6000.pickUp(batteryE2500W3000);
	}
	
	@Test (expected = NullPointerException.class)
	public void pickup_robotNoBoard() throws Exception{
		boardH500W500.putEntity(Position.returnUniquePosition(1, 2), batteryE2500W2500);
		robotOrientationLEFTEnergy10000.pickUp(batteryE2500W2500);
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void transferItems_robotsNotOnSameBoard() {
		boardH500W500.putEntity(Position.returnUniquePosition(1,1), batteryE2500W2500);
		boardH500W500.putEntity(Position.returnUniquePosition(1,1), robotOrientationLEFTEnergy10000);
		robotOrientationLEFTEnergy10000.pickUp(batteryE2500W2500);
		robotOrientationLEFTEnergy10000.transferItems(robotOrientationUPEnergy10000);
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void transferItems_robotsTooFarAway() {
		boardH500W500.putEntity(Position.returnUniquePosition(1,1), batteryE2500W2500);
		boardH500W500.putEntity(Position.returnUniquePosition(1,1), robotOrientationLEFTEnergy10000);
		boardH500W500.putEntity(Position.returnUniquePosition(3,1), robotOrientationUPEnergy10000);
		robotOrientationLEFTEnergy10000.pickUp(batteryE2500W2500);
		robotOrientationLEFTEnergy10000.transferItems(robotOrientationUPEnergy10000);
	}
	
	@Test 
	public void transferItems_correctCase() {
		boardH500W500.putEntity(Position.returnUniquePosition(1,1), batteryE2500W2500);
		boardH500W500.putEntity(Position.returnUniquePosition(1,1), robotOrientationLEFTEnergy10000);
		boardH500W500.putEntity(Position.returnUniquePosition(2,1), robotOrientationUPEnergy10000);
		robotOrientationLEFTEnergy10000.pickUp(batteryE2500W2500);
		robotOrientationLEFTEnergy10000.transferItems(robotOrientationUPEnergy10000);
		assertTrue(robotOrientationUPEnergy10000.carriesItem(batteryE2500W2500));
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void moveNextTo_incorrectCase() throws Exception{
		boardH500W500.putEntity(Position.returnUniquePosition(1,1), robotOrientationLEFTEnergy10000);
		Robot.moveNextTo(robotOrientationLEFTEnergy10000, robotOrientationUPEnergy10000);
	}
	
	@Test (expected = NullPointerException.class)
	public void teleport_notOnBoard() {
		robotOrientationLEFTEnergy10000.teleport();
	}
	
	@Test
	public void teleport_correctCase() {
		boardH500W500.putEntity(Position.returnUniquePosition(1,1), robotOrientationLEFTEnergy10000);
		Position oldPosition = robotOrientationLEFTEnergy10000.getPosition();
		robotOrientationLEFTEnergy10000.teleport();
		assertFalse(robotOrientationLEFTEnergy10000.getPosition() == oldPosition);
		assertTrue(boardH500W500.isValidPosition(robotOrientationLEFTEnergy10000.getPosition()));
	}
	
	@Test	
	public void hitAndTerminate() {
		for(int i = 0; i < 5; i++)
			robotOrientationLEFTEnergy10000.hit();
		assertTrue(robotOrientationLEFTEnergy10000.isTerminated());
	}
	
	@Test (expected = IllegalStateException.class)
	public void executeProgram_noProgram() throws IllegalStateException{
		boardH500W500.putEntity(Position.returnUniquePosition(1,1), robotOrientationLEFTEnergy10000);
		robotOrientationLEFTEnergy10000.executeProgram();
	}
	
	@Test (expected = IllegalStateException.class)
	public void executeProgram_notOnBoard() throws FileNotFoundException, IllegalStateException{
		robotOrientationLEFTEnergy10000.addProgram("src\\Programs\\RobotTestProgram");
		robotOrientationLEFTEnergy10000.executeProgram();
	}
	
	@Test 
	public void executeProgram_correctCase() throws FileNotFoundException{
		EnergyAmount oldEnergy = robotOrientationLEFTEnergy10000.getEnergy();
		boardH500W500.putEntity(Position.returnUniquePosition(1,1), robotOrientationLEFTEnergy10000);
		robotOrientationLEFTEnergy10000.addProgram("src\\Programs\\RobotTestProgram");
		robotOrientationLEFTEnergy10000.executeProgram();
		assertTrue(robotOrientationLEFTEnergy10000.getEnergy().add(Robot.getEnergyToShoot()).hasSameValueAs(oldEnergy));
	}
	
	@Test (expected = IllegalStateException.class)
	public void executeNStepProgram_noProgram() {
		boardH500W500.putEntity(Position.returnUniquePosition(1,1), robotOrientationLEFTEnergy10000);
		robotOrientationLEFTEnergy10000.executeNSteps(1);
	}
	
	@Test (expected = IllegalStateException.class)
	public void executeNStepProgram_notOnBoard() throws IllegalStateException{
		robotOrientationLEFTEnergy10000.executeNSteps(3);
	}
	
	@Test
	public void executeNStepProgram_correctCase() throws FileNotFoundException {
		EnergyAmount oldEnergy = robotOrientationUPEnergy6000.getEnergy();
		boardH500W500.putEntity(Position.returnUniquePosition(1,1), robotOrientationUPEnergy6000);
		robotOrientationUPEnergy6000.addProgram("src\\Programs\\RobotTestProgram");
		robotOrientationUPEnergy6000.executeNSteps(3);
		assertTrue(robotOrientationUPEnergy6000.getEnergy().add(Robot.getEnergyToShoot().rescale(3)).hasSameValueAs(oldEnergy));
	}
	
	@After
	public void tearDown() throws Exception {
		System.err.close();
		System.out.close();
	}


}

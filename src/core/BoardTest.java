package core;

import static org.junit.Assert.*;

import java.util.Iterator;
import java.util.NoSuchElementException;




import org.junit.Before;
import org.junit.Test;

import Auxiliary.EnergyAmount;
import Auxiliary.EnergyUnit;
import Auxiliary.Orientation;
import Auxiliary.Position;
import Inspectors.EnergyInspector;
import Items.Battery;
import Items.EnergyHolder;
import Items.Item;

public class BoardTest {

	private Board boardH500W500;
	private Robot robot;
	private Wall wall;
	private EnergyHolder battery;
	private Position positionX2Y5;
	private EnergyInspector energyInspectorMin3000Ws;
	
	@Before
	public void setUp() throws Exception{
		boardH500W500 = new Board(500,500);
		robot = new Robot(Orientation.UP, new EnergyAmount(10000, EnergyUnit.WATTSECOND));
		battery = new Battery(new EnergyAmount(2500, EnergyUnit.WATTSECOND),5000);
		wall = new Wall();
		positionX2Y5 = Position.returnUniquePosition(2, 5);
		energyInspectorMin3000Ws = new EnergyInspector(new EnergyAmount(3000, EnergyUnit.WATTSECOND));
	}
	
	@Test
	public void constructor_CorrectCase() {
		Board board = new Board(500,500);
		assertEquals(board.getHeight(),500);
		assertEquals(board.getWidth(),500);
	}
	
	@SuppressWarnings("unused")
	@Test	(expected = IllegalArgumentException.class)
	public void constructor_invalidHeight(){
		Board board = new Board(500,-500);
	}
	
	@SuppressWarnings("unused")
	@Test	(expected = IllegalArgumentException.class)
	public void constructor_invalidWidth(){
		Board board = new Board(-500, 500);
	}
	
	@Test
	public void putEntity_EntityNull(){
		Robot robot = null;
		try{
			boardH500W500.putEntity(positionX2Y5,robot);
		}
		catch(NullPointerException exc){
			assertEquals(exc.getMessage(),"No entity given.");
		}
	}
	
	@Test
	public void putEntity_EntityTerminated(){
		robot.terminate();
		try{
			boardH500W500.putEntity(positionX2Y5,robot);
		}
		catch(IllegalArgumentException exc){
			assertEquals(exc.getMessage(),"The entity is terminated!");
		}
	}
	
	@Test
	public void putEntity_IllegalPosition(){
		try{
			boardH500W500.putEntity(Position.returnUniquePosition(2,562), robot);
		}
		catch(IllegalArgumentException exc){
			assertEquals(exc.getMessage(),"Illegal coordinate!");
		}
	}
	
	@Test
	public void putEntity_PositionObstructed(){
		boardH500W500.putEntity(positionX2Y5, wall);
		try{
			boardH500W500.putEntity(positionX2Y5,robot);
		}
		catch(IllegalArgumentException exc){
			assertEquals(exc.getMessage(), "Position obstructed, contains an obstacle to the entity that need be put.");
		}
	}
	
	@Test
	public void putEntity_correctCaseEmptyPosition(){
		boardH500W500.putEntity(positionX2Y5, wall);
		assertEquals(wall.getPosition().getX(),2);
		assertEquals(wall.getPosition().getY(),5);
		assertEquals(wall.getBoard(),boardH500W500);
		assertFalse(boardH500W500.getEntitiesOfSpecifiedKindOnBoard(Wall.class).isEmpty());
	}
	
	@Test
	public void putEntity_correctCaseExistentPosition(){
		boardH500W500.putEntity(positionX2Y5, battery);
		boardH500W500.putEntity(positionX2Y5, robot);
		assertTrue(boardH500W500.getEntitiesOfSpecifiedKindOnBoard(Battery.class).contains(battery));
		assertTrue(boardH500W500.getEntitiesOfSpecifiedKindOnBoard(Robot.class).contains(robot));
	}
	
	@Test
	public void putEntity_EntityAlreadyOnRightBoard(){
		boardH500W500.putEntity(positionX2Y5, robot);
		boardH500W500.putEntity(Position.returnUniquePosition(5,8), robot);
		assertEquals(robot.getPosition().getX(),5);
		assertEquals(robot.getPosition().getY(),8);
	}
	
	@Test
	public void putEntity_EntityAlreadyOnDifferentBoard(){
		boardH500W500.putEntity(positionX2Y5, robot);
		Board board2 = new Board(500,500);
		board2.putEntity(Position.returnUniquePosition(3,6), robot);
		assertEquals(robot.getPosition().getX(),3);
		assertEquals(robot.getPosition().getY(),6);
		assertEquals(robot.getBoard(),board2);
		assertTrue(boardH500W500.getEntitiesOfSpecifiedKindOnBoard(Robot.class).isEmpty());
		
	}
	
	@Test
	public void merge_CorrectCase(){
		Board firstBoard = new Board(1,1);
		Board secondBoard = new Board(2,2);
		Robot robot2 = new Robot(Orientation.UP, new EnergyAmount(256, EnergyUnit.WATTSECOND));
		Robot robot3 = new Robot(Orientation.DOWN, new EnergyAmount(256, EnergyUnit.WATTSECOND));
		Wall wall2 = new Wall();
		Wall wall3 = new Wall();
		Item battery2 = new Battery(new EnergyAmount(256, EnergyUnit.WATTSECOND),25);
		firstBoard.putEntity(Position.returnUniquePosition(1,0), robot);
		firstBoard.putEntity(Position.returnUniquePosition(0,1), wall);
		firstBoard.putEntity(Position.returnUniquePosition(1,1), battery);
		secondBoard.putEntity(Position.returnUniquePosition(0,0), wall2);
		secondBoard.putEntity(Position.returnUniquePosition(0,1), wall3);
		secondBoard.putEntity(Position.returnUniquePosition(1,0), battery2);
		secondBoard.putEntity(Position.returnUniquePosition(1,1), robot2);
		secondBoard.putEntity(Position.returnUniquePosition(2,2), robot3);
		Board.merge(firstBoard, secondBoard);
		assertEquals(firstBoard,wall2.getBoard());
		assertEquals(Position.returnUniquePosition(0, 0), wall2.getPosition());
		assertEquals(firstBoard,wall.getBoard());
		assertEquals(Position.returnUniquePosition(0, 1), wall.getPosition());
		assertEquals(firstBoard,robot.getBoard());
		assertEquals(Position.returnUniquePosition(1, 0), robot.getPosition());
		assertEquals(firstBoard,battery2.getBoard());
		assertEquals(Position.returnUniquePosition(1, 0), battery2.getPosition());
		assertEquals(firstBoard,battery.getBoard());
		assertEquals(Position.returnUniquePosition(1, 1), battery.getPosition());
		assertEquals(firstBoard,robot2.getBoard());
		assertEquals(Position.returnUniquePosition(1, 1), robot2.getPosition());
		assertTrue(secondBoard.isTerminated());
		assertTrue(wall3.isTerminated());
		assertTrue(robot3.isTerminated());
	}
	
	@Test
	public void terminate(){
		boardH500W500.putEntity(Position.returnUniquePosition(1,1),robot);
		boardH500W500.putEntity(Position.returnUniquePosition(1,1),battery);
		boardH500W500.putEntity(Position.returnUniquePosition(25,26), wall);
		boardH500W500.terminate();
		assertTrue(robot.isTerminated());
		assertTrue(battery.isTerminated());
		assertTrue(wall.isTerminated());
		assertTrue(boardH500W500.isTerminated());
		assertFalse(boardH500W500.containsEntity(Position.returnUniquePosition(1, 1)));
		
	}
	
	@Test
	public void containsEntity_trueAndFalseCase(){
		assertFalse(boardH500W500.containsEntity(Position.returnUniquePosition(1, 1)));
		boardH500W500.putEntity(Position.returnUniquePosition(1,1), battery);
		assertTrue(boardH500W500.containsEntity(Position.returnUniquePosition(1, 1)));
	}
	
	@Test
	public void getEntitiesOfSpecifiedKindOnBoard(){
		boardH500W500.putEntity(positionX2Y5, battery);
		boardH500W500.putEntity(positionX2Y5, robot);
		boardH500W500.putEntity(Position.returnUniquePosition(1,1), wall);
		assertTrue(boardH500W500.getEntitiesOfSpecifiedKindOnBoard(Battery.class).contains(battery));
		assertFalse(boardH500W500.getEntitiesOfSpecifiedKindOnBoard(Battery.class).contains(robot));
		assertFalse(boardH500W500.getEntitiesOfSpecifiedKindOnBoard(Battery.class).contains(wall));
		assertTrue(boardH500W500.getEntitiesOfSpecifiedKindOnBoard(Robot.class).contains(robot));
		assertFalse(boardH500W500.getEntitiesOfSpecifiedKindOnBoard(Robot.class).contains(battery));
		assertFalse(boardH500W500.getEntitiesOfSpecifiedKindOnBoard(Robot.class).contains(wall));
		assertTrue(boardH500W500.getEntitiesOfSpecifiedKindOnBoard(Wall.class).contains(wall));
		assertFalse(boardH500W500.getEntitiesOfSpecifiedKindOnBoard(Wall.class).contains(battery));
		assertFalse(boardH500W500.getEntitiesOfSpecifiedKindOnBoard(Wall.class).contains(robot));
	}
		
	@Test
	public void isPositionReachable(){
		boardH500W500.putEntity(Position.returnUniquePosition(5,5), robot);
		assertTrue(boardH500W500.isPositionReachableForRobot(Position.returnUniquePosition(2, 2), robot));
		boardH500W500.putEntity(Position.returnUniquePosition(1,1), wall);
		assertFalse(boardH500W500.isPositionReachableForRobot(Position.returnUniquePosition(1, 1), robot));
		boardH500W500.putEntity(Position.returnUniquePosition(0,1), new Wall());
		boardH500W500.putEntity(Position.returnUniquePosition(1,0), new Wall());
		assertFalse(boardH500W500.isPositionReachableForRobot(Position.returnUniquePosition(0, 0), robot));
	}
	
	@Test
	public void returnFirstOccupiedPosition() {
		Robot robotOne = new Robot(Orientation.UP, new EnergyAmount(10000, EnergyUnit.WATTSECOND));
		Robot robotTwo = new Robot(Orientation.UP, new EnergyAmount(10000, EnergyUnit.WATTSECOND));
		boardH500W500.putEntity(Position.returnUniquePosition(5,365), robotOne);
		boardH500W500.putEntity(Position.returnUniquePosition(5,17), robotTwo);
		assertEquals(boardH500W500.returnFirstOccupiedPositionInDirection(robotOne),robotTwo.getPosition());
	}
	
	@Test (expected = NullPointerException.class)
	public void removeEntity_null() throws Exception{
		boardH500W500.removeEntity(null);
	}
	
	@Test (expected = NullPointerException.class)
	public void removeEntity_notOnBoard() throws Exception{
		boardH500W500.removeEntity(robot);
	}

	@Test 
	public void containsEntityOfSpecifiedKindOnPosition() {
		boardH500W500.putEntity(Position.returnUniquePosition(5,365), robot);
		Position position = Position.returnUniquePosition(1,1);
		assertFalse(boardH500W500.containsEntityOfSpecifiedKindOnPosition(position, robot.getClass()));
		position = robot.getPosition();
		assertFalse(boardH500W500.containsEntityOfSpecifiedKindOnPosition(position, wall.getClass()));
		assertTrue(boardH500W500.containsEntityOfSpecifiedKindOnPosition(position, robot.getClass()));
	}
		
	@Test
	public void hitAllEntitiesOnPosition() {
		Robot robotOne = new Robot(Orientation.UP, new EnergyAmount(10000, EnergyUnit.WATTSECOND));
		Battery batteryOne = new Battery(new EnergyAmount(2000, EnergyUnit.WATTSECOND), 5000);
		boardH500W500.putEntity(Position.returnUniquePosition(5,17), robotOne);
		boardH500W500.putEntity(Position.returnUniquePosition(5,17), batteryOne);
		boardH500W500.hitAllEntitiesOnPosition(robotOne.getPosition());
		assertTrue(robotOne.getCapacity().hasSameValueAs(new EnergyAmount(16000,EnergyUnit.WATTSECOND)));
		assertTrue(batteryOne.getEnergy().hasSameValueAs(new EnergyAmount(2500, EnergyUnit.WATTSECOND)));
	}
	
	@Test
	public void getRandomFreePosition_NoEntitiesOnPosition() {
		boardH500W500.putEntity(Position.returnUniquePosition(5,17), robot);
		Position position = boardH500W500.getRandomFreePosition(robot);
		assertFalse(position.equals(robot.getPosition()));
		assertFalse(boardH500W500.containsEntity(position));
	}
	
	@Test (expected = NullPointerException.class)
	public void getRandomItemOnPosition_NoItemOnPosition() throws Exception {
		boardH500W500.getRandomItemOnPosition(Position.returnUniquePosition(15, 16));
	}
	
	//XXX Is dat wel ok om randomizer te testen?
	@Test 
	public void getRandomItemOnPosition_OneItemOnPosition() {
		boardH500W500.putEntity(Position.returnUniquePosition(15, 16), battery);
		Battery battery2 = new Battery(new EnergyAmount(2500, EnergyUnit.WATTSECOND),2500);
		Battery battery3 = new Battery(new EnergyAmount(2500, EnergyUnit.WATTSECOND),2500);
		boardH500W500.putEntity(Position.returnUniquePosition(15, 16), battery2);
		boardH500W500.putEntity(Position.returnUniquePosition(15, 16), battery3);
		for(int i = 1; i <= 6; i ++){
			Item bat = boardH500W500.getRandomItemOnPosition(Position.returnUniquePosition(15, 16));
			assertTrue(bat == battery ^ bat == battery2 ^ bat == battery3);
		}
	}
	
	//XXX Is dat wel ok om randomizer te testen?
	@Test
	public void hitRandomItem() {
		Battery batteryOne = new Battery(new EnergyAmount(2000, EnergyUnit.WATTSECOND), 5000);
		boardH500W500.putEntity(Position.returnUniquePosition(5,17), batteryOne);
		boardH500W500.hitRandomEntity(batteryOne.getPosition());
		assertTrue(batteryOne.getEnergy().hasSameValueAs(new EnergyAmount(2500, EnergyUnit.WATTSECOND)));
	}
	
	@Test (expected = NullPointerException.class)
	public void iterator_noEntitiesOnBoardHasNext() throws Exception {
		Iterator<Entity> iterator = boardH500W500.getElementsOnCondition(energyInspectorMin3000Ws);
		iterator.hasNext();
	}
	
	@Test (expected = NullPointerException.class)
	public void iterator_noEntitiesOnBoardNext() throws Exception {
		Iterator<Entity> iterator = boardH500W500.getElementsOnCondition(energyInspectorMin3000Ws);
		iterator.next();
	}
	
	@Test
	public void iterator_1EntityOnBoard_inspectionFailedHasNext() throws Exception {
		boardH500W500.putEntity(positionX2Y5, battery);
		Iterator<Entity> iterator = boardH500W500.getElementsOnCondition(energyInspectorMin3000Ws);
		assertFalse(iterator.hasNext());
	}
	
	@Test(expected = NoSuchElementException.class)
	public void iterator_1EntityOnBoard_inspectionFailedNext() throws Exception {
		boardH500W500.putEntity(positionX2Y5, battery);
		Iterator<Entity> iterator = boardH500W500.getElementsOnCondition(energyInspectorMin3000Ws);
		iterator.next();
	}
	
	@Test
	public void iterator_2EntitiesOnBoard_samePosition_oneSufficientEnergy_otherNot_hasNext() throws Exception {
		boardH500W500.putEntity(positionX2Y5, robot);
		boardH500W500.putEntity(positionX2Y5, battery);
		Iterator<Entity> iterator = boardH500W500.getElementsOnCondition(energyInspectorMin3000Ws);
		assertTrue(iterator.hasNext());
		assertTrue(iterator.hasNext());	
	}
	
	@Test
	public void iterator_2EntitiesOnBoard_samePosition_oneSufficientEnergy_otherNot_next() throws Exception {
		boardH500W500.putEntity(positionX2Y5, robot);
		boardH500W500.putEntity(positionX2Y5, battery);
		Iterator<Entity> iterator = boardH500W500.getElementsOnCondition(energyInspectorMin3000Ws);
		assertEquals(iterator.next(), robot);
		assertFalse(iterator.hasNext());
	}
	
	@Test
	public void iterator_2EntitiesOnBoard_differentPosition_oneSufficientEnergy_otherNot_hasNext() throws Exception {
		boardH500W500.putEntity(positionX2Y5, robot);
		boardH500W500.putEntity(Position.returnUniquePosition(5, 5), battery);
		Iterator<Entity> iterator = boardH500W500.getElementsOnCondition(energyInspectorMin3000Ws);
		assertTrue(iterator.hasNext());
		assertTrue(iterator.hasNext());
	}
	
	@Test
	public void iterator_2EntitiesOnBoard_differentPosition_oneSufficientEnergy_otherNot_next() throws Exception {
		boardH500W500.putEntity(positionX2Y5, robot);
		boardH500W500.putEntity(Position.returnUniquePosition(5, 5), battery);
		Iterator<Entity> iterator = boardH500W500.getElementsOnCondition(energyInspectorMin3000Ws);
		assertEquals(iterator.next(), robot);
		assertFalse(iterator.hasNext());
	}
	
	@Test
	public void iterator_3EntitiesOnBoard_samePosition_2SufficientEnergy_otherNot() throws Exception {
		boardH500W500.putEntity(positionX2Y5, battery);
		boardH500W500.putEntity(positionX2Y5, robot);
		boardH500W500.putEntity(Position.returnUniquePosition(5, 5), new Robot(Orientation.UP, new EnergyAmount(0.1, EnergyUnit.SUGARCUBE)));
		Iterator<Entity> iterator = boardH500W500.getElementsOnCondition(energyInspectorMin3000Ws);
		Entity temp = iterator.next();
		assertTrue(iterator.hasNext());
		assertTrue(temp != iterator.next());
		assertFalse(iterator.hasNext());
	}
	
	@Test (expected = UnsupportedOperationException.class)
	public void iterator_1EntityOnBoard_remove() throws Exception {
		boardH500W500.putEntity(positionX2Y5, robot);
		Iterator<Entity> iterator = boardH500W500.getElementsOnCondition(energyInspectorMin3000Ws);
		iterator.remove();
	}
	
}

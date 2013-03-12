package Commands;

import static org.junit.Assert.*;

import java.util.ArrayList;

import myPackage.*;
import org.junit.Before;
import org.junit.Test;

import Auxiliary.*;
import Conditions.*;

public class CommandsTest {

	private Robot robotOrientationRightEnergy3000;
	private Board board;
	private EnergyAtLeastCondition energyMin600Condition;
	private EnergyAtLeastCondition energyMin500Condition;
	private Iteration iteration1, iteration2;
	private Sequence sequence1;
	
	@Before
	public void setUp() throws Exception {
		robotOrientationRightEnergy3000 = new Robot(Orientation.RIGHT, new EnergyAmount(3000, EnergyUnit.WATTSECOND));
		board = new Board(50, 50);
		board.putEntity(Position.returnUniquePosition(5, 0), robotOrientationRightEnergy3000);
		energyMin600Condition = new EnergyAtLeastCondition(600);
		energyMin500Condition = new EnergyAtLeastCondition(500);
		iteration1 = new Iteration(energyMin500Condition, BasicCommand.MOVE);
		ArrayList<Command> seq = new ArrayList<Command>();
		seq.add(BasicCommand.TURN_CLOCKWISE);
		seq.add(iteration1);
		sequence1 = new Sequence(seq);
		iteration2 = new Iteration(energyMin600Condition, sequence1);
	}

	@Test
	public void testExecution() {
		iteration2.execute(robotOrientationRightEnergy3000);
		assertEquals(robotOrientationRightEnergy3000.getPosition(), Position.returnUniquePosition(5, 5));
		assertEquals(robotOrientationRightEnergy3000.getEnergy().getAmountInSI_unit(), 400, 0);
	}
	
	@Test
	public void hasNextBasicCommandNestedWhileLoop_TrueCase() {
		assertTrue(iteration2.containsNextStep(robotOrientationRightEnergy3000, 0));
		assertTrue(iteration2.containsNextStep(robotOrientationRightEnergy3000, 1));
		assertTrue(iteration2.containsNextStep(robotOrientationRightEnergy3000, 2));
		iteration2.execute(robotOrientationRightEnergy3000);
		assertFalse(iteration2.containsNextStep(robotOrientationRightEnergy3000, 0));
	}
	
	@Test
	public void hasNextBasicCommandNestedWhileLoop_FalseCase(){
		Robot robot = new Robot(Orientation.UP, new EnergyAmount(400, EnergyUnit.WATTSECOND));
		assertFalse(iteration2.containsNextStep(robot, 0));
		assertFalse(iteration2.containsNextStep(robot, 1));
		assertFalse(iteration2.containsNextStep(robot, 2));
	}
	
	@Test
	public void hasNextBasicCommandNestedWhileLoop_differentTrueCase(){
		Robot robot = new Robot(Orientation.UP, new EnergyAmount(700, EnergyUnit.WATTSECOND));
		EnergyAtLeastCondition cond = new EnergyAtLeastCondition(1000);
		Iteration iteration = new Iteration(cond, BasicCommand.MOVE);
		ArrayList<Command> seq = new ArrayList<Command>();
		seq.add(BasicCommand.TURN_CLOCKWISE);
		seq.add(iteration);
		Sequence sequence = new Sequence(seq);
		Iteration iteration3 = new Iteration(energyMin600Condition, sequence);
		assertTrue(iteration3.containsNextStep(robot, 1));
		assertEquals(iteration3.getStepNbOfNextStep(robot, 1),1);
	}
}

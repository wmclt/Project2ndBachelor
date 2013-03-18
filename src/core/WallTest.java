package core;



import static org.junit.Assert.*;


import org.junit.Before;
import org.junit.Test;

import Auxiliary.EnergyAmount;
import Auxiliary.EnergyUnit;
import Auxiliary.Orientation;
import Auxiliary.Position;
import Items.Battery;
import Items.Item;

public class WallTest {

	private Wall wall;
	
	@Before
	public void setUp() throws Exception {
		wall = new Wall();
	}

	@Test
	public void isObstacleFor_Wall(){
		Wall wall2 = new Wall();
		assertTrue(wall.isObstacleFor(wall2));
	}
	
	@Test
	public void isObstacleFor_Robot(){
		Robot robot = new Robot(Orientation.LEFT, new EnergyAmount(10000, EnergyUnit.WATTSECOND));
		assertTrue(wall.isObstacleFor(robot));
	}
	
	@Test
	public void isObstacleFor_Item(){
		Item battery = new Battery(new EnergyAmount(2500, EnergyUnit.WATTSECOND),5689);
		assertTrue(wall.isObstacleFor(battery));
	}
	
	@Test
	public void cloneTest(){
		Board board = new Board(5,5);
		board.putEntity(Position.returnUniquePosition(2,2), wall);
		Wall wall2 = wall.clone();
		assertEquals(wall.getPosition(),wall2.getPosition());
		assertEquals(wall.getBoard(),wall2.getBoard());
	}

}

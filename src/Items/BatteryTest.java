package Items;

import static org.junit.Assert.*;


import org.junit.Before;
import org.junit.Test;

import core.Robot;
import core.Wall;

import Auxiliary.EnergyAmount;
import Auxiliary.EnergyUnit;
import Auxiliary.Orientation;

public class BatteryTest {

	private Battery batteryEnergy2500Weight5000;
	private EnergyAmount Ws2500;
	
	@Before
	public void setUp() throws Exception {
		Ws2500 = new EnergyAmount(2500, EnergyUnit.WATTSECOND);
		batteryEnergy2500Weight5000 = new Battery(Ws2500,5000);
	}
	
	@Test
	public void constructor_CorrectCase(){
		Battery battery = new Battery(Ws2500,5000);
		assertEquals(battery.getEnergy().getAmountInSpecifiedUnit(EnergyUnit.WATTSECOND),2500,0);
		assertEquals(battery.getWeight(),5000);
	}
	
	@Test
	public void constructor_NegativeWeight(){
		Battery battery = new Battery(Ws2500,-5000);
		assertEquals(battery.getWeight(),5000);
	}
	
	@Test
	public void isObstacleFor_Wall(){
		Wall wall = new Wall();
		assertTrue(batteryEnergy2500Weight5000.isObstacleFor(wall));
	}
	
	@Test
	public void isObstacleFor_Robot(){
		Robot robot = new Robot(Orientation.LEFT,new EnergyAmount(15000, EnergyUnit.WATTSECOND));
		assertFalse(batteryEnergy2500Weight5000.isObstacleFor(robot));
	}
	
	@Test
	public void isObstacleFor_Item(){
		Battery battery = new Battery(Ws2500,5689);
		assertFalse(batteryEnergy2500Weight5000.isObstacleFor(battery));
	}
	
	@Test
	public void consumeEnergy(){
		batteryEnergy2500Weight5000.consumeEnergy(new EnergyAmount(2000, EnergyUnit.WATTSECOND));
		assertEquals(batteryEnergy2500Weight5000.getEnergy().getAmountInSpecifiedUnit(EnergyUnit.WATTSECOND),500,0);
	}
	
	@Test
	public void isLegalEnergy_bothCases(){
		
		assertTrue(batteryEnergy2500Weight5000.canHaveAsEnergy(new EnergyAmount(25, EnergyUnit.WATTSECOND)));
	}
	
	@Test
	public void cloneTest(){
		Battery bat = batteryEnergy2500Weight5000.clone();
		assertEquals(bat.getWeight(),batteryEnergy2500Weight5000.getWeight());
		assertEquals(bat.getEnergy(),batteryEnergy2500Weight5000.getEnergy());
	}
	
}

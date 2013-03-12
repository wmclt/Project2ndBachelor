package Items;

import java.util.Random;

import Auxiliary.*;
import myPackage.*;

/**
 * ...
 * 
 * @author 	Brecht J.J. Gosselé & William E.R.J. Mauclet
 * 		   	2BiR: wtk-cws (Gosselé) en cws-elt(Mauclet)
 * @version	3.0
 *
 */
public class SurpriseBox extends Item{
	
	/**
	 *...
	 *
	 * @param 	weight
	 * 			...
	 * @post	...
	 * 			|(new this).getWeight() == weight
	 */
	public SurpriseBox(int weight) {
		super(weight);
	}
	/**
	 * ...
	 * 
	 * @return	...
	 * 			|let
	 * 			|	SurpriseBox clone = new SurpriseBox(getWeight())
	 * 			|	clone.setPosition(getBoard(), getPosition())
	 * 			|in
	 * 			|	result == clone
	 */
	@Override
	public SurpriseBox clone() {
		SurpriseBox clone = new SurpriseBox(getWeight());
		clone.setPosition(getBoard(), getPosition());
		return clone;
	}

	/**
	 * ...
	 * 
	 * @effect	...
	 * 			|let
	 * 			|	ArrayList<Position> allAdjacentSquares = Position.getAllAdjacentSquares(getPosition())
	 * 			|in
	 * 			|	for each position in allAdjacentSquares :
	 * 			|		if(getBoard().isValidPosition(position))
	 * 			|			then getBoard().hitAllEntitiesOnPosition(position);
	 */
	@Override
	public void hit() {
		for(Position position : Position.getAllAdjacentSquares(getPosition())) {
			if(getBoard().isValidPosition(position)){
				getBoard().hitAllEntitiesOnPosition(position);
			}
		}
	} 
	
	/**
	 * ...
	 * 
	 * @param 	robot
	 * @effect	...
	 * 			|let
	 * 			|	Random randomGenerator = new Random()
	 * 			|	int index = randomGenerator.nextInt(3)		
	 * 			|in
	 * 			|	if(index == 0) then robot.hit()
	 * 			|	if(index == 1) then robot.teleport()
	 * 			|	if(index == 2) then addRandomItem(robot)
	 * 			|	
	 * 			|	terminate()
	 */
	public void useOn(Robot robot) {
		Random randomGenerator = new Random();
		int index = randomGenerator.nextInt(3);
		
		if(index == 0)
			robot.hit();
		if(index == 1)
			robot.teleport();
		if(index == 2){
			addRandomItem(robot);
		}
		robot.dropItem(this);
		terminate();		
	}
	
	/**
	 * ...
	 * 	
	 * @param 	robot
	 * @effect	...
	 * 			|let
	 * 			|	Random randomGenerator = new Random()
	 * 			|	int index = randomGenerator.nextInt(3)
	 * 			|	int weight = randomGenerator.nextInt(Integer.MAX_VALUE)
	 * 			|in
	 * 			|	if(index == 0) then robot.addToLoad(new SurpriseBox(weight))
	 * 			|	if(index == 1) then double energy == randomGenerator.nextInt(5001)
	 * 			|						robot.addToLoad(new Battery(energy, weight))
	 * 			|	if(index == 2) then double energy == randomGenerator.nextInt(Integer.MAX_VALUE)
	 * 			|						robot.addToLoad(new RepairKit(energy, weight))
	 * 
	 */
	public void addRandomItem(Robot robot) {
		Random randomGenerator = new Random();
		int index = randomGenerator.nextInt(3);
		int weight = randomGenerator.nextInt(Integer.MAX_VALUE);
		if(index == 0){
			robot.addToLoad(new SurpriseBox(weight));
		}
		if(index == 1){
			double energy = randomGenerator.nextInt(5001);
			robot.addToLoad(new Battery(new EnergyAmount(energy, EnergyUnit.WATTSECOND), weight));
		}
		else{
			double energy = randomGenerator.nextDouble()*Double.MAX_VALUE;
			robot.addToLoad(new RepairKit(new EnergyAmount(energy, EnergyUnit.WATTSECOND), weight));
		}
	}
}

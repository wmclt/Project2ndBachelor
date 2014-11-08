package core;


import java.io.FileNotFoundException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import Auxiliary.EnergyAmount;
import Auxiliary.EnergyUnit;
import Auxiliary.Orientation;
import Auxiliary.Position;
import Commands.*;
import Items.Item;
import Readers.FileReader;
import Readers.Translater;
import be.kuleuven.cs.som.annotate.*;;


/**
 * A class of robots, able to move and turn around given their energy-level, which must by all times
 * be lower than the robot's capacity.
 * 
 * @invar	The capacity of each robot must be a valid capacity for a robot.
 * 			|isValidCapacity(getCapacity())
 * @invar	The energy of each robot must be a valid energy-value for a robot given its capacity.
 * 			|canHaveAsEnergy(getEnergy())
 * @invar	No item can be carried by two robots at the same time.
 * 			|for each robot1, robot2 in Robot :
 * 			|	for each item instanceof Item :
 * 			|		if(robot1.carriesItem(item) && robot2.carriesItem(item))
 * 			|			then robot1 == robot2
 * @note	All aspects related to the energy are expressed in Ws!
 * 
 * @author 	Brecht Gosselé & William Mauclet
 * 			2BiR: wtk-cws (Gosselé) en cws-elt(Mauclet)
 * @version	5.2.3
 */
public class Robot extends Entity implements EnergyRelated{

	private static final EnergyUnit standardUnit = EnergyUnit.WATTSECOND;
    private static final EnergyAmount capacityLossWhenHit = new EnergyAmount(4000, getStandardUnit());
    private static final EnergyAmount minimalEnergyToMove = new EnergyAmount(500, getStandardUnit());
    private static final EnergyAmount energyToTurn = new EnergyAmount(100, getStandardUnit());
    private static final EnergyAmount energyToShoot = new EnergyAmount(1000, getStandardUnit());
    private static final EnergyAmount maxCapacity = new EnergyAmount(20000, getStandardUnit());
    
    private EnergyAmount capacity;
    private EnergyAmount energy;
    private Orientation orientation;    
    private EnergyAmount energyToMove = getMinimalEnergyToMove();
    private ArrayList<Item> load = new ArrayList<Item>();    
    private Command program = null;
    private int progressInProgram = 0;

    /**
     * Initialize this new robot with given orientation and given energy-amount.
     * 
     * @param	orientation
     * 			The initial orientation of this new robot.
     * @param	energy
     * 			The initial energy-amount of this new robot.
     * @effect	The constructor of the superclass is summoned.
     * 			|super()
     * @effect	The orientation of this new robot is set to the given orientation.
     * 			|setOrientation(orientation)
     * @effect	The capacity is set at its maximal possible value.
     * 			|setCapacity(getMaxCapacity())
     * @effect	The energy of this new robot is set to the given energy.
     * 			|setEnergy(energy)
     */
    public Robot(Orientation orientation, EnergyAmount energy) {
        super();
        setOrientation(orientation);
        setCapacity(getMaxCapacity());
        setEnergy(energy);
        
    }
    
    /**
     * Terminate this robot. If the robot was carrying items, terminate these items first.
     * 
     * @effect	If the robot was carrying items, terminate these items. 
     * 			|for each item in getLoad() :
     * 			|	item.terminate()
     * @effect	Summon the method terminate() of the superclass.
     * 			|super.terminate()
     */
    public void terminate() {
        for(Item item : getLoad())
            item.terminate();
        super.terminate();
    }

    /**
     * Return a copy of this robot with the exact same attributes, the same position and with a copy of the ArrayList load containing 
     * copies of the items stored in it.
     * 
     * @return	A copy of this robot with the exact same attributes with a copy of the ArrayList load containing copies of the items
     * 			stored in it.
     * 			|let
     * 			|	Robot clone = new Robot(getOrientation(), getEnergy())
     * 			|	clone.setPosition(getBoard(), getPosition())
     * 			|	for each item in getLoad() :
     * 			|		clone.getLoad().add(item.clone())
     * 			|in
     * 			|	result == clone
     */
    @Override
    public Robot clone() {
        Robot clone = new Robot(getOrientation(), getEnergy());
        clone.setPosition(getBoard(), getPosition());
        for(Item item : getLoad()){
            clone.getLoad().add(item.clone());
        }
        clone.updateEnergyToMove();
        return clone;
    }
    
    /**
     * Return the standard unit in which the energy of the robot will be expressed.
     */
    @Basic @Immutable @Model
    private static EnergyUnit getStandardUnit() {
    	return standardUnit;
    }
    
    /**
     * Return the amount of capacity the robot is bound to lose when the robot is hit.
     */
    @Basic @Immutable @Model
    public static EnergyAmount getCapacityLossWhenHit() {
    	return capacityLossWhenHit;
    }
    
    /**
     * Return the minimal energy needed to move one position forward (in standard Ws).
     */
    @Basic @Immutable @Model
    private static EnergyAmount getMinimalEnergyToMove() {
        return minimalEnergyToMove;
    }

    /**	
     * Return the energy needed to turn (in Ws).
     */
    @Basic @Immutable
    public static EnergyAmount getEnergyToTurn() {
        return energyToTurn;
    }

    /**
     * Return the energy needed to shoot (in Ws).
     */
    @Basic @Immutable
    public static EnergyAmount getEnergyToShoot() {
        return energyToShoot;
    }

    /**
     * Return the energy needed to move one position forward (in Ws).
     */
    @Basic
    public EnergyAmount getEnergyToMove() {
        return energyToMove;
    }

    /**
     * Return the array containing the items that the robot is carrying.
     */
    @Basic @Model
    private ArrayList<Item> getLoad() {
        return load;
    }

    /**
     * Return the capacity of this robot. The capacity is the maximum energy-level of a robot (in Ws).
     */
    @Basic
    public EnergyAmount getCapacity() {
        return capacity;
    }
    
    /**
     * Set the capacity of this robot to the given capacity.
     * 
     * @pre		The given capacity has a valid value.
     * 			|isValidCapacity(capacity)
     * @param 	capacity
     * 			The new capacity of this robot.
     * @post	The new capacity of this robot is the given capacity.
     * 			|(new this).getCapacity() == capacity
     */
    @Raw 
    public void setCapacity(EnergyAmount capacity) {
    	assert(isValidCapacity(capacity));
    	this.capacity = capacity;
    }
    
    /**
     * Return the maximal capacity an instance of the class Robot can have.
     */
    @Basic
    public static EnergyAmount getMaxCapacity(){
    	return maxCapacity;
    }

    /**
     * Return the energy-level of this robot (in Ws).
     */
    @Basic
    public EnergyAmount getEnergy() {
        return energy;
    }

    /**
     * Return the energy-level of the robot as a fraction of the robot's capacity.
     * 
     * @return	The amount of energy as a fraction of the maximal amount.
     * 			|result == ((getEnergy().getAmountInSI_unit())/(getCapacity().getAmountInSI_unit())
     */
    public double getFractionEnergy() {
        return (getEnergy().getAmountInSI_unit())/(getCapacity().getAmountInSI_unit());
    }

    /**
     * Set the energy to the given amount of energy.
     * 
     * @pre		The given energy-level must be a valid level for a robot, in view of the capacity of 
     * 			this robot.
     * 			|canHaveAsEnergy(energy)
     * @param 	energy
     * 			The new energy of the robot.
     * @post	The energy of this robot is equal to the given energy
     * 			|new.getEnergy() == energy
     */
    @Raw  @Model
    private void setEnergy(EnergyAmount energy) {
        assert canHaveAsEnergy(energy);
        this.energy = energy;
    }
    
    /**
     * Set the energy required to move to the given value of energyToMove.
     * 
     * @pre		The given energyToMove must be bigger than or equal to the minimal energy to move.
     * 			|energyToMove.compareTo(getMinimalEnergyToMove()) >= 0
     * @param 	energyToMove
     * 			The new value of the energy required to move.
     * @post	The new value of the energy required to move is the given value energyToMove.
     * 			|new.getEnergyToMove() == energyToMove
     */
    @Model
    private void setEnergyToMove(EnergyAmount energyToMove) {
    	assert(energyToMove.compareTo(getMinimalEnergyToMove()) >= 0);
    	this.energyToMove = energyToMove;
    }

    /**
     * Add the given energyAmount to the robot's energy.
     * 
     * @pre		The actual energy-level of the robot plus the given energy-amount add up lower than the robot's capacity.
     * 			|canHaveAsEnergy(energyAmount.add(getEnergy()))
     * @param	energyAmount
     * 			The amount of energy to add to the robot's energy-level.
     * @effect	The robot's energy-level is set to the sum of the robot's energy-level and the given energy amount. 
     * 			|setEnergy(getEnergy().add(energyAmount))
     */
    public void charge(EnergyAmount energyAmount) {
    	assert canHaveAsEnergy(energyAmount.add(getEnergy()));
    	setEnergy(getEnergy().add(energyAmount));
    }

    /**
     * This method fills the energy of the robot to the robot's capacity.
     * 
     * @effect	This robot's energy is set to this robot's capacity.
     * 			| setEnergy(getCapacity())
     */
    @Model 
    private void rechargeToCapacity() {
        setEnergy(getCapacity());
    }

    /**
     * Check whether the given double capacity is of positive value and lower than the maximal value the capacity of a robot can have.
     * 
     * @param 	capacity
     * 			The new capacity of the robot.
     * @return	True if and only if the given double capacity is of positive value and lower than the maximal capacity a robot can have.	
     * 			|result == (capacity.isStrictPositive() && capacity.compareTo(Robot.getMaxCapacity()) <= 0)
     */
    public static boolean isValidCapacity(EnergyAmount capacity) {
    	return (capacity.isStrictPositive() && capacity.compareTo(Robot.getMaxCapacity()) <= 0);
    }

    /**
     * Check whether the given energy is of positive value and lower than this robot's capacity.
     * 
     * @param	energy
     * 			The energy-value to check.
     * @return	True if and only if the given energy is of positive value and lower than or equal to this robot's capacity.
     * 			|result == (energy.isPositive() && energy.compareTo(getCapacity()) <= 0)
     */
    public boolean canHaveAsEnergy(EnergyAmount energy){
    	return energy.compareTo(getCapacity()) <= 0;
    }
    
    /**
     * Return the program stored in this robot.
     */
    @Basic
    public Command getProgram() {
    	return program;
    }
    
    /**
     * Store the given program in this robot.
     * 
     * @post	The program stored in this robot is the given command. 
     * 			|new.getProgram() == command
     * @post	The progress in the program is set to zero.
     * 			|new.getProgressInProgram() == 0
     */
    @Model
    private void setProgram(Command command) {
    	program = command;
    	setProgressInProgram(0);
    }
    
    /**
     * Get the number of times the program stored in this robot has been executed for one step.
     */
    @Basic
    private int getProgressInProgram() {
    	return progressInProgram;
    }
    
    /**
     * Set the progress in the program of the robot to the given integer.
     * @param 	step
     * 			The new integer to which the progress is to be set.
     * @post	The progress is equal to the given integer.
     * 			|new.getProgressInProgram() == step
     */
    @Model
    private void setProgressInProgram(int step) {
    	assert step >= 0;
    	progressInProgram = step;
    }

    /**
     * Return the orientation of this robot.
     */
    @Basic
    public Orientation getOrientation() {
        return orientation;
    }

    /**
     * This method changes the orientation of the robot to the given orientation.
     * 
     * @param	orientation
     * 			The new orientation of the robot.
     * @post	The robot's new orientation is the given orientation. If a value is given that isn't a possible value of the 
     * 			enum Orientation, the robot' orientation is default up. 
     * 			|if(!Orientation.class.isInstance(orientation))
     * 			|	then new.getOrientation() == Orientation.UP
     * 			|else
     * 			|	then new.getOrientation() == orientation	
     */
    @Raw @Model
    private void setOrientation(Orientation orientation) {
        if(!Orientation.class.isInstance(orientation))
            this.orientation = Orientation.UP;
        this.orientation = orientation;
    }

    /**
	 * ...
	 * 
	 * @param 	clazz
	 * 			...
	 * @return	...
	 * 			|let
	 * 			|	HashSet<Entity> carryings = new HashSet<Entity>()$
	 * 			|	for each entity in getLoad() :
	 * 			|		if(clazz.isInstance(entity) then carryings.add(entity.clone())
	 * 			|in
	 * 			|	result == carryings
	 */
	public HashSet<Entity> getCarryingsOfSpecifiedKindInRobot(Class<? extends Entity> clazz) {
		HashSet<Entity> carryings = new HashSet<Entity>();
		for(Entity entity: getLoad()){
			if(clazz.isInstance(entity)){
				carryings.add(entity);
			}
		}
		return carryings;
	}

    /**
     * Pick up the given item.
     * 
     * @param 	item
     * 			The item for the robot to pick up.
     * @effect	The item is removed from the board on which this robot and item are.
     * 			|getBoard().removeEntity(item)
     * @effect	The item is added to this robot's ArrayList load.
     * 			|addToLoad(item)
     * @throws	NullPointerException
     * 			The given item is null.	
     * 			| item == null
     * @throws	NullPointerException
     * 			This robot or the given item are not located on any board.
     * 			|getBoard() == null || item.getBoard() == null
     * @throws	IllegalStateException
     * 			This robot and the given item are not on the same position or on the same board.
     * 			| (item.getPosition() != this.getPosition()) || (item.getBoard() != this.getBoard())
     */
    public void pickUp(Item item) throws NullPointerException, IllegalStateException {
        //Testing if getBoard().isTerminated() is futile: an entity cannot be standing on a terminated board.
        if(item == null)
        	throw new NullPointerException("Item is null");
        if(getBoard() == null || item.getBoard() == null)
            throw new NullPointerException("No board!");
        if((item.getBoard() != this.getBoard()) || item.getPosition() != this.getPosition() )
            throw new IllegalStateException("Not on the same position!");
        //Pick up.
        getBoard().removeEntity(item);
        addToLoad(item);
    }

    /**
     * Add the given item to the robot's ArrayList load.
     * 
     * @pre		The given item is not null.
     * 			|item != null
     * @param 	item
     * 			The item to be added to the robot's load.
     * @effect	The item is added to the ArrayList load.
     * 			|getLoad().add(item)
     * @effect	The ArrayList load is sorted from heaviest (index 0) to lightest.
     * 			|sortLoad()
     * @effect	The value of the energy required to move of this robot is updated.
     * 			|updateEnergyToMove()
     */
    public void addToLoad(Item item) {
    	assert item!= null;
        getLoad().add(item);
        sortLoad();
        updateEnergyToMove();
    }

    /**
     * Drop the given item. If this robot is on a board, the given item is given the same position as this robot. Otherwise
     * the given item's new board is null and it is not carried by a robot.
     * 
     * @param 	item
     * 			The item to be dropped.
     * @effect	The given item is removed from this robot's ArrayList load. 
     * 			|getLoad().remove(item)
     * @effect	The energyToMove of the robot is updated.
     * 			|updateEnergyToMove()
     * @effect	If this robot is on a board, the given item is given the same position as this robot.
     * 			|if(getBoard() != null) 
     * 			|	then getBoard().putEntity(getPosition(), item)
     * @throws	NullPointerException
     * 			The item is null.
     * 			|item == null
     * @throws 	IllegalArgumentException
     * 			This robot does not carry the given item.
     * 			|!carriesItem(item)
     */
    public void dropItem(Item item) throws IllegalArgumentException, NullPointerException {
    	if(item == null)
    		throw new NullPointerException("Item is null.");
        if(!carriesItem(item))
            throw new IllegalArgumentException("The robot doesn't carry the given item.");
        getLoad().remove(item);
        updateEnergyToMove();
        if(getBoard() != null)
            getBoard().putEntity(getPosition(), item);
    }
    
    /**
     * This method checks whether this robot carries the given item.
     * 
     * @param 	item
     * 			The item that must be checked if it is carried by this robot;
     * @return	A boolean telling whether this robot carries the given item.
     * 			|let
     * 			|	boolean trueOrFalse = false
     * 			|	for each other in getLoad() :
     * 			|		if(other == item)
     * 			|			trueOrFalse = true
     * 			|in
     * 			|	result == trueOrFalse
     */
    public boolean carriesItem(Item item){
    	for(Item other : getLoad())
    		if(other == item)
    			return true;
    	return false;
    }
    
    
    /**
     * This method sorts the ArrayList load of the robot in order to have the items sorted from heaviest to lightest, 
     * with the heaviest item stored on index 0.
     * 
     * @post	The items are sorted from heaviest to lightest, with the heaviest item stored on index 0.
     * 			|for each index in getLoad().size() :
     * 			|	(new.getLoad().get(index + 1) == null) || (new.getLoad().get(index).getWeight() >= new.getLoad().get(index + 1).getWeight())
     */
    @Model
    private void sortLoad() {
        int i,j,newValue;
        for(i = load.size() - 2 ; i >= 0 ; i--) {
            newValue = load.get(i).getWeight();
            j = i;
            Item temp = load.get(i);
            while(j < load.size() - 1 && load.get(j + 1).getWeight() > newValue){
                load.set(j, load.get(j + 1));
                j++;
            }
            load.set(j,temp);
        }
    }

    /**
     * Return the i'th heaviest item in the robot's ArrayList load.
     * 
     * @param 	i
     * 			The degree of relative heaviness of the seeked item in the robot's load.
     * @return	The i'th heaviest item in the robot's ArrayList load.
     * 			|let
     * 			|	int strictBigger = 0
     * 			|	for each item in getLoad() :
     * 			|		if(item.getWeight() > getLoad().get(i - 1))
     * 			|			then strictBigger++
     * 			|in
     * 			|	strictBigger <= i-2
     * 			|	result == getLoad().get(i - 1)
     * @throws	IllegalArgumentException
     * 			The given index i is bigger than the number of items carried by the robot.
     * 			|i > getLoad().size()-1
     */
    public Item getHeaviestItem(int i) throws IllegalArgumentException {
        if(i > getLoad().size())
            throw new IllegalArgumentException("The given index " +i+" is bigger than the number of items " +
                "carried by the robot!");
        return getLoad().get(i - 1).clone();
    }
    
    /**
     * Make the robot use the given item.
     * 
     * @param 	item
     * 			The given item the robot needs to use.
     * @effect	The item is terminated, it is removed from the robot's load..
     * 			|if(item.isTerminated()
     * 			|	then getLoad().remove(item)
     * @effect	The item is used.
     * 			|item.useOn(this)
     * @throws	IllegalArgumentException
     * 			The given item is terminated.
     * 			|item.isTerminated()
     * @throws	IllegalArgumentException
     * 			The given  item is not in this robot's ArrayList load.
     * 			|!getLoad().contains(item)
     */
    public void use(Item item) throws IllegalArgumentException{
    	if(item.isTerminated()) {
            getLoad().remove(item);
            throw new IllegalArgumentException("Item is terminated!");
        }
        if(!getLoad().contains(item))
            throw new IllegalArgumentException("No such item found in robot's load.");
        item.useOn(this);
        
    }

    /**
     * Return the i'th position in the given direction of the robot.
     * 
     * @pre		getBoard() != null
     * @return	If the i'th position in the given direction of the robot is not out of bounds, return it.
     * 			|let 
     * 			|	Position newSquare = getPosition()
     * 			|in
     * 			If the given orientation is up, the returned position has the robot's coordinates, except that the
     * 			y-coordinate is decreased by one.
     * 			|	if(orientation == Orientation.UP) then newSquare = Position.returnUniquePosition(getPosition().getX(),getPosition().getY() - i)
     * 			If the given orientation is right, the returned position has the robot's coordinates, except that the
     * 			x-coordinate is increased by one.
     * 			|	if(orientation == Orientation.RIGHT) then newSquare = Position.returnUniquePosition(getPosition().getX() + i,getPosition().getY())
     * 			If the given orientation is down, the returned position has the robot's coordinates, except that the
     * 			y-coordinate is increased by one.
     * 			|	if(orientation == Orientation.DOWN) then newSquare = Position.returnUniquePosition(getPosition().getX(),getPosition().getY() + i)
     *			If the given orientation is left, the returned position has the robot's coordinates, except that the
     * 			x-coordinate is decreased by one.
     * 			|	if(orientation == Orientation.LEFT) then newSquare = Position.returnUniquePosition(getPosition().getX() - i,getPosition().getY())
     * 			|
     * 			|	result == newSquare
     * @return	If the i'th square in the given direction is out of bounds, return null.
     * 			|if(orientation == Orientation.UP) then if(!getBoard().isValidPosition(returnUniquePosition(getPosition().getX(), getPosition().getY()-i))) 
     * 			|	then result == null
     * 			|if(orientation == Orientation.RIGHT) then if(!getBoard().isValidPosition(returnUniquePosition(getPosition().getX()+1, getPosition().getY()))) 
     * 			|	then result == null
     * 			|if(orientation == Orientation.DOWN) then if(!getBoard().isValidPosition(returnUniquePosition(getPosition().getX(), getPosition().getY()+i))) 
     * 			|	then result == null
     * 			|if(orientation == Orientation.LEFT) then if(!getBoard().isValidPosition(returnUniquePosition(getPosition().getX()-1, getPosition().getY()))) 
     * 			|	then result == null
     */
    public Position getSquareInDirection(int i, Orientation orientation){
        assert getBoard() != null;
        Position newSquare = getPosition();
        if(orientation == Orientation.UP) {
            newSquare = Position.returnUniquePosition(getPosition().getX(),getPosition().getY() - i);
        }
        if(orientation == Orientation.RIGHT) {
            newSquare = Position.returnUniquePosition(getPosition().getX() + i,getPosition().getY());
        }
        if(orientation == Orientation.DOWN) {
            newSquare = Position.returnUniquePosition(getPosition().getX(),getPosition().getY() + i);
        }
        if(orientation == Orientation.LEFT) {
            newSquare = Position.returnUniquePosition(getPosition().getX() - i,getPosition().getY());
        }
        if(!getBoard().isValidPosition(newSquare))
            newSquare = null;
        return newSquare;
    }
    
    /**
     * Return all the positions in front of the robot till the end of the board.
     * 
     * @pre		The robot is placed on a board.
     * 			|getBoard() != null
     * @return	An ArrayList containing all the positions in front of the robot.
     * 			|let
     * 			|	ArrayList<Position> allPositions = new ArrayList<Position>()
     * 			|	int index = 0
     * 			|	if(robot.getOrientation() == Orientation.UP) 	 then index = (int) robot.getPosition().getY()
     * 			|	if(robot.getOrientation() == Orientation.RIGHT)	 then index = (int) (robot.getBoard().getWidth() - robot.getPosition().getX())
     * 			|	if(robot.getOrientation() == Orientation.DOWN) 	 then index = (int) (robot.getBoard().getHeight() - robot.getPosition().getY())
     * 			|	if(robot.getOrientation() == Orientation.LEFT) 	 then index = (int) robot.getPosition().getX()
     * 			|
     * 			|	for each i in 1..index-1 :
     * 			|		allPositions.add(getSquareInDirection(i, robot.getOrientation()))
     * 			|in
     * 			|	result == allPositions
     */
    public ArrayList<Position> getAllSquaresInDirection() {
    	assert getBoard() != null;    	
    	ArrayList<Position> allPositions = new ArrayList<Position>();
    	int index = 0;
    	
    	if(getOrientation() == Orientation.UP)
    		index = (int) getPosition().getY();
    	if(getOrientation() == Orientation.RIGHT)
    		index = (int) (getBoard().getWidth() - getPosition().getX());
    	if(getOrientation() == Orientation.DOWN)
    		index = (int) (getBoard().getHeight() - getPosition().getY());
    	if(getOrientation() == Orientation.LEFT)
    		index = (int) getPosition().getX();
    	
    	for(int i = 1; i <= index ; i++) {
    		allPositions.add(getSquareInDirection(i, getOrientation()));
    	}
    	return allPositions;
    }

    /**
     * Return the total weight of all the items carried by the robot in kilograms rounded down. (nl: 'naar beneden afgerond')
     * 
     * @return	The total weight of all the items carried by the robot in kilograms rounded down. (nl: 'naar beneden afgerond') 
     * 			|let 
     * 			|	BigInteger loadInG = BigInteger.ZERO
     * 			|	for each item in getLoad() :
     * 			|		loadInG = BigInteger.valueOf(item.getWeight() + loadInG.longValue())
     * 			|in
     * 			|	result == (int) Math.floor(loadInG.doubleValue()/1000)
     */
    @Model
    private int getLoadInKg() {
        BigInteger loadInG = BigInteger.ZERO;
        for(Item item : getLoad()) {
            loadInG = BigInteger.valueOf(item.getWeight() + loadInG.longValue());
        }
        return (int) Math.floor(loadInG.doubleValue()/1000); 
    }
    
    /**
     * Update the value of energyToMove.
     * 
     * @post	The value of energyToMove is updated, i.e. let it be equal to the minimal energy to move plus 50 Ws per
     * 			kilogram of items carried by the robot.
     * 			|new.getEnergyToMove() == getMinimalEnergyToMove().add(new EnergyAmount(getLoadInKg() * 50, EnergyUnit.WATTSECOND)).getAmountInSpecifiedUnit(getStandardUnit())
     */
    @Model
    private void updateEnergyToMove() {
        setEnergyToMove(getMinimalEnergyToMove().add(new EnergyAmount(getLoadInKg() * 50, EnergyUnit.WATTSECOND)));
    }
    
    /**
     * Transfer the items of this robot to the given other robot.
     * 
     * @param 	other	
     * 			The other robot.
     * @effect	All the pointers to the items carried by robot are copied to the other robot's load.
     * 			|for each item in getLoad() :
     * 			|	other.addToLoad(item)
     * @effect	The load of this robot is cleared.
     * 			|getLoad().clear()
     * @throws 	IllegalArgumentException
     * 			The two robots are not on the same board.
     * 			|other.getBoard() != getBoard()
     * @throws 	IllegalArgumentException
     * 			The two robots are not next to each other.
     * 			|Position.getDistance(getPosition(), other.getPosition()) != 1
     */
    @Raw
    public void transferItems(Robot other) throws IllegalArgumentException{
    	if(other.getBoard() != getBoard())
    		throw new IllegalArgumentException("The two robots are not on the same board.");
    	if(Position.getDistance(getPosition(), other.getPosition()) != 1)
    		throw new IllegalArgumentException("The two robots are not next to each other.");
    	else
    		for(Item item: getLoad()){
    			other.addToLoad(item);
    		}
    	getLoad().clear();
    }
    
    /**
     * Make the robot move one position forward.
     * 
     * @pre		The robot has got enough energy to move one position forward.
     * 			|getEnergy().compareTo(getEnergyToMove()) != -1
     * @post	The robot has moved one position forward.
     * 			|new.getPosition() == getSquareInDirection(1, getOrientation())
     * @post	The robot's energy-level has decreased by the energy the robot needs to move one position forward.
     * 			|new.getEnergy() = (getEnergy().substract(getEnergyToMove())).getAmountInSpecifiedUnit(getStandardUnit())
     * @throws  NullPointerException
     * 			The robot is not on a board.
     * 			|getBoard() == null
     * @throws 	IllegalStateException
     * 			The new position-to-be of the robot is out of the robot's board's bounds.
     *          |getSquareInDirection(1,getOrientation()) == null
     * @throws 	IllegalArgumentException
     *          The new position-to-be of the robot contains an obstacle to the robot.
     *          |getBoard().containsObstacle(getSquareInDirection(1,getOrientation()),this)
     */
    public void move() throws NullPointerException, IllegalStateException, IllegalArgumentException{
        assert (getEnergy().compareTo(getEnergyToMove()) != -1);
        if(getBoard() == null)
            throw new NullPointerException("404: no board found!");
        Position target = getSquareInDirection(1,getOrientation());
        if(target == null)
            throw new IllegalStateException("Position in front of the robot is out-of-bounds!");
        getBoard().putEntity(target, this);
        setEnergy(getEnergy().substract(getEnergyToMove()));
    }

    /**
     * This method makes the robot turn 90° in clockwise direction. The robot's energy-level is decreased by the energy needed
     * to turn.
     * 
     * @pre		The robot's energy-level is high enough to consume the getEnergyToTurn() needed to turn.
     * 			|getEnergy().compareTo(getEnergyToTurn()) != -1
     * @post	The new orientation is 90° in clockwise direction compared to the previous orientation.
     * 			|new.getOrientation() == Orientation.turnRight(getOrientation())
     * @post	The robot's energy-level is decreased by getEnergyToTurn().
     * 			|new.getEnergy() == getEnergy().substract(getEnergyToTurn()).getAmountInSpecifiedUnit(getStandardUnit())
     */
    public void turnClockwise() {
        assert (getEnergy().compareTo(getEnergyToTurn()) != -1);
        setOrientation(Orientation.turnRight(getOrientation()));
        setEnergy(getEnergy().substract(getEnergyToTurn()));
    }

    /**
     * This method makes the robot turn 90° in counterclockwise direction. The robot's energy-level is decreased by
     * the energy needed to turn.
     * 
     * @pre		The robot's energy-level is high enough to consume the getEnergyToTurn() needed to turn.
     * 			|getEnergy().compareTo(getEnergyToTurn()) != -1
     * @post	The new orientation is 90° in counterclockwise direction compared to the previous orientation.
     * 			|new.getOrientation() == Orientation.turnLeft(getOrientation())
     * @post	The robot's energy-level is decreased by getEnergyToTurn().
     * 			|new.getEnergy() == getEnergy().substract(getEnergyToTurn()).getAmountInSpecifiedUnit(getStandardUnit())
     */
    public void turnCounterclockwise() {
    	assert (getEnergy().compareTo(getEnergyToTurn()) != -1);
        setOrientation(Orientation.turnLeft(getOrientation()));
        setEnergy(getEnergy().substract(getEnergyToTurn()));
    }
    
    /**
     * This method puts the robot on another random square of the board.
     * 
     * @effect	The robot's put on a random new position.
     * 			|getBoard().putEntity(getBoard().getRandomFreePosition(this), this)
     * @throws	NullPointerException 
     * 			The robot isn't placed on a board.
     * 			|getBoard() == null
     */
    public void teleport() throws NullPointerException{
    	if(getBoard() == null)
    		throw new NullPointerException("Robot tried to teleport, but wasn't placed on a board. Too bad! ");
    	getBoard().putEntity(getBoard().getRandomFreePosition(this), this);
    }

    /**
     * Return whether the given entity can be on the same position as the robot.
     * 
     * @pre		The entity is not null.
     * 			|entity != null
     * @param	entity
     * 			The entity that need be checked whether it would form an obstacle to the robot.
     * @return	If the given entity is an instance of the class robot or wall, the result is true. Otherwise the 
     * 			result is false.
     * 			|result == entity.isObstacleForRobot(this)
     */
    public boolean isObstacleFor(Entity entity){
        assert(entity != null);
        return entity.isObstacleForRobot(this);
    }
    

	@Override
	public boolean isObstacleForRobot(Robot robot) {
		return robot != this;
	}

	@Override
	public boolean isObstacleForItem(Item item) {
		return false;
	}

    /**
     * The robot shoots its laser in the direction it is facing, consuming 1000 Ws of energy.
     * 
     * @pre		The robot must have enough energy to fire off its laser.
     * 			|getEnergy().compareTo(getEnergyToShoot()) != -1
     * @effect	If the robot is on a board, a random entity, on the closest position in the direction faced by the robot that
     * 			contains entities on it, is removed from the board and terminated.
     * 			|getBoard().hitRandomEntity(getBoard().returnFirstOccupiedPositionInDirection(this))
     * @post	The energy of the robot is decreased with getEnergyToShoot()
     * 			|new.getEnergy().hasSameValueAs(getEnergy().substract(getEnergyToShoot()))
     * @throws	NullPointerException
     * 			There is no entity at all in the direction the robot is facing.
     * 			|for each position in getAllSquaresInDirection(robot)) :
     * 			|	!position.containsEntity()
     */
    public void shoot() throws NullPointerException {
        assert (getEnergy().compareTo(getEnergyToShoot()) != -1);
        setEnergy(getEnergy().substract(getEnergyToShoot()));
        if(getBoard() != null) {
        	getBoard().hitRandomEntity(getBoard().returnFirstOccupiedPositionInDirection(this));
        }        
    }
    
    /**
     * Make the robot undergo the effects of being hit by a laser.
     * 
     * @post	The robot's capacity has either diminished by 4000 but is still bigger than zero
     * 			|new.getCapacity().hasSameValueAs(getCapacity().substract(getCapacityLossWhenHit()))
     * 			|&& new.getCapacity().isStrictlyPositive() == true
     * 			Or the robot is terminated.
     * 			|(new.isTerminated())
     * @effect	If the new capacity is lower than the energy, the energy is set to the new capacity
     * 			|if(!canHaveAsEnergy(getEnergy()))
     * 			|	then setEnergy(getCapacity())
     */
    @Raw
    public void hit(){
    	if(getCapacity().compareTo(getCapacityLossWhenHit()) <= 0)
    		terminate();
    	else
    		setCapacity(getCapacity().substract(getCapacityLossWhenHit()));
    	if(!canHaveAsEnergy(getEnergy()))
    		setEnergy(getCapacity());
    }
    
    /**
     * Return the minimal energy required for this robot to reach the given position.
     * 
     * @param	position
     * 			The target-position for which this method calculates whether this robot can reach it, and with what minimal energy-
     * 			consumption.
     * @return	If the robot does have enough energy to reach the given position, the returned result is the minimal energy required for the 
     *  		robot to reach that position.
     * 			|let 
     * 			|	ArrayList<Robot> range = getExtendedRange(this, null)
     * 			|in
     * 			|	for each robot in range :
     * 			|		if(robot.getPosition() == position)
     * 			|			returnValue.hasSameValueAs(getEnergy().substract(robot.getEnergy())
     * 			|
     * 			|	result == returnValue 
     * @throws	IllegalStateException
     * 			The robot is not placed on a board. The message of the IllegalStateException is "-1".
     * 			|getBoard() == null
     * @throws	IllegalStateException
     * 			The given position is theoretically not reachable because of obstacles. The message of the IllegalStateException is "-1".
     * 			|!getBoard().isPositionReachableForRobot(position, this)
     * @throws	IllegalStateException
     * 			The robot hasn't got enough energy to reach the given position. The message of the IllegalStateException is "-1".
     * 			|let 
     * 			|	ArrayList<Robot> range = getExtendedRange(this, null)
     * 			|in
     * 			|	!range.contains(position
     * @throws	IllegalArgumentException
     * 			The given position if out of the robot's board's bounds.
     * 			|!getBoard().isValidPosition(position)
     */
    public EnergyAmount getEnergyRequiredToReach(Position position) throws IllegalStateException, IllegalArgumentException{
        if(getBoard() == null) 
            throw new IllegalStateException("-1");
        if(!getBoard().isPositionReachableForRobot(position, this))
        	throw new IllegalStateException("-1");
        	
        //Find the total range of this robot.
        ArrayList<Robot> range = getExtendedRange(this, null);

        //Find out whether the given position is within the range of this robot, if so, return how much energy it would minimally 
        // cost this robot to get there.
        for(Robot robot : range){
            if(robot.getPosition() == position) {
                return getEnergy().substract(robot.getEnergy());
            }
        }
        //The robot hasn't got energy to reach the given position (=> the given position is not within the range of this robot).
        throw new IllegalStateException("-1");
    }

    /**
     * Move both given robots the closest possible to each other.
     * If both robots haven't got enough energy to end up next to each other, a best effort should be made so that the final distance is minimal. 
     * If multiple final combinations of positions are possible for the two given robots, the least energy-consumptive combination shall be selected.
     *
     * @param 	robot1
     * 			One of the two robots that need to move next to each other.
     * @param 	robot2
     * 			One of the two robots that need to move next to each other.
     * @effect	Combinations of positions robot1 and robot2 can respectively reach with their current energy-level are made. Each position, and the state 
     * 			wherein robot1 or robot2 would be if they reached this position via the least energy-consumptive ways, are stored in a new robot. Within the 
     * 			collection of combinations, a selection has been made so as to keep (one of) the combination with the smallest end-distance between positions 
     * 			and sum of energy-consumptions, in that order. The robots are then moved to their respective positions in the selection.
     * 			|let
     * 			|	HashMap<Robot, ArrayList<BasicCommand>> commandsRange = new HashMap<Robot, ArrayList<BasicCommand>>()
     * 			|	commandsRange.put(robot1, new ArrayList<BasicCommand>())
     * 			|	commandsRange.put(robot2, new ArrayList<BasicCommand>())
     * 			|	ArrayList<Robot> range1 = getExtendedRange(robot1,commandsRange)
     * 			|	ArrayList<Robot> range2 = getExtendedRange(robot2, commandsRange)
     * 			|	ArrayList<Robot> optimalCombination = findOptimalCombination(range1, range2, robot1, robot2)
     * 			|in
     * 			|	Sequence ordersRobot1 = new Sequence(commandsRange.get(optimalCombination.get(0)));
     *   		|	Sequence ordersRobot2 = new Sequence(commandsRange.get(optimalCombination.get(1)));
     *   		|	ordersRobot1.execute(robot1);
     *  		|	ordersRobot2.execute(robot2);
     * @throws	NullPointerException
     * 			At least one of the given robots is null.
     * 			|robot1 == null || robot2 == null
     * @throws	IllegalArgumentException
     * 			The two robots are not located on the same board.
     * 			|robot2.getBoard()  != robot1.getBoard() || robot1.getBoard() == null || robot2.getBoard() == null
     */
    public static void moveNextTo(Robot robot1, Robot robot2) throws NullPointerException, IllegalArgumentException{
    	//Standard assertions
        if(robot1 == null || robot2 == null)
        	throw new NullPointerException("404: robot(s) null");
        //The three test in the next if-statement are needed.
        if(robot2.getBoard()  != robot1.getBoard() || robot1.getBoard() == null || robot2.getBoard() == null)
            throw new IllegalArgumentException("Robots are not on the same board!");

        //Make a new HashMap linking robots (with different positions) to an ArrayList of basic commands.
        HashMap<Robot, ArrayList<Command>> commandsRange = new HashMap<Robot, ArrayList<Command>>();
        commandsRange.put(robot1, new ArrayList<Command>());
        commandsRange.put(robot2, new ArrayList<Command>());

        //Get the (extended) ranges of both robots.
        ArrayList<Robot> range1 = getExtendedRange(robot1, commandsRange);
        ArrayList<Robot> range2 = getExtendedRange(robot2, commandsRange);

        //Get an ArrayList containing destinations for respectively robot1 and robot2. This ArrayList shall be already selected/minimized
        //for a minimal distance between the destinations and a minimal sum of energy-consumptions for robot1 and robot2 to get to their respective 
        //destinations. (see documentation of findOptimalCombination() for more information.)
        ArrayList<Robot> optimalCombination = findOptimalCombination(range1, range2, robot1, robot2);

        //Execute the command linked to the found robots.
        Sequence ordersRobot1 = new Sequence(commandsRange.get(optimalCombination.get(0)));
        Sequence ordersRobot2 = new Sequence(commandsRange.get(optimalCombination.get(1)));
        ordersRobot1.execute(robot1);
        ordersRobot2.execute(robot2);
    }

    /**
     *  This method calculates the range of this robot with new robots that correspond to new positions within (energetical) reach of this robot. Each of these new robots has
     *  an orientation and an energy-level, corresponding to the orientation and energy-level this robot would have if it moved to the position of the new robot.
     * 
     * @param 	robot
     * 			The robot for which this method calculates the extended range.
     * @param 	commandsRange
     * 			A HashMap that links the robots in range, to a list of basic commands that the given robot would need to perform in order to get to the positions of the 
     * 			robots in range.
     * @return	An ArrayList with robots, whose positions are all the positions the given robot can attain with the given robot's energy-level.
     * 			|let
     * 			|	ArrayList<Robot> range = new ArrayList<Robot>()
     * 			|in
     * 			|	for each otherRobot1 in range :
     * 			|		robot.getEnergyRequiredToReach(otherRobot.getPosition()) != -1 && robot.getEnergyRequiredToReach(otherRobot.getPosition()) != -2
     * 			|
     * 			|	result == range
     * @note	IMPORTANT: The size of the range of the robot is expected and meant to be smaller than Integer.MAX_VALUE.
     */
    @Model
    private static ArrayList<Robot> getExtendedRange(Robot robot, HashMap<Robot, ArrayList<Command>> commandsRange){
        ArrayList<Robot> range = new ArrayList<Robot>();
        range.add(robot);
        int oldSize = 0;
        while(range.size() != oldSize) {
            int actualSize = range.size();
            for(int i = oldSize; i < actualSize; i++) {
                range.addAll(getFreeAdjacentSquares(range.get(i),range,commandsRange));
            }
            oldSize = actualSize;
        }
        return range;
    }
    
    /**
     * This method returns a selection of the least distanced and least energy-consumptive (to attain for the respective robots) combinations of all possible
     * combinations of positions from the given range1 and range2.
     * 
     * @param 	robot1
     * 			One of the two robots that need to move next to each other. (see moveNextToWithObstacles(Robot robot1, Robot robot2))
     * @param 	robot2
     * 			One of the two robots that need to move next to each other. (see moveNextToWithObstacles(Robot robot1, Robot robot2))
     * @param 	range1
     * 			The range of robot1 expressed in robots.
     * @param 	range2
     * 			The range of robot2 expressed in robots.
     * @return	The result of this method is an ArrayList containing on memory-place "0" a robot whose position is in range of robot1 and on memory-place "1" a robot whose
     * 			position is in range of robot2.
     *			|result == optimalCombination
     * @return	This ArrayList contains a combination of robots with a minimal distance in-between and a minimal sum of energy-consumptions for both robots robot1 and robot2
     * 			to attain the positions of their respective robots in their respective ranges, in that order.
     * 			|result == optimalCombination
     * 			|	&&
     *			|let
     *			|	double minimalEnergy = robot1.getEnergyRequiredToReach(optimalCombination.get(0).getPosition()) 
     *			|							+robot2.getEnergyRequiredToReach(optimalCombination.get(1).getPosition())
     *			|	double minimalDistance = Position.getDistance(optimalCombination.get(0),optimalCombination.get(1))
	 *			|in
     *			|	for each robotRange1 in range1 :
     *			|		for each robotRange2 in range2 :
     *			|			long distance = Position.getDistance(robotRange1.getPosition(), robotRange2.getPosition())
     *			|			double energy == (robot1.getEnergy() - robotRange1.getEnergy()) + (robot2.getEnergy() - robotRange2.getEnergy())
     *          |
     *         	|			(minimalDistance < distance) || (minimalDistance == distance && minimalEnergy <= energy)
     */
    @Model
    private static ArrayList<Robot>  findOptimalCombination(ArrayList<Robot> range1, ArrayList<Robot> range2, Robot robot1, Robot robot2){
    	long minimalDistance = Long.MAX_VALUE;
    	EnergyAmount minimalEnergy = new EnergyAmount(Double.MAX_VALUE, getStandardUnit());
    	ArrayList<Robot> optimalCombination = new ArrayList<Robot>();
        for(Robot robotRange1 : range1){
            for(Robot robotRange2 : range2){
                long distance = Position.getDistance(robotRange1.getPosition(), robotRange2.getPosition());
                if(distance != 0 && distance <= minimalDistance) {
                	EnergyAmount energy = (robot1.getEnergy().substract(robotRange1.getEnergy())).add((robot2.getEnergy().substract(robotRange2.getEnergy())));
                	if(distance < minimalDistance) {
                		minimalDistance = distance;
                		minimalEnergy = energy;
                		updateOptimalCombination(optimalCombination,robotRange1, robotRange2);
                	}
                	if((distance == minimalDistance) && (energy.compareTo(minimalEnergy) == -1)) {
                			minimalEnergy = energy;
                			updateOptimalCombination(optimalCombination, robotRange1, robotRange2);
                	}
                }
            }
        }
        return optimalCombination;
    }
    

	/**
	 * This method clears the ArrayList optimalCombination and adds the two given robots to it.
	 * 
	 * @param 	optimalCombination
	 * 			The ArrayList that need be updated.
	 * @param 	robotRange1
	 * 			A robot that must be added to the ArrayList.
	 * @param 	robotRange2
	 * 			A robot that must be added to the ArrayList.
	 * @post	The ArrayList optimalCombination contains robotRange1 on its first memory-place and robotRange2 on its second.
	 * 			|(new optimalCombination).get(0) == robotRange1 && (new optimalCombination).get(1) == robotRange2
	 */
	private static void updateOptimalCombination(ArrayList<Robot> optimalCombination, Robot robotRange1,Robot robotRange2) {
		optimalCombination.clear();
		optimalCombination.add(robotRange1);
		optimalCombination.add(robotRange2);
	}
 
    /**
     * For a given robot, return which of the squares adjacent to that robot can be reached by the given robot. Return an ArrayList containing new robots, which
     * have the values the given robot would have if it reached the position of the new robot. Also, if the formal argument commandsRange isn't null, update it.
     * 
     * @param 	robot
     * 			The robot whose adjacent squares we want to know are reachable by robot, and in what state would robot than be if it reached one of them.
     * @param 	range
     * 			An ArrayList of robots that needs to be updated with the eventual new robots on the adjacent squares.
     * @param 	commandsRange
     * 			A HashMap that links the new robots to the ArrayList of basic commands the original robot must follow in order to get to the position of the new robot.
     * 			(see moveNextToWithObstacles(Robot robot1, Robot robot2))
     * @return	An ArrayList wherein the new robots positioned on free adjacent squares of the given robot, are stored.
     * 			|let
     * 			|	ArrayList<Robot> robotsOnFreeAdjacentSquares = new ArrayList<Robot>()
     * 			|	getOneSquareAndUpdateRobotsOnFreeAdjacentSquaresAndUpdateCommandsRange(robot, range, commandsRange, robotsOnFreeAdjacentSquares, robot.getOrientation());
     *	 		|	getOneSquareAndUpdateRobotsOnFreeAdjacentSquaresAndUpdateCommandsRange(robot, range, commandsRange, robotsOnFreeAdjacentSquares, Orientation.turnLeft(robot.getOrientation()));
     *   		|	getOneSquareAndUpdateRobotsOnFreeAdjacentSquaresAndUpdateCommandsRange(robot, range, commandsRange, robotsOnFreeAdjacentSquares, Orientation.turnRight(robot.getOrientation()));
     *   		|	getOneSquareAndUpdateRobotsOnFreeAdjacentSquaresAndUpdateCommandsRange(robot, range, commandsRange, robotsOnFreeAdjacentSquares, Orientation.opposite(robot.getOrientation()));
     * 			|in
     * 			|	for each robot1 in robotsOnFreeAdjacentSquares :
     * 			|		for each robot2 in range :
     * 			|			robot1.getPosition() != robot2.getPosition()
     * 			|	&&
     * 			|	result == robotsOnFreeAdjacentSquares
     * @note	The order in which the method getOneSquareAndUpdateRobotsOnFreeAdjacentSquaresAndUpdateCommandsRange( ) is used multiple times is of importance in order to get
     * 			the most efficient path, as it takes less energy for the robot to go the the square in front than to the square left, right or behind.
     */
    @Model
    private static ArrayList<Robot> getFreeAdjacentSquares(Robot robot, ArrayList<Robot> range, HashMap<Robot, ArrayList<Command>> commandsRange) {

        ArrayList<Robot> robotsOnFreeAdjacentSquares = new ArrayList<Robot>();
        
        //The order of the summons is deliberate, as it takes less energy for the robot to go the the square in front than to the square left, right or behind.
        getOneSquareAndUpdateRobotsOnFreeAdjacentSquaresAndUpdateCommandsRange(robot, range, commandsRange, robotsOnFreeAdjacentSquares, robot.getOrientation());
        getOneSquareAndUpdateRobotsOnFreeAdjacentSquaresAndUpdateCommandsRange(robot, range, commandsRange, robotsOnFreeAdjacentSquares, Orientation.turnLeft(robot.getOrientation()));
        getOneSquareAndUpdateRobotsOnFreeAdjacentSquaresAndUpdateCommandsRange(robot, range, commandsRange, robotsOnFreeAdjacentSquares, Orientation.turnRight(robot.getOrientation()));
        getOneSquareAndUpdateRobotsOnFreeAdjacentSquaresAndUpdateCommandsRange(robot, range, commandsRange, robotsOnFreeAdjacentSquares, Orientation.opposite(robot.getOrientation()));
        
        //If there are two new robots on the same position, remove the last created robot on that position.
        ArrayList<Robot> redundancies = new ArrayList<Robot>();
        for(Robot robot1 : robotsOnFreeAdjacentSquares){
            for(Robot robot2 : range){
                if(robot1.getPosition() == robot2.getPosition()){
                    redundancies.add(robot1);
                    if(commandsRange != null)
                        commandsRange.remove(robot1);
                }
            }
        }
        for(Robot robot1: redundancies){
            robotsOnFreeAdjacentSquares.remove(robot1);
        }
        return robotsOnFreeAdjacentSquares;
    }

    /**
     * This method is summoned in getFreeAdjacentSquares( ). This method calculates which square is adjacent to the given robot in the given direction and the new energy-
     * level of the given robot if it would go to that square. If possible, a new robot is created and added to the given range. This new robot's position is the calculated 
     * adjacent square and it's energy-level is the calculated new energy-level.
     * 
     * @param 	robot
     * 			The given robot whose adjacent squares must be checked (see general description above).
     * @param 	range
     * 			The range of the original robot (see documentation getExtendedRange()).
     * @param 	commandsRange
     * 			Either null, or a HashMap mapping new robots to an ArrayList of basic commands the original robot would have to execute to get to the position of the new
     * 			robot (see documentation getExtendedRange()).
     * @param 	robotsOnFreeAdjacentSquares
     * 			The ArrayList to which the result of this method must be added in getFreeAdjacentSquares( ).
     * @param 	orientation
     * 			The orientation in which getFreeAdjacentSquares( ) wants the square adjacent to the given robot.
     * @effect	The new position and energy-level are calculated and a new robot on that position and with that energy-level is tried to be made and to be added to range.
     * 			|let
     * 			|	Position newPosition = robot.getSquareInDirection(1, orientation)
     * 			|	EnergyAmount usedEnergy = getEnergyToTurn().rescale(Orientation.getNbTurnsNecessary(orientation, robot.getOrientation())).add(robot.getEnergyToMove())
   	 *			|in
   	 *			|	EnergyAmount newEnergy = robot.getEnergy().substract(usedEnergy)
   	 *			|	makeNewRobotInRange(robot, commandsRange, robotsOnFreeAdjacentSquares,orientation, newPosition, newEnergy)
     */
    @Model
    private static void getOneSquareAndUpdateRobotsOnFreeAdjacentSquaresAndUpdateCommandsRange(Robot robot, ArrayList<Robot> range, HashMap<Robot, ArrayList<Command>> commandsRange ,
    ArrayList<Robot> robotsOnFreeAdjacentSquares, Orientation orientation){
    	Position newPosition = robot.getSquareInDirection(1, orientation);
    	EnergyAmount usedEnergy = getEnergyToTurn().rescale(Orientation.getNbTurnsNecessary(orientation, robot.getOrientation())).add(robot.getEnergyToMove());
    	if(usedEnergy.compareTo(robot.getEnergy()) != 1){
    		EnergyAmount newEnergy = robot.getEnergy().substract(usedEnergy);
    		makeNewRobotInRange(robot, commandsRange, robotsOnFreeAdjacentSquares,orientation, newPosition, newEnergy);
    	}
    }

	/**
	 * This method is summoned in getOneSquareAndUpdateRobotsOnFreeAdjacentSquaresAndUpdateCommandsRange( ).
     * Given a robot and a given orientation, this method checks whether the square adjacent to the given robot in the given orientation is free, isn't out of bounds and if 
     * the given newEnergy is a valid value for a robot's energy-level. If those conditions are met, a new robot is added to the ArrayList range. 
     * The variables of the new robot are equal to what the variables of the given robot would be if it moved to the said adjacent square, its new position included. 
     * If the formal argument commandsRange isn't null, the new robot is added as a key and the mapped value  of this new key is an ArrayList containing the orders that the given
     * robot would have to execute to get to the new robot's position.
     * 
	 * @param 	robot
	 * 			The given robot whose adjacent squares must be checked (see documentation getOneSquareAndUpdateRobotsOnFreeAdjacentSquaresAndUpdateCommandsRange( ).
	 * @param 	commandsRange
	 * 			Either null, or a HashMap mapping new robots to a list of orders the original robot would have to execute to get to the position of the new
     * 			robot (see documentation getExtendedRange()).
	 * @param 	robotsOnFreeAdjacentSquares
	 * 			The ArrayList to which the result of this method must be added.
	 * @param 	orientation
	 * 			The orientation of the new robot.
	 * @param 	newPosition
	 * 			The position of the new robot.
	 * @param 	newEnergy
	 * 			The energy-level of the new robot.
	 * @effect	If the given robot would accept the given newEnergy as energy-level, if the given newPosition is valid and there is no obstacle to a robot on that position,
     *   		add a new robot storing the given newPosition and with the given newEnergy in robotsOnFreeAdjacentSquares.
     *   		|if(robot.canHaveAsEnergy(newEnergy) && newPosition != null && !robot.getBoard().containsObstacle(newPosition, robot))
     *   		|then let
     *   		|		  Robot newRobot = new Robot(orientation, newEnergy)
     *   		|	  	  newRobot.setPosition(robot.getBoard(), newPosition)
     *   		|	  in
     *   		|		  robotsOnFreeAdjacentSquares.add(newRobot)
     * @effect	If the given HashMap<Robot, ArrayList<Command>> commandsRange is not null, add the new robot as key with as mapped value the appropriate orders for the 
     *   		original robot to reach the new robot's position. (see documentation getExtendedRange())
     *   		|if(newPosition != null && !robot.getBoard().containsObstacle(newPosition, robot) && commandsRange != null)
     *   		|then let
     *   		|		  Robot newRobot = new Robot(orientation, newEnergy)
     *   		|		  newRobot.setEnergyToMove(robot.getEnergyToMove())
     *   		|	  	  newRobot.setPosition(robot.getBoard(), newPosition)
     *   		|		  ArrayList<Command> newOrders = (ArrayList<Command>)commandsRange.get(robot).clone()
     *   		|		  if(orientation ==  Orientation.turnLeft(robot.getOrientation())) then  newOrders.add(Order.LEFT)
     *   		|		  if(orientation ==  Orientation.turnRight(robot.getOrientation())) then newOrders.add(Order.RIGHT)
     *   		|		  if(orientation ==  Orientation.opposite(robot.getOrientation())) then newOrders.add(Order.RIGHT);  newOrders.add(Order.RIGHT);
     *   		|	 in 
     *   		|		  newOrders.add(Order.MOVE)
     *   		|		  commandsRange.put(newRobot, newOrders)
 	 */
    @Model
	private static void makeNewRobotInRange(Robot robot, HashMap<Robot, ArrayList<Command>> commandsRange, ArrayList<Robot> robotsOnFreeAdjacentSquares, Orientation orientation,
											Position newPosition, EnergyAmount newEnergy) {
		
		//If the given robot would accept the given newEnergy as energy-level, if the given newPosition is valid and there is no obstacle to a robot on that position,
		//add a new robot storing the given newPosition and with the given newEnergy in robotsOnFreeAdjacentSquares.
        if(newPosition != null && !robot.getBoard().containsObstacle(newPosition, robot)){
            Robot newRobot = new Robot(orientation, newEnergy);
            newRobot.setEnergyToMove(robot.getEnergyToMove());
            newRobot.setPosition(robot.getBoard(), newPosition);
            robotsOnFreeAdjacentSquares.add(newRobot);

            //If the given HashMap<Robot, ArrayList<Command>> commandsRange is not null, add the new robot as key with as mapped value the appropriate orders for the 
            // original robot to reach the new robot's position. (see documentation getExtendedRange())
            if(commandsRange != null){
				@SuppressWarnings("unchecked")
				ArrayList<Command> newOrders = (ArrayList<Command>)commandsRange.get(robot).clone();
                if(orientation ==  Orientation.turnLeft(robot.getOrientation())) 
                    newOrders.add(BasicCommand.TURN_COUNTERCLOCKWISE);
                if(orientation ==  Orientation.turnRight(robot.getOrientation())) 
                    newOrders.add(BasicCommand.TURN_CLOCKWISE);
                if(orientation ==  Orientation.opposite(robot.getOrientation())) {
                    newOrders.add(BasicCommand.TURN_CLOCKWISE);
                    newOrders.add(BasicCommand.TURN_CLOCKWISE);
                }
                newOrders.add(BasicCommand.MOVE);
                commandsRange.put(newRobot, newOrders);
            }	
        }
	}
    
    
    /**
     * This method makes the robot execute a given program (or instance of the class Command).
     * 
     * @param 	command
     * 			The program/command the robot needs to execute.
     * @effect	The program is executed.
     * 			|command.execute(this)
     * @throws 	IllegalStateException
     * 			No program is stored in the robot.
     * 			|getProgram() == null
     * @throws 	IllegalStateException
     * 			The robot isn't placed on a board.
     * 			|getBoard() == null
     */
    public void executeProgram(){
    	if(getProgram() == null)
    		throw new IllegalStateException("No program stored in the robot!");
    	if(getBoard() == null)
    		throw new IllegalStateException("The robot isn't placed on a board!");
    	else
    		getProgram().execute(this);
    }
    
    /**
     * This method makes the robot execute one basic command of the given program.
     * 
     * @param 	command
     * 			The program/command the robot needs to execute one basic command of.
     * @effect	If the program is not at its last basic command, it executes the next basic command.
     * 			|if(command.containsNextBasicCommand(this, getPrograms().get(command)) && getPrograms().get(command) != 0)
     *			|	then getPrograms().put(command, command.getNbNextBasicCommand(this, getPrograms().get(command)));
     *			|		 command.executeStep(this, getPrograms().get(command));
     * @effect	If the program is at its last basic command, the program's first basic command is executed again.
     *			|else 
     *			|	then int lastStep = 0;
     *			|		 getPrograms().put(command, lastStep);
     *			|		 executeOneStep(command);
     */
    private void executeOneStepOfProgram(){    	
    		setProgressInProgram(getProgram().getStepNbOfNextStep(this, getProgressInProgram()));
    		getProgram().executeStep(this, getProgressInProgram());
    }
    
    /**
     * Add the program with the given name to the programs stored in the robot;
     * 
     * @param 	nameProgram
     * 			The name of the program to add.
     * @effect	The new program of this robot is set to the program with the given name.
     * 			|setProgram(Translater.readCommand(FileReader.openFile(nameProgram)))
     * @throws 	FileNotFoundException
     * 			The program was not found.
     */
    public void addProgram(String nameProgram) throws FileNotFoundException {
    	setProgram(Translater.readCommand(FileReader.openFile(nameProgram)));
    }
    
    /**
     * N steps (basic commands) of the program are executed in corresponding order. 
     * 
     * @param 	n
     * 			The number of steps of the program to execute.
     * @throws 	IllegalStateException
     * 			No program is stored in the robot.
     * 			|getProgram() == null
     * @effect	N steps of the program are executed.
     * 			|for each iterate in 0..n :
     * 			|	if(getProgram().containsNextBasicCommand(this, getProgressInProgram()))
     * 			|		then executeOneStepOfProgram() 
     * @throws 	IllegalStateException
     * 			No program is stored in the robot.
     * 			|getProgram() == null
     * @throws 	IllegalStateException
     * 			The robot isn't placed on a board.
     * 			|getBoard() == null
     */
    public void executeNSteps(int n) throws IllegalStateException{
    	if(getBoard() == null)
    		throw new IllegalStateException("The robot isn't placed on a board!");
    	if(getProgram() == null)
    		throw new IllegalStateException("No program stored in the robot!");
    	int i = 1;
    	while(i <= n && getProgram().containsNextStep(this, getProgressInProgram())){
    		executeOneStepOfProgram();
    		i++;
    	}
    }

    
    
}
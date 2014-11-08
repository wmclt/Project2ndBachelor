package core;

import java.util.*;
import Auxiliary.Position;
import Inspectors.Inspector;
import Items.Item;
import be.kuleuven.cs.som.annotate.Basic;
import be.kuleuven.cs.som.annotate.Immutable;
import be.kuleuven.cs.som.annotate.Model;
import be.kuleuven.cs.som.annotate.Raw;

/**
 * ...
 * @invar 	One entity can't be twice on the same board. 
 * 			|for each position1 in getEntitiesOnBoard().keySet() : 
 * 			| 	for each entity1 in getEntitiesOnBoard().get(position1) : 
 * 			| 		for each position2 in getEntitiesOnBoard().keySet() : 
 * 			| 			for each entity2 in getEntitiesOnBoard().get(position2) : 
 * 			| 				! (entity1 == entity2 && position1 != position2)
 * @invar 	No two robots on the same position
 * 			|let 
 * 			| 	robot1, robot2 && Robot.class.isInstance(robot1) && Robot.class.isInstance(robot2) && robot1 != robot2 
 * 			|in 
 * 			| 	for each position in getEntitiesOnBoard().keySet() :
 *        	|		!(getEntitiesOnBoard().get(position).contains(robot1) && getEntitiesOnBoard().get(position).contains(robot2))
 * @invar 	No wall and other entity on the same position. 
 * 			|for each position in getEntitiesOnBoard().keySet() : 
 * 			| 	for each entity in getEntitiesOnBoard().get(position) : 
 * 			| 		!(Wall.class.isInstance(entity) && getEntitiesOnBoard.get(position).size() > 1)
 * @invar	One entity can't be located on two different boards.
 * 			|for each entity instanceof Entity :
 * 			|	for each board1 in Board :
 * 			|		for each board2 in Board : 
 * 			|			! ((board1.containsEntity(entity) && board2.containsEntity(entity)) && board1 != board2)
 * @invar	A board should not contain terminated entities.
 * 			|for each board in Board :
 * 			|	 for each position in board.getEntitiesOnBoard().keySet() :
 * 			|		for each entity in board.getEntitiesOnPosition(position) :
 * 			|			!entity.isTerminated()
 * 
 * @author 	Brecht Gosselé & William Mauclet 2Bir: wtk-cws (Gosselé)
 *         	en cws-elt(Mauclet)
 * @version 3.0
 * 
 */
public class Board extends Deletable {

	private static final long maxX = Long.MAX_VALUE;
	private static final long maxY = Long.MAX_VALUE;
	private final long height;
	private final long width;
	private HashMap<Position, ArrayList<Entity>> entitiesOnBoard;

	/**
	 * Initialize this new board with given height and given width.
	 * 
	 * @param 	height
	 *          The initial height of this new board.
	 * @param 	width
	 *          The initial height of this new board.
	 * @effect 	The constructor of the superclass is invoked. 
	 * 			|super()
	 * @post 	The height of this new board is the given width. 
	 * 			|new.getHeight() == height
	 * @post 	The width of this new board is the given width. 
	 * 			|new.getWidth() == width
	 * @post	This new board has an ArrayList entitiesOnBoard.
	 * 			|new.getEntitiesOnBoard() != null
	 * @post	If an invalid value for the height and/or width is given, this board is terminated and has no height nor width.
	 * 			|if(!(isValidHeight(height) && isValidWidth(width)))
	 * 			|	then (new this).isTerminated()
	 * 			|		 (new this).getHeight() == null
	 * 			|		 (new this).getWidth() == null
	 * @throws	IllegalArgumentException
	 * 			An invalid value for the height and/or width was given.
	 * 			|!(isValidHeight(height) && isValidWidth(width))
	 */
	public Board(long width, long height) throws IllegalArgumentException  {
		super();
		this.entitiesOnBoard = new HashMap<Position, ArrayList<Entity>>();
		if(!(isValidHeight(height) && isValidWidth(width))) {
			this.terminate();
			throw new IllegalArgumentException("Invalid height and/or width given!");
		}
		this.height = height;
		this.width = width;
	}
	
	/**
	 * ...
	 */
	@Basic @Immutable 
	private static long getMaxX() {
		return maxX;
	}

	/**
	 * ...
	 */
	@Basic @Immutable
	private static long getMaxY() {
		return maxY;
	}
	
	/**
	 * ...
	 * @param 	height
	 * 			...
	 * @return	...
	 * 			|result == ((height <= getMaxY()) && (height > 0))
	 */
	@Model
	private static boolean isValidHeight(long height) {
		return ((height <= getMaxY()) && (height > 0));
	}
	
	/**
	 * ...
	 * @param 	height
	 * 			...
	 * @return	...
	 * 			|result == (width <= getMaxX()) && (width > 0)
	 */
	@Model
	private static boolean isValidWidth(long width) {
		return (width <= getMaxX()) && (width > 0);
	}

	/**
	 * ...
	 */
	@Basic @Model
	private HashMap<Position, ArrayList<Entity>> getEntitiesOnBoard() {
		return entitiesOnBoard;
	}

	/**
	 * ...
	 * 
	 * @param	x
	 * 			...
	 * @param 	y
	 * 			...
	 * @param 	entity
	 * 			...
	 * @post 	... 
	 * 			|(new entity).getBoard() == this && (new entity).getPosition() == Position.returnUniquePosition(x,y)
	 * @post	...
	 * 			|!entity.getBoard().containsEntity(entity)
	 * @post 	... 
	 * 			|new.getEntitiesOnBoard().get(Position.returnUniquePosition(x,y)).contains(entity)
	 * @throws 	NullPointerException
	 *          ... 
	 *         	|entity == null
	 * @throws 	IllegalStateException
	 *          ...
	 *          |this.isTerminated()
	 * @throws 	IllegalArgumentException
	 *          ... 
	 *          |entity.isTerminated()
	 * @throws 	IllegalArgumentException
	 *          ... 
	 *          |!isValidPosition(position)
	 * @throws 	IllegalArgumentException
	 *          ... 
	 *          |this.containsObstacle(position,entity)
	 */
	public void putEntity(Position position, Entity entity) throws IllegalStateException, NullPointerException,IllegalArgumentException {
		// Standard tests.
		if (isTerminated())
			throw new IllegalStateException("The board is terminated!");
		if (entity == null)
			throw new NullPointerException("No entity given.");
		if (entity.isTerminated())
			throw new IllegalArgumentException("The entity is terminated!");
		if (!isValidPosition(position))
			throw new IllegalArgumentException("Illegal coordinate!");
		
		// If obstacle, throw exception.
		if (this.containsObstacle(position, entity))
			throw new IllegalArgumentException("Position obstructed, contains an obstacle to the entity that need be put.");

		//If the entity had a previous board, remove it from its previous board.
		if(entity.getBoard() != null)
			entity.getBoard().removeEntity(entity);
		
		// If no "tuple" for this position yet, make a new one.
		if (!this.getEntitiesOnBoard().containsKey(position)) {
			ArrayList<Entity> entities = new ArrayList<Entity>();
			entities.add(entity);
			getEntitiesOnBoard().put(position, entities);
		}
		// If there already is a 'tuple' for this position, just add the entity to its ArrayList.
		else {
			getEntitiesOnBoard().get(position).add(entity);
		}
		// Change the data in entity.
		entity.setPosition(this, position);
	}

	/**
	 * ...
	 * 
	 * @param 	coordinate
	 * 			...
	 * @param 	maxCoordinate
	 * 			...
	 * @return 	... 
	 * 			| result == ((position.getX() <= getWidth()) && (position.getX() >= 0) && (position.getY() <= getHeight()) && (position.getY() >= 0))
	 */
	public boolean isValidPosition(Position position) {
		return ((position.getX() <= getWidth()) && (position.getX() >= 0) && (position.getY() <= getHeight()) && (position.getY() >= 0));
	}

	/**
	 * ...
	 */
	@Basic
	public long getHeight() {
		return height;
	}

	/**
	 * ...
	 */
	@Basic
	public long getWidth() {
		return width;
	}

	/**
	 * ...
	 * 
	 * @param 	x
	 * 			...
	 * @param 	y
	 * 			...
	 * @param 	entity
	 * 			...
	 * @return 	... 
	 * 			|let 
	 * 			| 	 Position position = Position.returnUniquePosition(x,y) 
	 * 			|in 
	 *         	| 	if(!getEntitiesOnBoard().containsKey(position)) 
	 *         	| 		then result == false 
	 *         	| 	else 
	 *         	| 		for	each entities in getEntitiesOnBoard().get(position) : 
	 *         	|			if(entities.isObstacleFor(entity)) 
	 *         	| 				result == true
	 *         	| 	if(result != true) 
	 *         	| 		result == false
	 */
	public boolean containsObstacle(Position position, Entity entity) {
		if (!getEntitiesOnBoard().containsKey(position)) {
			return false;
		} else {
			for (Entity entities : getEntitiesOnBoard().get(position)) {
				if (entities.isObstacleFor(entity)) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * ...
	 * 
	 * @param 	robot
	 * 			...
	 * @return	...
	 * 			|for each position in robot.getAllSquaresInDirection()
	 * 			|	if(containsEntity(position)
	 * 			|		then result == position
	 * @throws	NullPointerException
     * 			There is no entity at all in the direction the robot is facing.
     * 			|for each position in getAllSquaresInDirection(robot)) :
     * 			|	!position.containsEntity()
	 */
	public Position returnFirstOccupiedPositionInDirection(Robot robot)	{
		for(Position position : robot.getAllSquaresInDirection())
			if(containsEntity(position))
				return position;
		throw new NullPointerException("Robot could not hit anything in front of it!");
	}

	/**
	 * ...
	 * 
	 * @param 	entity
	 * 			...
	 * @post 	...
	 *       	|!new.getEntitiesOnBoard().get(entity.getPosition()).contains(entity)
	 * @post 	... 
	 * 			|if(getEntitiesOnBoard().get(entity.getPosition()).isEmpty()) 
	 *       	|	then new.getEntitiesOnBoard().remove(entity.getPosition())
	 * @post 	... 
	 * 			|(new entity).getBoard() == null && (new entity).getPosition() == null
	 * @throws 	NullPointerException
	 *          ... 
	 *          |entity == null
	 * @throws 	NullPointerException
	 *          ...
	 *          |!getEntitiesOnBoard().get(entity.getPosition()).contains(entity)
	 */
	public void removeEntity(Entity entity) throws NullPointerException {
		// Standard tests.
		if (entity == null)
			throw new NullPointerException();
		if (!getEntitiesOnBoard().get(entity.getPosition()).contains(entity))
			throw new NullPointerException();
		else
			getEntitiesOnBoard().get(entity.getPosition()).remove(entity);
			//Remove the entity out of the entitiesOnBoard (= HashMap)

		// If position ain't got no entities on it anymore, dump it.
		if (getEntitiesOnBoard().get(entity.getPosition()).isEmpty())
			getEntitiesOnBoard().remove(entity.getPosition());
		// Remove all references to this board in the entity.
		entity.removeFromBoard();
	}

	/**
	 * ...
	 * 
	 * @param 	position
	 * 			...
	 * @pre 	... 
	 * 			|!getEntitiesOnBoard().get(position).isEmpty()
	 * @effect	...
	 * 			|let
	 * 			|	int index = randomGenerator.nextInt(getEntitiesOnPosition(position).size() - 1)
	 * 			|in
	 * 			|	getEntitiesOnPosition(position).get(index).terminate()
	 */
	public void hitRandomEntity(Position position) {
		assert !getEntitiesOnBoard().get(position).isEmpty();
		Random randomGenerator = new Random();
		int index = randomGenerator.nextInt(getEntitiesOnPosition(position).size());
		// Hit
		getEntitiesOnPosition(position).get(index).hit();
	}
	
	/**
	 * ...
	 * @pre		...
	 * 			|isValidPosition(position)
	 * @param 	position
	 * 			...
	 * @effect	...
	 * 			|if(getEntitiesOnBoard().containsKey(position))
	 * 			|	for each entity in getEntitiesOnPosition(position))
	 * 			|		entity.hit()
	 */
	public void hitAllEntitiesOnPosition(Position position) {
		assert isValidPosition(position);
		if(getEntitiesOnBoard().containsKey(position)){
			for(Entity entity : getEntitiesOnPosition(position)){
				entity.hit();
			}
		}
	}
	
	/**
	 * ...
	 * 
	 * @param 	robot
	 * 			...
	 * @return	...
	 * 			|let
	 * 			|	Random rand = new Random()
	 * 			|	Position position = robot.getPosition()
	 * 			|in
	 * 			|	for each i in 1..getHeight()*getWidth()
	 * 			|		if(position == robot.getPosition())
	 * 			|			then let
	 * 			|					long x = rand.nextLong()
	 * 			|					long y = rand.nextLong()
	 * 			|				 in
	 * 			|					Position tempPosition = Position.returnUniquePosition(x, y)
	 * 			|					if(isValidPosition(tempPosition) && !containsObstacle(tempPosition, robot))
	 * 			|						then position = tempPosition
	 * 			|	result == position
	 */
	public Position getRandomFreePosition(Robot robot){
		Random rand = new Random();
		Position position = robot.getPosition();
		while(position == robot.getPosition()){
			long x = (rand.nextLong()/Long.MAX_VALUE)*getWidth();
			long y = (rand.nextLong()/Long.MAX_VALUE)*getHeight();
			Position tempPosition = Position.returnUniquePosition(x, y);
			if(!containsObstacle(tempPosition, robot))
				position = tempPosition;
		}
		return position;
	}
	
	/**
	 * ...
	 * 
	 * @pre		...
	 * 			|!getEntitiesOnPosition(position).isEmpty()
	 * @param 	position
	 * 			...
	 * @return	...
	 * 			|let
	 * 			|	Item item = null
	 * 			|in
	 * 			|	for each entity in getEntitiesOnPosition(position)
	 * 			|		if(Item.class.isInstance(entity)
	 * 			|			then item = (Item) entity
	 * 			|
	 * 			|	result == item
	 * @throws	NullPointerException
	 * 			...
	 * 			|!getEntitiesOnBoard().containsKey(position)
	 */
	public Item getRandomItemOnPosition(Position position) throws NullPointerException {
		if(!getEntitiesOnBoard().containsKey(position))
			throw new NullPointerException("No items on this position!");
		Item item = null;
		for(Entity entity : getEntitiesOnPosition(position)){
			if(Item.class.isInstance(entity))
				item = (Item) entity;
		}
		return item;
	}

	/**
	 * ...
	 * 
	 * @param 	x
	 * 			...
	 * @param 	y
	 * 			...
	 * @return 	... 
	 * 			|result == getEntitiesOnBoard().get(position)
	 * @throws 	IllegalArgumentException
	 *          ... 
	 *          |!isValidPosition(position)
	 */
	private ArrayList<Entity> getEntitiesOnPosition(Position position) throws IllegalArgumentException {
		if (!isValidPosition(position))
			throw new IllegalArgumentException();
		return getEntitiesOnBoard().get(position);
	}

	/**
	 * ...
	 * 
	 * @param 	position
	 * 			...
	 * @return	...
	 * 			|result == getEntitiesOnBoard().containsKey(position)
	 * @throws 	IllegalArgumentException
	 * 			...
	 * 			|!isValidPosition(position)
	 */
	public boolean containsEntity(Position position) throws IllegalArgumentException {
		if (!isValidPosition(position))
			throw new IllegalArgumentException("Position out-of-bounds!");
		return getEntitiesOnBoard().containsKey(position);
	}
	
	/**
	 * ...
	 * 
	 * @param	entity
	 * 			...
	 * @return	...
	 * 			|for each position in getEntitiesOnBoard().keySet() :
	 * 			|	 for each onBoard in getEntitiesOnPosition(position) :
	 * 			|		if(onBoard == entity) 
	 * 			|			then result == true 
	 * 			|result == false
	 */			
	@Model
	private boolean containsEntity(Entity entity) {
		for(Position position : getEntitiesOnBoard().keySet())
			for(Entity onBoard : getEntitiesOnPosition(position))
				if(onBoard == entity)
					return true;
		return false;
	}
	
	
	/**
	 * ...
	 * 
	 * @param 	clazz
	 * 			...
	 * @return	...
	 * 			|let
	 * 			|	HashSet<Entity> entities = new HashSet<Entity>()
	 * 			|	for each position in getEntitiesOnBoard().keySet() :
	 * 			|		for each entity in getEntitiesOnBoard().get(position) :
	 * 			|			if(clazz.isInstance(entity) then entities.add(entity.clone())
	 * 			|in
	 * 			|	result == entities
	 */
	public HashSet<Entity> getEntitiesOfSpecifiedKindOnBoard(Class<? extends Entity> clazz) {
		HashSet<Entity> entities = new HashSet<Entity>();
		for(Position position : getEntitiesOnBoard().keySet()){
			for(Entity entity: getEntitiesOnBoard().get(position)){
				if(clazz.isInstance(entity)){
					entities.add(entity);
				}
			}
		}
		return entities;
	}
	
	/**
	 * ...
	 * 
	 * @param 	position
	 * 			...
	 * @param 	clazz
	 * 			...
	 * @return	...
	 * 			|if(!getEntitiesOnBoard().containsKey(position))
	 *			|	result == false
	 *			|for(Entity entity : getEntitiesOnPosition(position))
	 *			|	if(clazz.isInstance(entity))
	 *			|		result == true
	 *			|result == false
	 */
	public boolean containsEntityOfSpecifiedKindOnPosition(Position position, Class<? extends Entity> clazz) {
		if(!getEntitiesOnBoard().containsKey(position))
			return false;
		for(Entity entity : getEntitiesOnPosition(position))
			if(clazz.isInstance(entity))
				return true;
		return false;
	}

	/**
	 * ...
	 * 
	 * @pre		...
	 * 			|firstBoard != null && secondBoard != null
	 * @pre		...
	 * 			|!firstBoard.isTerminated() && !secondBoard.isTerminated()
	 * @param 	firstBoard
	 * 			...
	 * @effect 	...
	 * 			|let
	 * 			|	ArrayList<Entity> transfers = new ArrayList<Entity>()
	 * 			|	for each position in secondBoard.getEntitiesOnBoard().keySet()
	 *         	| 		if(firstBoard.isValidPosition(position)) 
	 *         	| 			then for each entity in secondBoard.getEntitiesOnBoard().get(position)
	 *         	|				if(!firstBoard.containsObstacle(position,entity) 
	 *         	|		 			then transfers.add(entity)
	 *         	|in
	 *         	|	for each entity in transfers :
	 *         	|		firstBoard.putEntity(entity.getPosition(),entity)
	 * @effect 	... 
	 * 			|secondBoard.terminate()
	 */
	public static void merge(Board firstBoard, Board secondBoard) throws NullPointerException, IllegalStateException {
		// Standard assertions.
		if(firstBoard == null || secondBoard == null)
			throw new NullPointerException("A given board was null!");
		if(firstBoard.isTerminated() || secondBoard.isTerminated())
			throw new IllegalStateException("A given board was terminated!");
		
		ArrayList<Entity> transfers = new ArrayList<Entity>();
        // For each position, if valid position on firstBoard, and not occupied, put then entity in
	    // the ArrayList transfers.
        for (Position position : secondBoard.getEntitiesOnBoard().keySet())
            if (firstBoard.isValidPosition(position))
                for (Entity entity : secondBoard.getEntitiesOnBoard().get(position)) {
                    if(!firstBoard.containsObstacle(position,entity)) {
                        transfers.add(entity);
                    }
        }
        // Put each entity in transfers on the right place on firstBoard.
        for(Entity entity: transfers){
            firstBoard.putEntity(entity.getPosition(),entity);
        }
        // Terminate secondBoard.
        secondBoard.terminate();
	}

	/**
	 * ...
	 * 
	 * @effect 	... 
	 * 			|let
	 * 			|	ArrayList<Entity> trashCan = new ArrayList<Entity>()
	 * 			|	for each position in getEntitiesOnBoard().keySet()
	 * 			|		for each entity in getEntitiesOnBoard().get(position) 
	 * 			|		 	trashCan.add(entity)
	 * 			|in
	 * 			|	for each entity in trashCan :
	 * 			|		entity.terminate()
	 * @effect 	... 
	 * 			|super.terminate()
	 */
	@Raw
	public void terminate() {
		ArrayList<Entity> trashCan = new ArrayList<Entity>();
        //Put all elements to be terminated in the trashcan.
		for (Position position : getEntitiesOnBoard().keySet()){
            for (Entity entity: getEntitiesOnBoard().get(position)){
            	  trashCan.add(entity);
            }
        }
        //Terminate all elements in trashcan.
		for(Entity entity: trashCan) 
        	entity.terminate();
		//Terminate the board itself.
        super.terminate();	
        }
	
	/**
	 * ...
	 * 
	 * @param 	position
	 * 			...
	 * @param 	robot
	 * 			...
	 * @return	...
	 * 			|let
	 * 			|	ArrayList<Position> allStartPositions = new ArrayList<Position>()
	 * 			|	int size = 0
	 * 			|	trueOrFalse = false
	 * 			|in
	 * 			|	allStartPositions.add(position)
	 * 			|	for each index in 0..getWidth()*getHeight() :
	 * 			|		if(allStartPositions.size() != size)
	 * 			|			let
	 * 			|				int i = size
	 * 			|				size = allStartPositions.size()
	 * 			|			in
	 * 			|				for each index in i..size :
	 * 			|					for each iterate in getAdjacentSquares(allStartPositions.get(index),robot)) :
	 * 			|						if(!allStartPositions.contains(iterate)) then allStartPositions.add(iterate)
	 * 			|			if(allStartPositions.contains(robot.getPosition())) then trueOrFalse = true
	 * 			|	result == trueOrFalse
	 * @throws	IllegalArgumentException
	 * 			The given position is out of bounds.
	 * 			|!(isValidPosition(position))
	 */
	public boolean isPositionReachableForRobot(Position position, Robot robot) throws IllegalArgumentException {
		if(!isValidPosition(position))
			throw new IllegalArgumentException("Given position is out of bounds!");
		if(containsObstacle(position, robot))
			return false;
		ArrayList<Position> allStartPositions = new ArrayList<Position>();
		int size = 0;
		allStartPositions.add(position);
		while(allStartPositions.size() != size){
			int i = size;
			size = allStartPositions.size();
			while(i < size){
				for(Position iterate: getAdjacentSquares(allStartPositions.get(i),robot)){
					if(!allStartPositions.contains(iterate))
						allStartPositions.add(iterate);
				}
				i++;
			}
			if(allStartPositions.contains(robot.getPosition()))
				return true;
		}
		return false;
	}
	
	/**
	 * Return a matrix with the coordinates of the four squares adjacent to a given position.
	 * 
	 * @param 	position
	 * 			...
	 * @param	entity
	 * 			...
	 * @return	...
	 * 			|let
	 * 			|	ArrayList<Position> squares = new ArrayList<Position>()
	 * 			|	for each index in {-1, 1} :
	 * 			|		Position target = Position.returnUniquePosition(position.getX()+index,position.getY())
	 * 			|		if(isValidPosition(target) && !containsObstacle(target, entity))
	 * 			|			then squares.add(target)
	 * 			|		target = Position.returnUniquePosition(position.getX(),position.getY()+index)
	 * 			|		if(isValidPosition(target) && !containsObstacle(target, entity))
	 * 			|			then squares.add(target)
	 * 			|in
	 * 			|	result == squares
	 */
	private ArrayList<Position> getAdjacentSquares(Position position, Entity entity) {
		ArrayList<Position> squares = new ArrayList<Position>();
		for(int i=-1;i<=1;i+=2) {
			Position target = Position.returnUniquePosition(position.getX()+i,position.getY());
			if(isValidPosition(target) && !containsObstacle(target, entity)) 
				squares.add(target);
			target = Position.returnUniquePosition(position.getX(),position.getY()+i);
			if(isValidPosition(target) && !containsObstacle(target, entity)) 
				squares.add(target);
		}
		return squares;	
	}
	
	/**
	 * ...
	 * 
	 * @param	inspector
	 * 			...
	 * @return	...
	 * 			|let
	 * 			|	Iterator<Entity> iterator = new Iterator<Entity>
	 * 			|in	
	 * 			|	result == iterator
	 * 			|	&&
	 * 			|	if(iterator.hasNext())
	 * 			|		then let
	 * 			|				Entity entity = iterator.next()
	 * 			|			 in
	 * 			|				inspector.inspect(entity) == true
	 * 			|				entity.getBoard() == this
	 * 
	 */
	public Iterator<Entity> getElementsOnCondition(final Inspector inspector) throws Exception {
		if(getEntitiesOnBoard().isEmpty())
			throw new NullPointerException("Board is empty!");
		return new Iterator<Entity>(){

			@Override
			public boolean hasNext() {
				if(nextCalculated)
					return true;
				if(entitiesOnPositionIterator.hasNext()){
					Entity temp = entitiesOnPositionIterator.next();
					if(inspector.inspect(temp)){
						next = temp;
						nextCalculated = true;
						return true;
					}
					else return this.hasNext();
				}
				else if(positionIterator.hasNext()){
					entitiesOnPositionIterator = getEntitiesOnPosition(positionIterator.next()).iterator();
					return this.hasNext();
				}
				return false;	
			}

			@Override
			public Entity next() {
				if(nextCalculated){
					nextCalculated = false;
					return next;
				}
				else if(hasNext()){
					nextCalculated = false;
					return next;
				}
				else throw new NoSuchElementException();
			}

			@Override
			public void remove() throws UnsupportedOperationException{
				throw new UnsupportedOperationException();
				
			}
			
			private Iterator<Position> positionIterator = getEntitiesOnBoard().keySet().iterator();
			private Iterator<Entity> entitiesOnPositionIterator = getEntitiesOnPosition(positionIterator.next()).iterator();
			private Entity next = null;
			private boolean nextCalculated = false;
			
		};
		
	}
	
}

package Items;

import myPackage.Entity;
import myPackage.Robot;
import myPackage.Wall;
import be.kuleuven.cs.som.annotate.Basic;
import be.kuleuven.cs.som.annotate.Model;

/**
 * ...
 * 
 * @invar	An item cannot both be owned by a robot and be located on a board at the same time.
 * 			|!(	(item.getBoard() != null && item.getPosition() != null)
 * 			|	 &&
 * 			| 	!(for each robot in Robot :
 * 			|		!robot.carriesItem(item))
 * 			|	)
 * 
 * @note	The weight of the items is at all times expressed in grams, unless specified otherwise.
 * @author 	Brecht J.J. Gosselé & William E.R.J. Mauclet
 * 			2Bir: wtk-cws (Gosselé) en cws-elt(Mauclet)
 * @version	3.0
 *
 */
public abstract class Item extends Entity {

	/**
	 * Weight of the item in grams (g).
	 */
	private int weight;
	
	/**
	 * ...
	 * 
	 * @param	weight
	 * 			...
	 * @effect	...
	 * 			|super()
	 * @post	...
	 * 			|new.getWeight() == Math.abs(weight)
	 */
	public Item (int weight){
		super();
		setWeight(weight);
	}
	

	/**
	 * ...
	 * 
	 * @param 	weight
	 * 			...
	 * @post	...
	 * 			|new.getWeight() >= 0 && new.getWeight() < Integer.MAX_VALUE
	 */
	@Model
	private void setWeight(int weight) {
		this.weight = Math.abs(weight);
	}
	
	/**
	 * ...
	 */
	@Basic
	public int getWeight(){
		return weight;
	}
	
	/**
	 * ...
	 * 
	 * @pre		...
	 * 			|entity != null
	 * @return	...
	 * 			|if(Wall.class.isInstance(entity) then result == true
	 * 			|else then result == false
	 */
	@Override
	public boolean isObstacleFor(Entity entity) {
		assert(entity != null);
		if(Wall.class.isInstance(entity))
			return true;
		else
			return false;
	}
	
	/**
	 * ...
	 * 
	 * @return	...
	 * 			|result == ((this.getClass()) this).clone()
	 */
	public abstract Item clone();
	
	/**
	 * ...
	 * @param 	robot
	 * 			...
	 * @effect	...	
	 * 			|((this.getClass()) this ).useOn(robot)
	 */
	public abstract void useOn(Robot robot);
	
}

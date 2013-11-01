package core;

import Items.Item;

/**
 * ...
 * 
 * @author 	Brecht J.J. Gosselé & William E.R.J. Mauclet
 * 			2Bir: wtk-cws (Gosselé) en cws-elt(Mauclet)
 * @version	3.0
 *
 */
public class Wall extends Entity {

	/**
	 * ...
	 * 
	 * @effect	...
	 * 			|super()
	 */
	public Wall(){
		super();
	}
	
	/**
	 * ...
	 * 
	 * @pre		...
	 * 			|entity != null
	 * @return	...
	 * 			|result == true
	 */
	@Override
	public boolean isObstacleFor(Entity entity){
		assert(entity != null);
		return true;
	}
	
	@Override
	public boolean isObstacleForRobot(Robot robot) {
		return true;
	}

	@Override
	public boolean isObstacleForItem(Item item) {
		return true;
	}
	
	/**
	 * ...
	 * 
	 * @return	...
	 * 			|let
	 * 			|	Wall wall = new Wall()
	 * 			|	Wall.setPosition(getBoard(),getPosition())
	 * 			|in
	 * 			|	result == wall
	 */
	@Override
	public Wall clone() {
		Wall wall = new Wall();
		wall.setPosition(getBoard(), getPosition());
		return wall;
	}
	
	/**
	 *...
	 *@effect	...
	 *			|
	 */
	@Override
	public void hit() {}


	
}


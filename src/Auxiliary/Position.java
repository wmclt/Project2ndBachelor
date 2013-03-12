package Auxiliary;

import java.util.ArrayList;

import be.kuleuven.cs.som.annotate.Basic;
import be.kuleuven.cs.som.annotate.Immutable;

/**
 * ...
 * 
 * @invar	The x- and y-coordinates are positive.
 * 			|x >= 0 && y >= 0
 * @author 	Brecht J.J. Gosselé & William E.R.J. Mauclet
 * 			2Bir: wtk-cws (Gosselé) en cws-elt(Mauclet)
 * @version	3.0
 * 
 */
public class Position {
	
	private final long x;
	private final long y;
	private static ArrayList<Position> allPositions = new ArrayList<Position>();
	
	/**
	 * ...
	 * 
	 * @pre		...
	 * 			|!checkIfPositionExistsYet(x,y)
	 * @param	x
	 * 			...
	 * @param 	y
	 * 			...
	 * @post	...
	 * 			|this.getX() == x
	 * 			|this.getY() == y
	 */
	private Position(long x, long y){
		assert !checkIfPositionExistsYet(x,y);
		this.x = x;
		this.y = y;
		allPositions.add(this);
	}
	
	/**
	 * ...
	 */
	@Basic @Immutable
	public long getX(){
		return x;
	}
	
	/**
	 * ...
	 */
	 @Basic @Immutable
	 public long getY() {
		 return y;
	 }
	  
	/**
	 * ...
	 * 
	 * @param 	x
	 * 		...
	 * @param 	y
	 * 		...
	 * @return	...
	 * 		|let
	 * 		|	boolean trueOrFalse = false
	 * 		|	for each position in allPositions :
	 * 		|		if(position.getX() == x && position.getY() == y) then trueOrFalse == true
	 * 		|in
	 * 		|	result == trueOrFalse
	 */
	 public static boolean checkIfPositionExistsYet(long x, long y){
		 for(Position position : allPositions) {
			 if(position.getX() == x && position.getY() == y)
				 return true;
		 }
		 return false;
	 }
	 
	/**
	 * ...
	 * 
	 * @param 	x
	 * 			...
	 * @param 	y
	 * 			...
	 * @return	...
	 * 			|let
	 * 			|	Position position
	 * 			|	for each position in allPositions :
	 * 			|		if(position.getX() == x && position.getY() == y) then result == position
	 * 			|in
	 * 			|	if(result != null) then result == position
	 * 			|	else 			   then result == new Position(x,y)
	 */
	 public static Position returnUniquePosition(long x, long y) {
		for(Position position : allPositions) {
			if(position.getX() == x && position.getY() == y)
				return position;
		}
		return new Position(x,y);
			 
	 }
	 
	
	/**
	 * Return the Manhattan-distance between two positions.
	 * 
	 * @param 	position1
	 * 			...
	 * @param	position2
	 * 			...
	 * @return	The sum of Manhattan-distance between the two given positions.
	 * 			|result == Math.abs(position1.getY() -position2.getY()) + Math.abs(position1.getX() - position2.getX())
	 */
	 public static long getDistance(Position position1, Position position2) {
		return Math.abs(position1.getY() -position2.getY()) + Math.abs(position1.getX() - position2.getX());
	 }
	 
	 public static ArrayList<Position> getAllAdjacentSquares(Position position) {
		 ArrayList<Position> returnList = new ArrayList<Position>();
		 for(int i = -1; i <= 1; i++) {
			 for(int j = -1; j <= 1 ; j++) {
				 returnList.add(returnUniquePosition(position.getX()+i, position.getY()+j));
			 }
		 }
		 returnList.remove(position);
		 return returnList;
	 }
	 
	 /**
	  * ...
	  */
	 public static boolean isInRange(Position lowerBound, Position upperBound, Position position){
		 boolean x = (position.getX() >= lowerBound.getX() && position.getX() <= upperBound.getX());
		 boolean y = (position.getY() >= lowerBound.getY() && position.getY() <= upperBound.getY());
		 return x && y;
	 }
}

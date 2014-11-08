package Auxiliary;

import be.kuleuven.cs.som.annotate.Basic;
import be.kuleuven.cs.som.annotate.Model;
import be.kuleuven.cs.som.annotate.Value;

/**
 * An enumeration introducing different orientations.
 * For the moment, this class knows the orientations Up, Down, Left & Right.
 * 
 *
 * @author 	Brecht Gosselé & William Mauclet
 * 			2BiR: wtk-cws (Gosselé) en cws-elt(Mauclet)
 * @version	3.0
 */
@Value
public enum Orientation {
	
	UP(0), LEFT(3), RIGHT(1), DOWN(2);
	
	private int intOrientation;
	
	/**
	 * ...
	 * 
	 * @param 	intOrientation
	 * 			...
	 * @post	...
	 * 			|this.getIntOrientation() == intOrientation
	 */
	private Orientation(int intOrientation) {
		this.intOrientation = intOrientation;
	}
	
	/**
	 * ...
	 */
	@Basic
	public int getIntOrientation() {
		return intOrientation;
	}
	
	/**
	 * ...
	 * @param 	intOrientation
	 * 			...
	 * @return	...
	 * 			|let
	 * 			|	Orientation resultOrientation= Orientation.UP
	 * 			|	intOrientation = standardizeIntOrientation(intOrientation)
	 * 			|	for each orientation in Orientation.values() :
	 * 			|		if(intOrientation == orientation.getIntOrientation())
	 * 			|			then resultOrientation = orientation
	 * 			|in
	 * 			|	result == resultOrientation
	 */
	public static Orientation getOrientation(int intOrientation){
		Orientation resultOrientation = Orientation.UP;
		intOrientation = standardizeIntOrientation(intOrientation);
		for(Orientation orientation : Orientation.values())
			if(intOrientation == orientation.getIntOrientation())
				resultOrientation = orientation;
		return resultOrientation;
	}
	
	/**
	 * ...
	 * 
	 * @param 	orientation
	 * 			...
	 * @return	...
	 * 			|result == getOrientation(standardizeIntOrientation(orientation.getIntOrientation() + 1))
	 */
	public static Orientation turnRight(Orientation orientation) {
		int newOrientation = standardizeIntOrientation(orientation.getIntOrientation() + 1);
		return getOrientation(newOrientation);
	}
	
	/**
	 * ...
	 * 
	 * @param 	orientation
	 * 			...
	 * @return	...
	 * 			|result == getOrientation(standardizeIntOrientation(orientation.getIntOrientation() - 1))
	 */
	public static Orientation turnLeft(Orientation orientation) {
		int newOrientation = standardizeIntOrientation(orientation.getIntOrientation() - 1);
		return getOrientation(newOrientation);
	}
	
	/**
	 * ...
	 * 
	 * @param 	orientation
	 * 			...
	 * @return	...
	 * 			|result == getOrientation(standardizeIntOrientation(orientation.getIntOrientation() - 2)
	 */
	public static Orientation opposite(Orientation orientation) {
		int newOrientation = standardizeIntOrientation(orientation.getIntOrientation() - 2);
		return getOrientation(newOrientation);
	}
	
	/**
	 * ...
	 * 
	 * @param 	intOrientation
	 * 			...
	 * @return	...
	 * 			|let
	 * 			|	int standardizedInt = intOrientation % Orientation.values().length
	 * 			|	if(standardizedInt < 0)
	 * 			|		standardizedInt += Orientation.values().length
	 * 			|in		
	 * 			|	result = standardizedInt
	 */
	@Model
	private static int standardizeIntOrientation(int intOrientation){
		int standardizedInt = intOrientation % Orientation.values().length;
		if(standardizedInt < 0)
			standardizedInt += Orientation.values().length;
		return standardizedInt;
	}
	
	/**
	 * ...
	 * 
	 * @param 	orientation1
	 * 			...
	 * @param 	orientation2
	 * 			...
	 * @return	...
	 * 			|if(orientation1 == orientation2) 						then result == 0
	 * 			|if(orientation1 == Orientation.opposite(orientation2)) then result == 2
	 * 			|else 													then result == 1
	 */
	public static int getNbTurnsNecessary(Orientation orientation1, Orientation orientation2){
		if(orientation1 == orientation2)
			return 0;
		if(orientation1 == Orientation.opposite(orientation2))
			return 2;
		else
			return 1;
	}
}

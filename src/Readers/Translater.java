package Readers;


import java.util.ArrayList;

import Commands.*;
import Conditions.*;

/**
 * @author 	Brecht Gosselé & William Mauclet 2Bir: 
 * 			wtk-cws (Gosselé)  resp. cws-elt(Mauclet)
 * @version 3.0
 *
 */
public class Translater{
	
	

	public static Command readCommand(String command) throws IllegalArgumentException{

		if(command.startsWith("(while")){
			Condition itCondition = null;
			Command itCommand = null;
			boolean conditionYet = false; 
			for(int i =6; i < command.length(); i++){
				if(command.substring(i, i+1).equalsIgnoreCase("(")) {
					
					int openingBrackets = 0;
					int closingBrackets = 0;
					for(int j = i; j < command.length(); j++) {
						
						if(command.substring(j, j+1).equalsIgnoreCase("("))
							openingBrackets++;
						if(command.substring(j, j+1).equalsIgnoreCase(")"))
							closingBrackets++;
						
						if(openingBrackets == closingBrackets) {
							if(!conditionYet){
								itCondition = readCondition(command.substring(i,j+1));
								conditionYet = true;
								i = j;
								break;
							}
							else if(conditionYet) {
								itCommand = readCommand(command.substring(i,j+1));
								return new Iteration(itCondition, itCommand);
							}
						}
					}
				}
			}
		}
		
		else if(command.startsWith("(if")){
			Condition biCondition = null;
			Command todoIfTrue = null;
			Command todoIfFalse = null;
			int brackets = 0;
			for(int i =3; i < command.length(); i++){
				if(command.substring(i, i+1).equalsIgnoreCase("(")) {
					
					int openingBrackets = 0;
					int closingBrackets = 0;
					for(int j = i; j < command.length(); j++) {
						
						if(command.substring(j, j+1).equalsIgnoreCase("("))
							openingBrackets++;
						if(command.substring(j, j+1).equalsIgnoreCase(")"))
							closingBrackets++;
						
						if(openingBrackets == closingBrackets) {
							if(brackets == 0){
								biCondition = readCondition(command.substring(i,j+1));
								brackets++;
								i = j;
								break;
							}
							else if(brackets == 1) {
								todoIfTrue = readCommand(command.substring(i,j+1));
								brackets++;
								i =j;
								break;
							}
							else if(brackets == 2) {
								todoIfFalse = readCommand(command.substring(i,j+1));
								return new BinaryCommand(biCondition, todoIfTrue, todoIfFalse);
							}
							
						}
					}
				}
			}
		}
		
		else if(command.startsWith("(seq")){
			ArrayList<Command> seqCommands = new ArrayList<Command>();
			
			for(int i =4; i < command.length(); i++){
				if(command.substring(i, i+1).equalsIgnoreCase("(")) {
					
					int openingBrackets = 0;
					int closingBrackets = 0;
					for(int j = i; j < command.length(); j++) {
						
						if(command.substring(j, j+1).equalsIgnoreCase("("))
							openingBrackets++;
						if(command.substring(j, j+1).equalsIgnoreCase(")"))
							closingBrackets++;
						
						if(openingBrackets == closingBrackets) {
							seqCommands.add(readCommand(command.substring(i,j+1)));
							i = j;
							break;
						}
						
					}
				}
			}
			return new Sequence(seqCommands);
		}
		
		else if(command.equalsIgnoreCase("(move)")){
			return BasicCommand.MOVE;
		}
		else if(command.equalsIgnoreCase("(shoot)")){
			return BasicCommand.SHOOT;
		}
		else if(command.equalsIgnoreCase("(pick-up-and-use)")){
			return BasicCommand.PICKUP_AND_USE;
		}
		else if(command.startsWith("(turnclockwise)")){
			return BasicCommand.TURN_CLOCKWISE;
		}
		else if(command.startsWith("(turncounterclockwise)")){
			return BasicCommand.TURN_COUNTERCLOCKWISE;
		}
		
		throw new IllegalArgumentException("Program was not recognised!");
	}
	
	
	
	public static Condition readCondition(String condition){
		if(condition.startsWith("(and")){
			ArrayList<Condition> conConditions = new ArrayList<Condition>();
			for(int i = 4; i < condition.length(); i++){
				if(condition.substring(i, i+1).equalsIgnoreCase("(")) {
					
					int openingBrackets = 0;
					int closingBrackets = 0;
					for(int j = i; j < condition.length(); j++) {
						
						if(condition.substring(j, j+1).equalsIgnoreCase("("))
							openingBrackets++;
						if(condition.substring(j, j+1).equalsIgnoreCase(")"))
							closingBrackets++;
						
						if(openingBrackets == closingBrackets) {
							conConditions.add(readCondition(condition.substring(i,j+1)));
							i = j;
							break;
						}
						
					}
				}
			}
			return new Conjunction(conConditions);
		}
		
		else if(condition.startsWith("(or")){
			ArrayList<Condition> disConditions = new ArrayList<Condition>();
			for(int i = 3; i < condition.length(); i++){
				if(condition.substring(i, i+1).equalsIgnoreCase("(")) {
					
					int openingBrackets = 0;
					int closingBrackets = 0;
					for(int j = i; j < condition.length(); j++) {
						
						if(condition.substring(j, j+1).equalsIgnoreCase("("))
							openingBrackets++;
						if(condition.substring(j, j+1).equalsIgnoreCase(")"))
							closingBrackets++;
						
						if(openingBrackets == closingBrackets) {
							disConditions.add(readCondition(condition.substring(i,j+1)));
							i = j;
							break;
						}
						
					}
				}
			}
			return new Disjunction(disConditions);
		}
		
		else if(condition.startsWith("(not")){
			for(int i = 4; i < condition.length(); i++){
				if(condition.substring(i, i+1).equalsIgnoreCase("(")) {
					int openingBrackets = 0;
					int closingBrackets = 0;
					for(int j = i; j < condition.length(); j++) {
						
						if(condition.substring(j, j+1).equalsIgnoreCase("("))
							openingBrackets++;
						if(condition.substring(j, j+1).equalsIgnoreCase(")"))
							closingBrackets++;
						
						if(openingBrackets == closingBrackets) 
							return new Negation(readCondition(condition.substring(i,j+1)));
						
					}
				}
			}
			assert false;
			//XXX Exception hier gooien? zie exception lager ook
			
		}
		
		else if(condition.startsWith("(true)"))
			return BasicCondition.TRUE;
		else if(condition.startsWith("(false)"))
			return BasicCondition.FALSE;
		else if(condition.startsWith("(energy-at-least")){
			return new EnergyAtLeastCondition(Double.parseDouble(condition.substring(16, condition.length()-1)));}
		else if(condition.equalsIgnoreCase("(at-item)"))
			return BasicCondition.AT_ITEM;
		else if(condition.equalsIgnoreCase("(can-hit-robot)"))
			return BasicCondition.CAN_HIT_ROBOT;
		else if(condition.equalsIgnoreCase("(wall)")){
			return BasicCondition.WALL;
		}
			
		throw new IllegalArgumentException("A condition was not recognised!");
	}
		
		
}
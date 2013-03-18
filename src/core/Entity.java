package core;

import Auxiliary.Position;
import be.kuleuven.cs.som.annotate.Basic;
import be.kuleuven.cs.som.annotate.Raw;

/**
 * ...
 * 
 * @invar	The entity has either no position and no board, 
 * 			|(getPosition() == null && getBoard() == null) 
 * 			or it has a board and a valid position on that board.
 * 			| (getBoard() != null && getBoard().isLegalCoordinate(getPosition().getX(),getPosition().getY()))
 * @author 	Brecht J.J. Gosselé & William E.R.J. Mauclet
 * 			2Bir: wtk-cws (Gosselé) en cws-elt(Mauclet)
 * @version	3.0
 */
public abstract class Entity extends Deletable{
	
	private Board board;
	private Position position;

	/**
	 * ...
	 * 
	 * @effect	...
	 * 			|super()
	 */
	public Entity() {
		super();	
	}
	
	/**
	 * ...
	 */
	@Basic
	public Board getBoard() {
		return board;
	}
	
	/**
	 * ...
	 */
	@Basic
	public Position getPosition() {
		return position;
	}
	
	/**
	 * ...
	 * 
	 * @pre		...
	 * 			|if(board != null) then position != null
	 * @pre		...
	 * 			|if(board != null) then !board.isTerminated()
	 * @pre		...
	 * 			|if(board != null) then board.isLegalCoordinate(position.getX(),position.getY())
	 * @pre		...
	 * 			|!this.isTerminated()
	 * @param	board
	 * 			...
	 * @param	x
	 * 			...
	 * @param 	y
	 * 			...
	 * @post	...
	 * 			|(new this).getBoard() = board
	 * @post	...
	 * 			|(new this).getPosition() = Position.returnUniquePosition(x,y)
	 */
	@Raw
	public void setPosition(Board board, Position position) {
		if(board != null) {
			assert position != null;
			assert !board.isTerminated();
			assert board.isValidPosition(position);
		}
		assert !this.isTerminated();
		
		//If this entity is already placed on a board.
		if(this.getBoard() != null){
			//And if this entity's board is not the given board, remove this entity from this entity's board.
			if(this.getBoard() != board){
				getBoard().removeEntity(this);
			}						
		}
		//Remember new board and new position.
		this.setBoard(board);
		this.setPosition(position);
	}
	
	/**
	 * ...
	 * 
	 * @param 	position
	 * 			...
	 * @post	...
	 * 			|new.getPosition() == position
	 */
	private void setPosition(Position position) {
		this.position = position;
	}

	/**
	 * ...
	 * @pre		...
	 * 			|board != null
	 * @pre		...
	 * 			|!board.isTerminated()
	 * @param 	board2
	 * 			...
	 * @post	...
	 * 			|new.getBoard() == board
	 */
	private void setBoard(Board board) {
		if(board != null)
			assert !board.isTerminated();
		this.board = board;
	}
	
	

	/**
	 * ...
	 * 
	 * @effect	...
	 * 			|this.setPosition(null)
	 * @effect	...
	 * 			|this.setBoard(null)
	 */
	public void removeFromBoard(){
		this.setPosition(null);
		this.setBoard(null);
	}
	
	/**
	 * ...
	 * 
	 * @effect	...
	 * 			|if(getBoard() != null) then getBoard().removeEntity()
	 * @effect	...
	 * 			|super.terminate()
	 */
	public void terminate(){
		if(getBoard() != null) {
			getBoard().removeEntity(this);
		}
		removeFromBoard();
		super.terminate();
	}
	
	/**
	 * ...
	 * 
	 * @return	(see documentation of classes implementing Entity for more information).
	 * 			|result == ((this.getClass() this).clone()
	 */
	public abstract Entity clone();
	
	/**
	 * ...
	 * 
	 * @param 	entity
	 * 			...
	 * @return	(see documentation of classes implementing Entity for more information)
	 * 			|result == ((this.getClass() this).isObstacleFor(entity)
	 */
	public abstract boolean isObstacleFor(Entity entity);
	
	/**
	 * ...
	 * 
	 * @param 	entity
	 * 			...
	 * @effect	(see documentation of classes implementing Entity for more information)
	 * 			|((this.getClass() this).hit
	 */
	public abstract void hit();
	
}

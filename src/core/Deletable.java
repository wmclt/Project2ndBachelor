package core;

import be.kuleuven.cs.som.annotate.Basic;
import be.kuleuven.cs.som.annotate.Raw;

/**
 * ...
 * 
 * @author 	Brecht Gosselé & William E.R.J Mauclet
 * 			2Bir: wtk-cws (Gosselé) en cws-elt(Mauclet)
 * @version	3.0
 *
 */
public abstract class Deletable {
	
	/**
     * Variable registering whether or not this deletable has been terminated.
     */
    private boolean isTerminated;
	
    /**
     * Constructor.
     * 
     * @post	...
     * 			|!new.isTerminated()
     */
	public Deletable(){
		isTerminated = false;
	}
	
	
	/**
	 * Check whether this deletable is terminated.
	 */
	@Basic @Raw
	public boolean isTerminated() {
		return this.isTerminated;
	}
	
	/**
	 * Terminate this deletable.
	 *
	 * @post   	This deletable is terminated.
	 *      	| new.isTerminated()
	 */
	@Raw
	public void terminate() {
		this.isTerminated = true;
	}

	
	

}

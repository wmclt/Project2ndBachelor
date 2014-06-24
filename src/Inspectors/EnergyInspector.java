package Inspectors;

import core.EnergyRelated;
import core.Entity;
import Auxiliary.EnergyAmount;

/**
 * @author 	Brecht J.J. Gosselé & William E.R.J. Mauclet
 * 		   	2BiR: wtk-cws (Gosselé) en cws-elt(Mauclet)
 * @version	3.0
 */
public class EnergyInspector implements Inspector {

	EnergyAmount minimalEnergy;
	
	public EnergyInspector(EnergyAmount minimalEnergy) {
		this.minimalEnergy = minimalEnergy;
	}
	
	@Override
	public boolean inspect(Entity entity) {
		if(!EnergyRelated.class.isInstance(entity))
			return false;
		return ((EnergyRelated)entity).getEnergy().compareTo(minimalEnergy) > -1;
	}

}

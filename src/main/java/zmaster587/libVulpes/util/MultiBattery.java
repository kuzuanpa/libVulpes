package zmaster587.libVulpes.util;

import java.util.LinkedList;

import org.jetbrains.annotations.NotNull;
import zmaster587.libVulpes.api.IUniversalEnergy;

public class MultiBattery implements IUniversalEnergy {

	//Note: as of writing there should never be a need to save this
	
	protected final LinkedList<IUniversalEnergy> batteries = new LinkedList<>();
	
	public void addBattery(IUniversalEnergy battery) {
		batteries.add(battery);
	}
	
	public boolean removeBattery(IUniversalEnergy battery) {
		return batteries.remove(battery);
	}
	
	public void clear() {
		batteries.clear();
	}
	
	@Override
	public int extractEnergy(int amt, boolean simulate) {
		int amtExtracted = 0;
		
		for(@NotNull IUniversalEnergy battery : batteries)
			amtExtracted += battery.extractEnergy(amt, simulate);
		
		return amtExtracted;
	}

	@Override
	public int getEnergyStored() {
		int energyStored = 0;
		for(IUniversalEnergy battery : batteries)
			energyStored += battery.getEnergyStored();

		return energyStored;
	}

	@Override
	public int getMaxEnergyStored() {
		int energyStored = 0;
		for(IUniversalEnergy battery : batteries)
			energyStored += battery.getMaxEnergyStored();

		return energyStored;
	}
	
	@Override
	public void setMaxEnergyStored(int max) {
		max /= batteries.size();
		
		for(IUniversalEnergy battery : batteries) {
			battery.setMaxEnergyStored(max);
		}
	}

	@Override
	public int acceptEnergy(int amt, boolean simulate) {
		int energyRecieved = 0;
		for(IUniversalEnergy battery : batteries)
			energyRecieved += battery.acceptEnergy(amt - energyRecieved, simulate);
		return energyRecieved;
	}

	@Override
	public void setEnergyStored(int amt) {
		int difference = amt - getEnergyStored();
		int amtAdded = 0;
		
		//Possible inf loop
		//TODO: fix distribution
		if(difference > 0)
		while(amtAdded < difference) {
			int recieved = acceptEnergy(difference, false);
			if(recieved == 0)
				break;
			amtAdded += recieved;
		}
		else if(difference < 0)
			while(amtAdded < -difference) {
				int recieved =  extractEnergy(-difference, false);
				if(recieved == 0)
					break;
				amtAdded += recieved;
			}
	}

}

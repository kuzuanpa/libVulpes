package zmaster587.libVulpes.api;

import cpw.mods.fml.common.Optional.Interface;

@Interface(iface="ic2.api.energy.tile.IEnergySink",modid="IndustrialCraft")
public interface IUniversalEnergy  {
	void setEnergyStored(int amt);

	int extractEnergy(int amt, boolean simulate);

	int getEnergyStored();

	int getMaxEnergyStored();

	int acceptEnergy(int amt, boolean simulate);
	
	void setMaxEnergyStored(int max);
}

package zmaster587.libVulpes.api;

import net.minecraftforge.common.util.ForgeDirection;

public interface IUniversalEnergyTransmitter {
	
	/**
	 * @param side side requesting energy, UNKNOWN for internal tranmission or for teleportation
	 * @return max energy units that can be transmitted
	 */
    int getEnergyMTU(ForgeDirection side);
	
	/**
	 * @param side side requesting energy, UNKNOWN for internal tranmission or for teleportation
	 * @param simulate false to commit the change, true to only simulate
	 * @return amount of energy actually transmitted
	 */
    int transmitEnergy(ForgeDirection side, boolean simulate);
}

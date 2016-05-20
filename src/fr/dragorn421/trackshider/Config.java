package fr.dragorn421.trackshider;

import org.bukkit.configuration.ConfigurationSection;

public class Config
{

	public boolean hideAllRails;
	public int hideAllRailsChunkRadius;
	public int hideRailsOnConnectionAfterTicks;
	public boolean fixMinecartsRotation;
	public boolean showRailDirectionOnClick;
	public boolean showRidedVehicleLocation;
	public int showRidedVehicleLocationIntervalSeconds;
	public float pitchOffsetWhenShowRidedVehicleLocation;
	public float yawOffsetWhenShowRidedVehicleLocation;

	public boolean update(final ConfigurationSection c)
	{
		boolean modified = false;
		// hideAllRails
		if(!c.isBoolean("hideAllRails"))
		{
			c.set("hideAllRails", false);
			modified = true;
		}
		this.hideAllRails = c.getBoolean("hideAllRails");
		// hideAllRailsChunkRadius
		if(!c.isInt("hideAllRailsChunkRadius"))
		{
			c.set("hideAllRailsChunkRadius", 0);
			modified = true;
		}
		this.hideAllRailsChunkRadius = c.getInt("hideAllRailsChunkRadius");
		// hideRailsOnConnectionAfterTicks
		if(!c.isInt("hideRailsOnConnectionAfterTicks"))
		{
			c.set("hideRailsOnConnectionAfterTicks", 20);
			modified = true;
		}
		this.hideRailsOnConnectionAfterTicks = c.getInt("hideRailsOnConnectionAfterTicks");
		// fixMinecartsRotation
		if(!c.isBoolean("fixMinecartsRotation"))
		{
			c.set("fixMinecartsRotation", false);
			modified = true;
		}
		this.fixMinecartsRotation = c.getBoolean("fixMinecartsRotation");
		// showRailDirectionOnClick
		if(!c.isBoolean("showRailDirectionOnClick"))
		{
			c.set("showRailDirectionOnClick", false);
			modified = true;
		}
		this.showRailDirectionOnClick = c.getBoolean("showRailDirectionOnClick");
		// showRidedVehicleLocation
		if(!c.isBoolean("showRidedVehicleLocation"))
		{
			c.set("showRidedVehicleLocation", false);
			modified = true;
		}
		this.showRidedVehicleLocation = c.getBoolean("showRidedVehicleLocation");
		// showRidedVehicleLocationIntervalSeconds
		if(!c.isInt("showRidedVehicleLocationIntervalSeconds"))
		{
			c.set("showRidedVehicleLocationIntervalSeconds", 2);
			modified = true;
		}
		this.showRidedVehicleLocationIntervalSeconds = c.getInt("showRidedVehicleLocationIntervalSeconds");
		// pitchOffsetWhenShowRidedVehicleLocation
		if(!c.isDouble("pitchOffsetWhenShowRidedVehicleLocation"))
		{
			c.set("pitchOffsetWhenShowRidedVehicleLocation", 0D);
			modified = true;
		}
		this.pitchOffsetWhenShowRidedVehicleLocation = (float) c.getDouble("pitchOffsetWhenShowRidedVehicleLocation");
		// yawOffsetWhenShowRidedVehicleLocation
		if(!c.isDouble("yawOffsetWhenShowRidedVehicleLocation"))
		{
			c.set("yawOffsetWhenShowRidedVehicleLocation", 0D);
			modified = true;
		}
		this.yawOffsetWhenShowRidedVehicleLocation = (float) c.getDouble("yawOffsetWhenShowRidedVehicleLocation");
		return modified;
	}

}

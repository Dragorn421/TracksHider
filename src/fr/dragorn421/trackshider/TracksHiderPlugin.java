package fr.dragorn421.trackshider;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.RideableMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.material.Rails;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public class TracksHiderPlugin extends JavaPlugin implements Listener
{

	static private TracksHiderPlugin instance;

	final private Config config = new Config();

	@Override
	public void onEnable()
	{
		TracksHiderPlugin.instance = this;
		if(this.config.update(this.getConfig()))
			this.saveConfig();
		Bukkit.getPluginManager().registerEvents(this, this);
		super.getLogger().info(super.getName() + " enabled!");
	}

	@Override
	public void onDisable()
	{
		super.getLogger().info(super.getName() + " disabled!");
	}

	// quand un joueur change de chunk, lui cacher tous les rails du chunk
	@EventHandler
	public void onPlayerMove(final PlayerMoveEvent e)
	{
		if(!this.config.hideAllRails)
			return;
		final Chunk centerChunk = e.getTo().getChunk();
		if(centerChunk == e.getFrom().getChunk())// do nothing if player didnt change chunk
			return;
		this.hideRailsAround(centerChunk, e.getPlayer());
	}

	// cacher aussi les rails quand le joueur se connecte
	@EventHandler
	public void onPlayerJoin(final PlayerJoinEvent e)
	{
		if(!this.config.hideAllRails)
			return;
		Bukkit.getScheduler().runTaskLater(this, new Runnable() {
			@Override
			public void run()
			{
				TracksHiderPlugin.this.hideRailsAround(e.getPlayer().getLocation().getChunk(), e.getPlayer());
			}
		}, this.config.hideRailsOnConnectionAfterTicks);
	}

	// quand un joueur entre dans un véhicule, lui envoyer ttes les 2 secondes la position de ce véhicule
	final private Map<Player, BukkitTask> tasks = new HashMap<Player, BukkitTask>();

	@EventHandler
	public void onVehicleEnter(final VehicleEnterEvent e)
	{
		if(!this.config.showRidedVehicleLocation)
			return;
		if(!(e.getEntered() instanceof Player))// ignore non players
			return;
		if(this.tasks.containsKey(e.getEntered()))
			this.tasks.get(e.getEntered()).cancel();
		this.tasks.put((Player) e.getEntered(), Bukkit.getScheduler().runTaskTimer(this, new Runnable() {
			@Override
			public void run()
			{
				final Location loc = e.getVehicle().getLocation();
				e.getEntered().sendMessage(loc.toString());
				if(TracksHiderPlugin.this.config.pitchOffsetWhenShowRidedVehicleLocation != 0 || TracksHiderPlugin.this.config.yawOffsetWhenShowRidedVehicleLocation != 0)
				{
					loc.setPitch(loc.getPitch() + TracksHiderPlugin.this.config.pitchOffsetWhenShowRidedVehicleLocation);
					loc.setYaw(loc.getYaw() + TracksHiderPlugin.this.config.yawOffsetWhenShowRidedVehicleLocation);
					e.getVehicle().teleport(loc);
				}
			}
		}, 0L, this.config.showRidedVehicleLocationIntervalSeconds * 20));
	}

	@EventHandler
	public void onVehicleExit(final VehicleExitEvent e)
	{
		if(!this.config.showRidedVehicleLocation)
			return;
		if(!(e.getExited() instanceof Player))// ignore non players
			return;
		if(this.tasks.containsKey(e.getExited()))
			this.tasks.get(e.getExited()).cancel();
	}

	// envoyer au joueur la direction d'un rail quand il clique dessus
	@EventHandler
	public void onPlayerInteract(final PlayerInteractEvent e)
	{
		if(!this.config.showRailDirectionOnClick)
			return;
		if(e.getClickedBlock() == null)
			return;
		if(!this.isRails(e.getClickedBlock().getType()))
			return;
		final Rails r = (Rails) Material.RAILS.getNewData(e.getClickedBlock().getData());
		e.getPlayer().sendMessage(r.getDirection().toString());
	}

	// quand un minecart bouge, on corrige sa rotation (apparemment, n'a aucun effet...)
	@EventHandler
	public void onVehicleMove(final VehicleMoveEvent e)
	{
		if(!this.config.fixMinecartsRotation)
			return;
		if(!(e.getVehicle() instanceof RideableMinecart))
			return;
		final Location loc = e.getVehicle().getLocation();
		final Rails r = (Rails) Material.RAILS.getNewData(loc.getBlock().getData());
		float yaw = 0;
		switch(r.getDirection())
		{
		case NORTH:
		case SOUTH:
			yaw = 90;
			break;
		case EAST:
		case WEST:
			yaw = 0;
			break;
		case NORTH_EAST:
		case SOUTH_WEST:
			yaw = 45;
			break;
		case NORTH_WEST:
		case SOUTH_EAST:
			yaw = 135;
			break;
		// ignore other values, not needed. even half of above is unnecessary
		default:
			return;
		}
		loc.setYaw(yaw);
		e.getVehicle().teleport(loc);
	}

	// cacher les rails dans les chunks alentours à un joueur
	public void hideRailsAround(final Chunk centerChunk, final Player p)
	{
		// for each chunk around the center chunk
		for(int cx=centerChunk.getX()-this.config.hideAllRailsChunkRadius;cx<=centerChunk.getX()+this.config.hideAllRailsChunkRadius;cx++)
			for(int cz=centerChunk.getZ()-this.config.hideAllRailsChunkRadius;cz<=centerChunk.getZ()+this.config.hideAllRailsChunkRadius;cz++)
			{
				final Chunk c = centerChunk.getWorld().getChunkAt(cx, cz);
				// scan the chunk for any rail and hide it
				for(int x=0;x<16;x++)
					for(int y=0;y<c.getWorld().getMaxHeight()-1;y++)
						for(int z=0;z<16;z++)
						{
							final Block b = c.getBlock(x, y, z);
							if(this.isRails(b.getType()))
								p.sendBlockChange(b.getLocation(), Material.AIR, (byte)0);
						}
			}
	}

	// est-ce que le bloc est un rail
	public boolean isRails(final Material type)
	{
		switch(type)
		{
		// FALL-THROUGH
		case RAILS:
		case POWERED_RAIL:
		case ACTIVATOR_RAIL:
		case DETECTOR_RAIL:
			return true;
		default:
			return false;
		}
	}

	static public TracksHiderPlugin get()
	{
		return TracksHiderPlugin.instance;
	}

}

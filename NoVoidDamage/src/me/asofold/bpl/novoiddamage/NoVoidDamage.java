package me.asofold.bpl.novoiddamage;

import java.util.HashSet;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.plugin.java.JavaPlugin;

public class NoVoidDamage extends JavaPlugin implements Listener{
	/** Ok to teleport on.*/
	final HashSet<Integer> okBlocks = new HashSet<Integer>();
	
	public NoVoidDamage(){
		super();
		for (int x : new int[]{
				1,2,3,4,5,
				7,8,9,
				12,13,14,15,16,
				17,18,19,20,21,22,
				23,24,25,29,33,
				35,41,42,43,44,45,46,47,48,49,
				52,53,54,56,57,58,60,61,62,
				67,73,74,79, 80, 82,84,86,87,88,
				89, 91, 95, 97,98,99,100,103,
				108, 109, 110, 111, 112, 114, 116,
				118, 120, 121, 123, 124, 125, 126,
				128, 129, 130, 133, 134,135,137,138,
				
		}){
			okBlocks.add(x);
		}
	}
	
	@Override
	public void onEnable() {
		getServer().getPluginManager().registerEvents(this, this);
		super.onEnable();
	}

	@EventHandler(priority=EventPriority.LOWEST, ignoreCancelled=true)
	final void onDamage(final EntityDamageEvent event){
		if (event.getEntityType() != EntityType.PLAYER) return;
		if (event.getCause() != DamageCause.VOID) return;
		final Entity entity = event.getEntity();
		if (!(entity instanceof Player)) return;
		event.setCancelled(true);
		final Player player = (Player) entity;
		final Location loc = player.getLocation();
		final Chunk chunk = loc.getChunk();
		if (!chunk.isLoaded()) chunk.load();
		final World world = loc.getWorld();
		final Block block = world.getHighestBlockAt(loc.getBlockX(), loc.getBlockZ());
		final int id = block.getTypeId();
		if (okBlocks.contains(id)){
			teleportSafe(player, block.getRelative(BlockFace.UP).getLocation());
			checkKick(player);
			return;
		}
		else if (id == 0){
			if (okBlocks.contains(block.getRelative(BlockFace.DOWN).getTypeId())){
				teleportSafe(player, block.getLocation());
				checkKick(player);
				return;
			}
		}
		teleport(player, player.getWorld().getSpawnLocation());
		checkKick(player);
	}

	private void checkKick(Player player) {
		if (!player.isOnline() || Bukkit.getPlayerExact(player.getName()) == null){
			player.kickPlayer("You are not online anyway.");
			player.remove();
		}
	}

	private void teleportSafe(Player player, Location location) {
		if (!teleport(player, location)){
			teleport(player, player.getWorld().getSpawnLocation());
		}
	}

	private boolean teleport(Player player, Location location) {
		if (!location.getChunk().isLoaded()) location.getChunk().load();
		final Block block = location.getBlock().getRelative(BlockFace.DOWN);
		if (player.isOnline()) player.sendBlockChange(block.getLocation(), block.getTypeId(), block.getData());
		return player.teleport(location);
	}
}

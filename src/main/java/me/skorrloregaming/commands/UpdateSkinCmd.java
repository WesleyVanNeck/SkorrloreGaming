package me.skorrloregaming.commands;

import me.skorrloregaming.$;
import me.skorrloregaming.Link$;
import me.skorrloregaming.Server;
import me.skorrloregaming.skins.model.SkinModel;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Optional;

public class UpdateSkinCmd implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player))
			return true;
		Player player = ((Player) sender);
		if (Bukkit.getOnlineMode()) {
			player.sendMessage("This commmand is currently not applicable to you.");
			return true;
		}
		if (Server.getDelaySkinUpdate().containsKey(player.getUniqueId())) {
			player.sendMessage("You must wait " + Link$.formatTime(Server.getDelaySkinUpdate().get(player.getUniqueId())) + " before you can update your skin again.");
			return true;
		}
		final Player fPlayer = player;
		if (!Server.getDelaySkinUpdate().containsKey(fPlayer.getUniqueId())) {
			Server.getDelaySkinUpdate().put(fPlayer.getUniqueId(), 60 * 5);
		}
		new BukkitRunnable() {
			int time = 60 * 5;

			public void run() {
				time--;
				Server.getDelaySkinUpdate().put(fPlayer.getUniqueId(), time);
				if (time <= 0) {
					if (Server.getDelaySkinUpdate().containsKey(fPlayer.getUniqueId())) {
						Server.getDelaySkinUpdate().remove(fPlayer.getUniqueId());
					}
					cancel();
				}
			}
		}.runTaskTimer(Server.getPlugin(), 20L, 20L);
		player.sendMessage("Attempting to fetch the latest skin available..");
		Bukkit.getScheduler().runTaskAsynchronously(Server.getPlugin(), new Runnable() {
			@Override
			public void run() {
				Optional<SkinModel> model = Server.getSkinStorage().forceSkinUpdate(player);
				if (!model.isPresent()) {
					player.sendMessage("Failed to fetch your latest skin from mojang " + "\u2639" + ".");
					return;
				}
				Server.getSkinStorage().getFactory(player, model.get()).applySkin();
				player.sendMessage("Skin updated. You are now using the latest skin available.");
			}
		});
		return true;
	}

}

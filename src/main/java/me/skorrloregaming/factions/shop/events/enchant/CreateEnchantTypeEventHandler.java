package me.skorrloregaming.factions.shop.events.enchant;

import me.skorrloregaming.$;
import me.skorrloregaming.AnvilGUI;
import me.skorrloregaming.Server;
import me.skorrloregaming.factions.shop.LaShoppe;
import me.skorrloregaming.factions.shop.events.item.CreateItemPriceEventHandler;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;

import java.lang.reflect.InvocationTargetException;

public class CreateEnchantTypeEventHandler implements AnvilGUI.AnvilClickEventHandler {

	private LaShoppe shoppe;

	public CreateEnchantTypeEventHandler(LaShoppe shoppe) {
		this.shoppe = shoppe;
	}

	@Override
	public void onAnvilClick(AnvilGUI.AnvilClickEvent event) {
		String enchantmentName = event.getName().toUpperCase().replace(" ", "_");
		Enchantment enchantment = null;
		try {
			enchantment = Enchantment.getByName($.unformatEnchantment(enchantmentName));
		} catch (Exception ex) {
			event.getPlayer().sendMessage("Sorry, that's not a valid enchant name.");
			return;
		}
		final Enchantment fEnchantment = enchantment;
		Bukkit.getScheduler().runTaskLater(Server.getPlugin(), new Runnable() {

			@Override
			public void run() {
				try {
					new AnvilGUI(event.getPlayer(), new CreateEnchantPriceEventHandler(shoppe, fEnchantment))
							.setInputName("Enter price")
							.open();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				} catch (InstantiationException e) {
					e.printStackTrace();
				}
			}
		}, 1L);
	}
}

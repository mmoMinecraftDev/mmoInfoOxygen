/*
 * This file is part of mmoInfoOxygen <http://github.com/mmoMinecraftDev/mmoInfoOxygen>.
 *
 * mmoInfoOxygen is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
/*
 * This file is part of mmoInfoFood <http://github.com/mmoMinecraftDev/mmoInfoFood>.
 *
 * mmoInfoFood is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package mmo.Info;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import mmo.Core.InfoAPI.MMOInfoEvent;
import mmo.Core.MMOPlugin;
import mmo.Core.MMOPlugin.Support;
import mmo.Core.util.EnumBitSet;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.PluginManager;
import org.getspout.spoutapi.gui.Color;
import org.getspout.spoutapi.gui.ContainerType;
import org.getspout.spoutapi.gui.GenericContainer;
import org.getspout.spoutapi.gui.GenericGradient;
import org.getspout.spoutapi.gui.GenericLabel;
import org.getspout.spoutapi.gui.GenericTexture;
import org.getspout.spoutapi.gui.Gradient;
import org.getspout.spoutapi.gui.InGameHUD;
import org.getspout.spoutapi.gui.Label;
import org.getspout.spoutapi.gui.RenderPriority;
import org.getspout.spoutapi.gui.Screen;
import org.getspout.spoutapi.gui.Texture;
import org.getspout.spoutapi.gui.Widget;
import org.getspout.spoutapi.player.SpoutPlayer;

public class mmoInfoOxygen extends MMOPlugin implements Listener {
	private static final Map<Player, Widget> oxygenbar = new HashMap<Player, Widget>();
	private static String config_displayas = "bar";	
	private static Color redBar = new Color(0.69f,0.09f,0.12f,1f);
	private static Color blueBar = new Color(0,0,1f,1f);	

	@Override
	public EnumBitSet mmoSupport(EnumBitSet support) {		
		support.set(Support.MMO_AUTO_EXTRACT);
		return support;
	}

	@Override
	public void onEnable() {
		super.onEnable();
		pm.registerEvents(this, this);
	}

	@Override
	public void loadConfiguration(final FileConfiguration cfg) {
		config_displayas = cfg.getString("displayas", config_displayas);		
	}

	@EventHandler
	public void onMMOInfo(MMOInfoEvent event)
	{
		if (event.isToken("oxygen")) {
			SpoutPlayer player = event.getPlayer();
			if (player.hasPermission("mmo.info.oxygen")) {
				if (config_displayas.equalsIgnoreCase("bar")) {				
					final CustomWidget widget = new CustomWidget();					
					event.setWidget(plugin, widget);
					event.setIcon("o2.png");
				} else if (config_displayas.equalsIgnoreCase("icon")) { 
					player.getMainScreen().getBubbleBar().setVisible(false);
					CustomLabel label = (CustomLabel)new CustomLabel().setResize(true).setFixed(true);
					label.setText("100%");								
					oxygenbar.put(player, label);
					event.setWidget(this.plugin, label);
					event.setIcon("o2.png");
				} else {
					event.setCancelled(true);
				}
			}
		}
	}

	
	public class CustomLabel extends GenericLabel {
		private transient int tick = 0;
		public void onTick() {		
			if (tick++ % 40 == 0) {
				DecimalFormat df = new DecimalFormat("##");
				setText((String.format(df.format(getScreen().getPlayer().getRemainingAir() / 3.0F)) + "%"));			
			}
		}
	}

	public class CustomWidget extends GenericContainer {

		private final Gradient slider = new GenericGradient();
		private final Texture bar = new GenericTexture();

		public CustomWidget() {
			super();
			slider.setMargin(1).setPriority(RenderPriority.Normal).setHeight(5).shiftXPos(1).shiftYPos(1);
			bar.setUrl("bar10.png").setPriority(RenderPriority.Lowest).setHeight(7).setWidth(103).shiftYPos(0);
			this.setLayout(ContainerType.OVERLAY).setMinWidth(103).setMaxWidth(103).setWidth(103);
			this.addChildren(slider, bar);
		}

		private transient int tick = 0;
		public void onTick() {
			
			if (tick++ % 40 == 0) {				
				final int playerOxygen = Math.max(0, Math.min( 100, (int) (getScreen().getPlayer().getRemainingAir()/3)));								
				if (playerOxygen>=33 ) {			
					slider.setColor(blueBar);				
				} else  {
					slider.setColor(redBar); 				
				}	
				slider.setWidth(playerOxygen);			
			}
		}
	}
}
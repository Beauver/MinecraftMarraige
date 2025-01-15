package com.beauver.minecraft.plugins.marriagePaper.Listeners;

import com.beauver.minecraft.plugins.marriagePaper.Classes.Couple;
import com.beauver.minecraft.plugins.marriagePaper.Util.MarriageHandler;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class RightClickListener implements Listener {

    @EventHandler
    public void onPlayerRightClick(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();

        if(!player.isSneaking()) return;

        Entity targetEntity = event.getRightClicked();
        if(!(targetEntity instanceof Player target)) return;

        Couple couple = null;
        for(Couple c : MarriageHandler.getMarriages()){
            if(c.getPartner1().equals(player.getUniqueId()) && c.getPartner2().equals(target.getUniqueId())){
                couple = c;
                break;
            }else if(c.getPartner1().equals(target.getUniqueId()) && c.getPartner2().equals(player.getUniqueId())){
                couple = c;
                break;
            }
        }
        if(couple == null) return;

        target.sendActionBar(Component.text("Your partner grabbed you closely and kissed you on the lips!").color(TextColor.fromHexString("#FFAA00")));
        player.sendActionBar(Component.text("You kissed your partner!").color(TextColor.fromHexString("#FFAA00")));
        target.spawnParticle(Particle.HEART, target.getLocation(), 10, 0.5,0.5,0.5);
        player.spawnParticle(Particle.HEART, player.getLocation(), 10, 0.5,1,0.5);
    }

}

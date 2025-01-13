package com.beauver.minecraft.mods.marraigemod.Listeners;

import com.beauver.minecraft.mods.marraigemod.Objects.Couple;
import com.beauver.minecraft.mods.marraigemod.Util.MarriageHandler;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.world.World;

public class RightClickListener {

    // Static initialization to register the callback
    public static void registerEvents() {
        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (entity instanceof PlayerEntity targetPlayer) {
                if(!player.isSneaking()) {
                    return ActionResult.SUCCESS;
                }

                Couple couple = null;
                boolean firstPlayerIsSource = false;
                for(Couple c : MarriageHandler.getMarriages()) {
                    if(c.getPartner1().equals(player.getUuid())){
                        couple = c;
                        break;
                    }else if(c.getPartner2().equals(player.getUuid())){
                        couple = c;
                        firstPlayerIsSource = true;
                        break;
                    }
                }
                if(couple == null){
                    return ActionResult.SUCCESS;
                }

                PlayerEntity targetPlayerJson = null;
                if(firstPlayerIsSource){
                    targetPlayerJson = player.getServer().getPlayerManager().getPlayer(couple.getPartner1());
                } else {
                    targetPlayerJson = player.getServer().getPlayerManager().getPlayer(couple.getPartner2());
                }
                if(targetPlayerJson == null){
                    return ActionResult.SUCCESS;
                }
                if(!targetPlayer.getUuid().equals(targetPlayerJson.getUuid())){
                    return ActionResult.SUCCESS;
                }

                player.sendMessage(Text.literal("You kissed your partner!")
                                .styled(style -> style.withColor(16755200)),
                        true
                );
                targetPlayer.sendMessage(Text.literal("Your partner grabbed you closely and kissed you!")
                                .styled(style -> style.withColor(16755200)),
                        true
                );

                if (player.getWorld() instanceof ServerWorld serverWorldPlayer && targetPlayer.getWorld() instanceof ServerWorld serverWorldTarget) {
                    serverWorldTarget.spawnParticles(
                            ParticleTypes.HEART, targetPlayer.getX(), targetPlayer.getY() + 2, targetPlayer.getZ(),
                            10, 0.7, 1, 0.7, 0.5
                    );
                    serverWorldPlayer.spawnParticles(
                            ParticleTypes.HEART, player.getX(), player.getY() + 2, player.getZ(),
                            10, 0.7, 1, 0.7, 0.5
                    );
                }
                return ActionResult.SUCCESS;
            }
            return ActionResult.PASS;
        });
    }
}

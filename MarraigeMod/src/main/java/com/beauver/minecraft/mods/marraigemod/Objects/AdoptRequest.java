package com.beauver.minecraft.mods.marraigemod.Objects;

import com.google.gson.Gson;
import net.minecraft.entity.player.PlayerEntity;

import java.util.UUID;

public class AdoptRequest {

    UUID proposer;
    UUID target;

    public AdoptRequest(UUID proposer, UUID target) {
        this.proposer = proposer;
        this.target = target;
    }

    public AdoptRequest(PlayerEntity proposer, PlayerEntity target) {
        this.proposer = proposer.getUuid();
        this.target = target.getUuid();
    }

    public UUID getProposer() {
        return proposer;
    }

    public UUID getTarget() {
        return target;
    }

}

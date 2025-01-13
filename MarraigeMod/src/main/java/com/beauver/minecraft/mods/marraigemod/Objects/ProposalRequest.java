package com.beauver.minecraft.mods.marraigemod.Objects;

import com.google.gson.Gson;
import net.minecraft.entity.player.PlayerEntity;

import java.util.UUID;

public class ProposalRequest {

    UUID proposer;
    UUID target;

    public ProposalRequest(UUID proposer, UUID target) {
        this.proposer = proposer;
        this.target = target;
    }

    public ProposalRequest(PlayerEntity proposer, PlayerEntity target) {
        this.proposer = proposer.getUuid();
        this.target = target.getUuid();
    }

    public String toJSONString(){
        return new Gson().toJson(this);
    }

    public UUID getProposer() {
        return proposer;
    }

    public UUID getTarget() {
        return target;
    }

}

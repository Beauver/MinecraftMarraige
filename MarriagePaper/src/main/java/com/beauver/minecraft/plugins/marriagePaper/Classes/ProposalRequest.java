package com.beauver.minecraft.plugins.marriagePaper.Classes;

import org.bukkit.entity.Player;

import java.util.UUID;

public class ProposalRequest {

    UUID proposer;
    UUID target;

    public ProposalRequest(UUID proposer, UUID target) {
        this.proposer = proposer;
        this.target = target;
    }

    public ProposalRequest(Player proposer, Player target) {
        this.proposer = proposer.getUniqueId();
        this.target = target.getUniqueId();
    }

    public UUID getProposer() {
        return proposer;
    }

    public UUID getTarget() {
        return target;
    }

}

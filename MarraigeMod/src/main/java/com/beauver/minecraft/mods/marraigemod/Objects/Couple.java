package com.beauver.minecraft.mods.marraigemod.Objects;

import com.beauver.minecraft.mods.marraigemod.Enums.RelationshipType;
import net.minecraft.entity.player.PlayerEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Couple {

    UUID p;
    UUID p2;
    RelationshipType type;
    List<UUID> children = new ArrayList<>();
    long marriedSince;

    //<editor-fold desc="constructors">
    public Couple(PlayerEntity p, PlayerEntity p2) {
        this.p = p.getUuid();
        this.p2 = p2.getUuid();
        type = RelationshipType.STRAIGHT;
        marriedSince = System.currentTimeMillis();
    }

    public Couple(PlayerEntity p, PlayerEntity p2, RelationshipType type) {
        this.p = p.getUuid();
        this.p2 = p2.getUuid();
        this.type = type;
        marriedSince = System.currentTimeMillis();
    }

    public Couple(PlayerEntity p, PlayerEntity p2, RelationshipType type, List<UUID> children, long marriedSince) {
        this.p = p.getUuid();
        this.p2 = p2.getUuid();
        this.type = type;
        this.children = children;
        this.marriedSince = marriedSince;
    }
    //</editor-fold>

    //<editor-fold desc="Getters, setters, etc">
    public void addChild(PlayerEntity child) {
        children.add(child.getUuid());
    }

    public void removeChild(PlayerEntity child) {
        children.remove(child.getUuid());
    }
    public List<UUID> getChildren(){
        return new ArrayList<>(children);
    }

    public UUID getPartner1(){
        return p;
    }

    public UUID getPartner2(){
        return p2;
    }

    public RelationshipType getRelationshipType(){
        return type;
    }

    public void setRelationshipType(RelationshipType type) {
        this.type = type;
    }

    public double getDaysMarried() {
        double days = (System.currentTimeMillis() - marriedSince) / 86400000.0;
        return Math.round(days * 10) / 10.0;
    }
    //</editor-fold>

}

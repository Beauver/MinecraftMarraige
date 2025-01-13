package com.beauver.minecraft.plugins.marriagePaper.Classes;



import com.beauver.minecraft.plugins.marriagePaper.Enums.RelationshipType;
import org.bukkit.entity.Player;

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
    public Couple(Player p, Player p2) {
        this.p = p.getUniqueId();
        this.p2 = p2.getUniqueId();
        type = RelationshipType.STRAIGHT;
        marriedSince = System.currentTimeMillis();
    }

    public Couple(Player p, Player p2, RelationshipType type) {
        this.p = p.getUniqueId();
        this.p2 = p2.getUniqueId();
        this.type = type;
        marriedSince = System.currentTimeMillis();
    }

    public Couple(Player p, Player p2, RelationshipType type, List<UUID> children, long marriedSince) {
        this.p = p.getUniqueId();
        this.p2 = p2.getUniqueId();
        this.type = type;
        this.children = children;
        this.marriedSince = marriedSince;
    }
    //</editor-fold>

    //<editor-fold desc="Getters, setters, etc">
    public void addChild(Player child) {
        children.add(child.getUniqueId());
    }

    public void removeChild(Player child) {
        children.remove(child.getUniqueId());
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

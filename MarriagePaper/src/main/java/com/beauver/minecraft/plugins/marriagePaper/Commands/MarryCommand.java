package com.beauver.minecraft.plugins.marriagePaper.Commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import com.beauver.minecraft.plugins.marriagePaper.Classes.Couple;
import com.beauver.minecraft.plugins.marriagePaper.Classes.ProposalRequest;
import com.beauver.minecraft.plugins.marriagePaper.Util.MarriageHandler;
import com.beauver.minecraft.plugins.marriagePaper.Util.ProposalRequestHandler;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.units.qual.C;
import org.jetbrains.annotations.NotNull;

@CommandAlias("marry")
public class MarryCommand extends BaseCommand {

    @Default
    @Subcommand("propose")
    @CommandCompletion("@players")
    public void marryPlayer(CommandSender sender, String[] args) {
        //checks
        if(!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Only a player can run this command.").color(TextColor.fromHexString("#FF5555")));
            return;
        }
        if(args.length == 0) {
            player.sendMessage(Component.text("Please mention a player you want to marry.").color(TextColor.fromHexString("#FF5555")));
            return;
        }
        if(args[0].equalsIgnoreCase(player.getName())) {
            player.sendMessage(Component.text("You may not marry yourself.").color(TextColor.fromHexString("#FF5555")));
            return;
        }

        String targetName = args[0];
        Player target = Bukkit.getPlayer(targetName);

        //player offline
        if(target == null) {
            player.sendMessage(Component.text("You may not marry someone that is offline.").color(TextColor.fromHexString("#FF5555")));
            return;
        }
        //already outgoing proposal to targer
        for(ProposalRequest r : ProposalRequestHandler.getProposals()){
            if(r.getProposer().equals(player.getUniqueId()) && r.getProposer().equals(target.getUniqueId())){
                player.sendMessage(Component.text("You already have an outgoing proposal to this player.").color(TextColor.fromHexString("#FF5555")));
            }
        }
        //is already married
        for(Couple c : MarriageHandler.getMarriages()){
            if(c.getPartner1().equals(player.getUniqueId()) || c.getPartner2().equals(player.getUniqueId())) {
                player.sendMessage(Component.text("You're already married, don't try and have a affair!").color(TextColor.fromHexString("#FF5555")));
                return;
            }
        }

        ProposalRequest request = new ProposalRequest(player, target);
        ProposalRequestHandler.addProposal(request);

        player.sendMessage(Component.text("You proposed to ").color(TextColor.fromHexString("#55FF55"))
                .append(Component.text(targetName + "!").color(TextColor.fromHexString("#00AA00"))));

        target.sendMessage(Component.text(player.getName()).color(TextColor.fromHexString("#00AA00"))
                .append(Component.text(" proposed to you!").color(TextColor.fromHexString("#55FF55")))
                .append(Component.text("\nRun /marry accept " + player.getName() + " to accept the proposal").color(TextColor.fromHexString("#55FF55"))
                        .hoverEvent(HoverEvent.showText(Component.text("Click here to accept the proposal").color(TextColor.fromHexString("#55FF55"))))
                        .clickEvent(ClickEvent.runCommand("/marry accept " + player.getName())))
                .append(Component.text("\nRun /marry reject " + player.getName() + " to reject the proposal").color(TextColor.fromHexString("#FF5555"))
                        .hoverEvent(HoverEvent.showText(Component.text("Click here to reject the proposal").color(TextColor.fromHexString("#FF5555"))))
                        .clickEvent(ClickEvent.runCommand("/marry reject " + player.getName()))));
    }

    @Subcommand("accept")
    @CommandCompletion("@players")
    public void acceptMarry(CommandSender sender, String[] args) {
        if(!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Only a player can run this command.").color(TextColor.fromHexString("#FF5555")));
            return;
        }
        if(args.length == 0) {
            player.sendMessage(Component.text("Please mention a player you want to accept their proposal.").color(TextColor.fromHexString("#FF5555")));
            return;
        }

        String targetName = args[0];
        OfflinePlayer targetOffline = Bukkit.getOfflinePlayer(targetName);
        ProposalRequest request = null;

        for(ProposalRequest r : ProposalRequestHandler.getProposals()){
            if(r.getTarget().equals(player.getUniqueId()) && r.getProposer().equals(targetOffline.getUniqueId())){
                request = r;
                break;
            }
        }
        if(request == null){
            player.sendMessage(Component.text("You do not have a proposal request from this player").color(TextColor.fromHexString("#FF5555")));
            return;
        }

        Couple couple = new Couple(player.getUniqueId(), targetOffline.getUniqueId());
        MarriageHandler.addMarriage(couple);
        ProposalRequestHandler.removeProposal(request);

        player.getServer().broadcast(Component.text(player.getName()).color(TextColor.fromHexString("#00AA00"))
                .append(Component.text(" and ").color(TextColor.fromHexString("#55FF55")))
                .append(Component.text(targetOffline.getName()).color(TextColor.fromHexString("#00AA00")))
                .append(Component.text(" are now officially married!").color(TextColor.fromHexString("#55FF55"))));
    }

    @Subcommand("reject")
    @CommandCompletion("@players")
    public void rejectMarry(CommandSender sender, String[] args) {
        if(!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Only a player can run this command.").color(TextColor.fromHexString("#FF5555")));
            return;
        }
        if(args.length == 0) {
            player.sendMessage(Component.text("Please mention a player you want to reject their proposal.").color(TextColor.fromHexString("#FF5555")));
            return;
        }

        String targetName = args[0];
        Player target = Bukkit.getPlayer(targetName);
        OfflinePlayer targetOffline = Bukkit.getOfflinePlayer(targetName);
        ProposalRequest request = null;

        for(ProposalRequest r : ProposalRequestHandler.getProposals()){
            if(r.getTarget().equals(player.getUniqueId()) && r.getProposer().equals(targetOffline.getUniqueId())){
                request = r;
                break;
            }
        }
        if(request == null){
            player.sendMessage(Component.text("You do not have a proposal request from this player").color(TextColor.fromHexString("#FF5555")));
            return;
        }
        ProposalRequestHandler.removeProposal(request);

        if(targetOffline.isOnline()){
            target.sendMessage(Component.text(player.getName() + " has rejected your proposal.").color(TextColor.fromHexString("#FF5555")));
        }

        player.sendMessage(Component.text("You rejected " + targetOffline.getName() + "'s proposal.").color(TextColor.fromHexString("#FF5555")));
    }

    @Subcommand("adopt")
    @CommandCompletion("@players")
    public void adoptPlayer(CommandSender sender, String[] args) {
        if(!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Only a player can run this command.").color(TextColor.fromHexString("#FF5555")));
            return;
        }
    }

    @Subcommand("adopt accept")
    @CommandCompletion("@players")
    public void acceptAdopt(CommandSender sender, String[] args) {
        if(!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Only a player can run this command.").color(TextColor.fromHexString("#FF5555")));
            return;
        }
    }

    @Subcommand("adopt reject")
    @CommandCompletion("@players")
    public void rejectAdopt(CommandSender sender, String[] args) {
        if(!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Only a player can run this command.").color(TextColor.fromHexString("#FF5555")));
            return;
        }
    }

    @Subcommand("kiss")
    public void marryKiss(CommandSender sender, String[] args) {
        if(!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Only a player can run this command.").color(TextColor.fromHexString("#FF5555")));
            return;
        }
    }

    @Subcommand("hug")
    public void marryHug(CommandSender sender, String[] args) {
        if(!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Only a player can run this command.").color(TextColor.fromHexString("#FF5555")));
            return;
        }
    }

}

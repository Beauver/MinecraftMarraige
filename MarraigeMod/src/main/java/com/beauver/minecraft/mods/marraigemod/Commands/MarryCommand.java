package com.beauver.minecraft.mods.marraigemod.Commands;

import com.beauver.minecraft.mods.marraigemod.Enums.RelationshipType;
import com.beauver.minecraft.mods.marraigemod.Objects.AdoptRequest;
import com.beauver.minecraft.mods.marraigemod.Objects.Couple;
import com.beauver.minecraft.mods.marraigemod.Objects.ProposalRequest;
import com.beauver.minecraft.mods.marraigemod.Util.AdoptRequestHandler;
import com.beauver.minecraft.mods.marraigemod.Util.MarriageHandler;
import com.beauver.minecraft.mods.marraigemod.Util.ProposalRequestHandler;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.GameProfileArgumentType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public class MarryCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess access, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(
                CommandManager.literal("marry")
                        .then(CommandManager.literal("propose")
                                .then(CommandManager.argument("player", GameProfileArgumentType.gameProfile())
                                        .executes(MarryCommand::propose)))
                        .then(CommandManager.literal("accept")
                                .then(CommandManager.argument("player", GameProfileArgumentType.gameProfile())
                                        .executes(MarryCommand::acceptMarriage)))
                        .then(CommandManager.literal("reject")
                                .then(CommandManager.argument("player", GameProfileArgumentType.gameProfile())
                                        .executes(MarryCommand::rejectMarriage)))
                        .then(CommandManager.literal("divorce")
                                        .executes(MarryCommand::divorce))
                        .then(CommandManager.literal("list")
                                .executes(MarryCommand::listMarriages))
                        .then(CommandManager.literal("kiss")
                                .executes(MarryCommand::kiss))
                        .then(CommandManager.literal("hug")
                                .executes(MarryCommand::hug))
                        .then(CommandManager.literal("adopt")
                                .then(CommandManager.literal("accept")
                                        .then(CommandManager.argument("player", GameProfileArgumentType.gameProfile())
                                                .executes(MarryCommand::adoptAccept)))
                                .then(CommandManager.literal("reject")
                                        .then(CommandManager.argument("player", GameProfileArgumentType.gameProfile())
                                                .executes(MarryCommand::adoptReject)))
                                .then(CommandManager.literal("leave")
                                        .executes(MarryCommand::adoptLeave))
                                .then(CommandManager.argument("player", GameProfileArgumentType.gameProfile())
                                        .executes(MarryCommand::adopt)))
                        .then(CommandManager.literal("modify")
                                .then(CommandManager.literal("relationship-type")
                                        .then(CommandManager.argument("Straight|Gay|Lesbian", StringArgumentType.word())
                                                .executes(MarryCommand::changeRelationshipType))))
        );
    }

    private static int propose(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Collection<GameProfile> profiles = GameProfileArgumentType.getProfileArgument(context, "player");
        if (profiles.isEmpty()) {
            context.getSource().sendError(Text.literal("Player not found. Are you sure they're online?"));
            return 1;
        }

        GameProfile profile = profiles.iterator().next();
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity targetPlayer = source.getServer().getPlayerManager().getPlayer(profile.getId());

        if(targetPlayer == null) {
            source.sendError(Text.literal("You can only propose to people that are online!"));
            return 1;
        }
        if(source.getPlayer() == targetPlayer) {
            source.sendError(Text.literal("As desperate as you are and after all your attempts, you may not propose to yourself."));
            return 1;
        }
        for(Couple couple : MarriageHandler.getMarriages()) {
            if(couple.getPartner1().equals(source.getPlayer().getUuid()) || couple.getPartner2().equals(source.getPlayer().getUuid())) {
                source.sendError(Text.literal("You are already married... Are you trying to have an affair?!"));
                return 1;
            }
        }
        for(ProposalRequest p : ProposalRequestHandler.getProposals()) {
            if(p.getProposer().equals(source.getPlayer().getUuid()) && p.getTarget().equals(targetPlayer.getUuid())) {
                source.sendError(Text.literal("You have already have an outgoing proposal to this player."));
                return 1;
            }else if(p.getTarget().equals(source.getPlayer().getUuid()) && p.getProposer().equals(targetPlayer.getUuid())){
                acceptMarriage(context);
                return 1;
            }
        }

        ProposalRequest request = new ProposalRequest(source.getPlayer(), targetPlayer);
        ProposalRequestHandler.addProposal(request);

        source.getPlayer().sendMessage(Text.literal("You have proposed to: ").withColor(16755200)
                .append(Text.literal(targetPlayer.getName().getString()).withColor(16733525))
                .append(Text.literal("!").withColor(16755200)), false);

        targetPlayer.sendMessage(Text.literal(source.getPlayer().getName().getString()).withColor(16733525)
                .append(Text.literal(" has proposed to you!\nUse ").withColor(16755200))
                .append(Text.literal("/marry accept ").withColor(5635925))
                .append(Text.literal(source.getPlayer().getName().getString()).withColor(5635925))
                .append(Text.literal(" to accept the proposal!").withColor(16755200)), false);
        return 1;
    }

    private static int acceptMarriage(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Collection<GameProfile> profiles = GameProfileArgumentType.getProfileArgument(context, "player");
        if (profiles.isEmpty()) {
            context.getSource().sendError(Text.literal("Player not found. Are you sure they're online?"));
            return 1;
        }

        GameProfile profile = profiles.iterator().next();
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity targetPlayer = source.getServer().getPlayerManager().getPlayer(profile.getId());

        ProposalRequest request = null;
        if(targetPlayer == null) {
            source.sendError(Text.literal("You can only accept proposals of people who are online."));
            return 1;
        }
        for(Couple couple : MarriageHandler.getMarriages()) {
            if(couple.getPartner1().equals(source.getPlayer().getUuid()) || couple.getPartner2().equals(source.getPlayer().getUuid())) {
                source.sendError(Text.literal("You are already married... Are you trying to have an affair?!"));
                return 1;
            }
        }
        for(ProposalRequest p : ProposalRequestHandler.getProposals()) {
            if(p.getTarget().equals(source.getPlayer().getUuid()) && p.getProposer().equals(targetPlayer.getUuid())) {
                request = p;
                break;
            }
        }
        if(request == null){
            source.sendError(Text.literal("You have no proposal request from this player."));
            return 1;
        }

        ProposalRequestHandler.removeProposal(request);
        Couple couple = new Couple(source.getPlayer(), targetPlayer);
        MarriageHandler.addMarriage(couple);

        source.getServer().getPlayerManager().broadcast(Text.literal(source.getPlayer().getName().getString()).withColor(43520)
                .append(Text.literal(" and ").withColor(5635925))
                .append(Text.literal(targetPlayer.getName().getString()).withColor(43520))
                .append(Text.literal(" are now happily married!").withColor(5635925)), false);

        return 1;
    }

    private static int rejectMarriage(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Collection<GameProfile> profiles = GameProfileArgumentType.getProfileArgument(context, "player");
        if (profiles.isEmpty()) {
            context.getSource().sendError(Text.literal("Player not found. Are you sure they're online?"));
            return 1;
        }

        GameProfile profile = profiles.iterator().next();
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity sourcePlayer = source.getPlayer();

        if (sourcePlayer == null) {
            source.sendError(Text.literal("This command can only be run by a player."));
            return 1;
        }
        UUID sourceUUID = sourcePlayer.getUuid();
        UUID targetUUID;

        ServerPlayerEntity targetPlayer = source.getServer().getPlayerManager().getPlayer(profile.getId());
        if (targetPlayer != null) {
            targetUUID = targetPlayer.getUuid();
        } else {
            Optional<GameProfile> cachedProfile = source.getServer().getUserCache().findByName(profile.getName());
            if (cachedProfile.isPresent()) {
                targetUUID = cachedProfile.get().getId();
            } else {
                source.sendError(Text.literal("This player could not be found. Are you sure you spelled the name correctly?"));
                return 1;
            }
        }

        ProposalRequest request = null;
        for (ProposalRequest p : ProposalRequestHandler.getProposals()) {
            if (p.getTarget().equals(sourceUUID) && p.getProposer().equals(targetUUID)) {
                request = p;
                break;
            }
        }
        if (request == null) {
            source.sendError(Text.literal("You have no proposal request from this player."));
            return 1;
        }

        ProposalRequestHandler.removeProposal(request);
        sourcePlayer.sendMessage(Text.literal("You have rejected the proposal request... You probably made them cry ):").withColor(16755200), false);
        return 1;
    }

    private static int divorce(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity sourcePlayer = source.getPlayer();
        UUID sourceUUID = sourcePlayer.getUuid();

        Couple couple = null;
        for (Couple c : MarriageHandler.getMarriages()) {
            if (c.getPartner1().equals(sourceUUID) || c.getPartner2().equals(sourceUUID)) {
                couple = c;
                break;
            }
        }
        if(couple == null){
            source.sendError(Text.literal("Are you trying to divorce your oxygen supply? You're not married silly!"));
            return 1;
        }

        MarriageHandler.removeMarriage(couple);
        source.getServer().getPlayerManager().broadcast(Text.literal(source.getPlayer().getName().getString()).withColor(11141120)
                .append(Text.literal(" has filed for a divorce!").withColor(16733525)), false);
        return 1;
    }

    private static int listMarriages(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();

        MutableText text = Text.literal("");
        text.append(Text.literal("Marriage List:\n").withColor(5635925));
        for(Couple c : MarriageHandler.getMarriages()) {

            text.append(Text.literal("- ").withColor(11184810));
            text.append(Text.literal(source.getServer().getUserCache().getByUuid(c.getPartner1()).get().getName()).withColor(5636095));

            switch(c.getRelationshipType()){
                case STRAIGHT -> text.append(Text.literal(" ❤ ").withColor(11141120));
                case GAY -> text.append(Text.literal(" ❤ ").withColor(43690));
                case LESBIAN -> text.append(Text.literal(" ❤ ").withColor(16733695));
            }

            text.append(Text.literal(source.getServer().getUserCache().getByUuid(c.getPartner2()).get().getName()).withColor(5636095));
            text.append(Text.literal("\n-- ").withColor(11184810));
            text.append(Text.literal("Days Married: " + c.getDaysMarried()).withColor(5635925));
            text.append(Text.literal("\n"));

            if(c.getChildren().isEmpty()){
                continue;
            }

            text.append(Text.literal("-- ").withColor(11184810));

            int childAmount = c.getChildren().size();
            for(UUID child : c.getChildren()) {
                text.append(source.getServer().getUserCache().getByUuid(child).get().getName()).withColor(11184810);
                if(childAmount != 1){
                    text.append(Text.literal(", ").withColor(11184810));
                }
                childAmount--;
            }
            text.append(Text.literal("\n"));
        }

        source.getPlayer().sendMessage(text);
        return 1;
    }

    private static int adopt(CommandContext<ServerCommandSource> context) throws CommandSyntaxException{
        Collection<GameProfile> profiles = GameProfileArgumentType.getProfileArgument(context, "player");
        if (profiles.isEmpty()) {
            context.getSource().sendError(Text.literal("Player not found. Are you sure they're online?"));
            return 1;
        }

        GameProfile profile = profiles.iterator().next();
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity targetPlayer = source.getServer().getPlayerManager().getPlayer(profile.getId());

        Couple couple = null;
        if(targetPlayer == null) {
            source.sendError(Text.literal("You can only request adoption to people that are online!"));
            return 1;
        }
        if(source.getPlayer() == targetPlayer) {
            source.sendError(Text.literal("Do you want to be an orphan? Of course you can't adopt yourself!"));
            return 1;
        }
        for(AdoptRequest r : AdoptRequestHandler.getAdoptionRequests()){
            if(r.getTarget().equals(targetPlayer.getUuid()) && r.getProposer().equals(source.getPlayer().getUuid())) {
                source.sendError(Text.literal("You already have an outgoing adoption request to this player."));
                return 1;
            }
        }
        for(Couple c : MarriageHandler.getMarriages()) {
            if(c.getPartner1().equals(source.getPlayer().getUuid()) || c.getPartner2().equals(source.getPlayer().getUuid())){
                couple = c;
                break;
            }
        }
        if(couple == null){
            source.sendError(Text.literal("You must be married to adopt someone."));
            return 1;
        }
        if(couple.getPartner1().equals(targetPlayer.getUuid()) || couple.getPartner2().equals(targetPlayer.getUuid())){
            source.sendError(Text.literal("Do you want to participate in incest? You can't adopt your partner!"));
            return 1;
        }

        AdoptRequest request = new AdoptRequest(source.getPlayer(), targetPlayer);
        AdoptRequestHandler.addAdoptionRequests(request);

        source.getPlayer().sendMessage(Text.literal("You have sent an adoption request to: ").withColor(16755200)
                .append(Text.literal(targetPlayer.getName().getString()).withColor(16733525)), false);

        targetPlayer.sendMessage(Text.literal("You have received an adoption request from: ").withColor(16755200)
                .append(Text.literal(source.getPlayer().getName().getString()).withColor(16733525))
                .append(Text.literal("\nTo accept please write /marry adopt accept ").withColor(5635925))
                .append(Text.literal(source.getPlayer().getName().getString()).withColor(5635925)), false);
        return 1;
    }

    private static int kiss(CommandContext<ServerCommandSource> context) throws CommandSyntaxException{
        ServerCommandSource source = context.getSource();

        Couple couple = null;
        boolean firstPlayerIsSource = false;
        for(Couple c : MarriageHandler.getMarriages()) {
            if(c.getPartner1().equals(source.getPlayer().getUuid())){
                couple = c;
                break;
            }else if(c.getPartner2().equals(source.getPlayer().getUuid())){
                couple = c;
                firstPlayerIsSource = true;
                break;
            }
        }
        if(couple == null){
            source.sendError(Text.literal("You can not kiss someone as you're currently not married."));
            return 1;
        }

        ServerPlayerEntity targetPlayer = null;
        if(firstPlayerIsSource){
            targetPlayer = source.getServer().getPlayerManager().getPlayer(couple.getPartner1());
        } else {
            targetPlayer = source.getServer().getPlayerManager().getPlayer(couple.getPartner2());
        }
        if(targetPlayer == null){
            source.sendError(Text.literal("You can't kiss someone who's offline sadly ):"));
            return 1;
        }

        source.getPlayer().sendMessage(Text.literal("You kissed your partner!")
                        .styled(style -> style.withColor(16755200)),
                true
        );
        targetPlayer.sendMessage(Text.literal("Your partner grabbed you closely and kissed you!")
                .styled(style -> style.withColor(16755200)),
                        true
                );

        if(targetPlayer.getWorld() instanceof ServerWorld serverWorldPlayer){
            serverWorldPlayer.spawnParticles(ParticleTypes.HEART, targetPlayer.getX(), targetPlayer.getY() + 2, targetPlayer.getZ(), 10, 0.7,1,0.7,0.5);
        }
        source.getWorld().spawnParticles(ParticleTypes.HEART, source.getPlayer().getX(),source.getPlayer().getY() + 2, source.getPlayer().getZ(),10, 0.7,1,0.7,0.5);
        return 1;
    }

    private static int hug(CommandContext<ServerCommandSource> context) throws CommandSyntaxException{
        ServerCommandSource source = context.getSource();

        Couple couple = null;
        boolean firstPlayerIsSource = false;
        for(Couple c : MarriageHandler.getMarriages()) {
            if(c.getPartner1().equals(source.getPlayer().getUuid())){
                couple = c;
                firstPlayerIsSource = false;
                break;
            }else if(c.getPartner2().equals(source.getPlayer().getUuid())){
                couple = c;
                firstPlayerIsSource = true;
                break;
            }
        }
        if(couple == null){
            source.sendError(Text.literal("You can not kiss someone as you're currently not married."));
            return 1;
        }

        ServerPlayerEntity targetPlayer = null;
        if(firstPlayerIsSource){
            targetPlayer = source.getServer().getPlayerManager().getPlayer(couple.getPartner1());
        } else {
            targetPlayer = source.getServer().getPlayerManager().getPlayer(couple.getPartner2());
        }
        if(targetPlayer == null){
            source.sendError(Text.literal("You can't kiss someone who's offline sadly ):"));
            return 1;
        }

        source.getPlayer().sendMessage(Text.literal("You hugged your partner tightly!")
                        .styled(style -> style.withColor(16755200)),
                true
        );
        targetPlayer.sendMessage(Text.literal("Your partner has hugged you tightly!")
                        .styled(style -> style.withColor(16755200)),
                true
        );

        source.getWorld().spawnParticles(ParticleTypes.HAPPY_VILLAGER, targetPlayer.getX(), targetPlayer.getY() + 2, targetPlayer.getZ(), 10, 1,1,1,0.5);
        source.getWorld().spawnParticles(ParticleTypes.HAPPY_VILLAGER, source.getPlayer().getX(),source.getPlayer().getY() + 2, source.getPlayer().getZ(),10, 1,1,1,0.5);
        return 1;
    }

    private static int adoptAccept(CommandContext<ServerCommandSource> context) throws CommandSyntaxException{
        Collection<GameProfile> profiles = GameProfileArgumentType.getProfileArgument(context, "player");
        if (profiles.isEmpty()) {
            context.getSource().sendError(Text.literal("Player not found. Are you sure they're online?"));
            return 1;
        }

        GameProfile profile = profiles.iterator().next();
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity targetPlayer = source.getServer().getPlayerManager().getPlayer(profile.getId());

        Couple couple = null;
        AdoptRequest request = null;
        if(targetPlayer == null) {
            source.sendError(Text.literal("You can only accept adopt requests when the requester is online!"));
            return 1;
        }
        for(AdoptRequest r : AdoptRequestHandler.getAdoptionRequests()) {
            if(r.getProposer().equals(targetPlayer.getUuid()) && r.getTarget().equals(source.getPlayer().getUuid())) {
                request = r;
                break;
            }
        }
        if(request == null){
            source.sendError(Text.literal("You do not have an adoption request from this player."));
            return 1;
        }
        for(Couple c : MarriageHandler.getMarriages()) {
            if(c.getChildren().contains(source.getPlayer().getUuid())) {
                source.sendError(Text.literal("You are already adopted. You must leave your parents before getting adopted by another married couple."));
                return 1;
            }else if(c.getPartner1().equals(targetPlayer.getUuid()) || c.getPartner2().equals(targetPlayer.getUuid())) {
                couple = c;
                break;
            }
        }
        if(couple == null){
            source.sendError(Text.literal("Somehow, the player who sent you that adoption request, is not married."));
            return 1;
        }

        AdoptRequestHandler.removeAdoptionRequests(request);
        MarriageHandler.removeMarriage(couple);
        couple.addChild(source.getPlayer());
        MarriageHandler.addMarriage(couple);

        source.getServer().getPlayerManager().broadcast(Text.literal(source.getPlayer().getName().getString()).withColor(43520)
                .append(Text.literal(" has been adopted by ").withColor(5635925))
                .append(Text.literal(targetPlayer.getName().getString()).withColor(43520))
                .append("!").withColor(5635925), false);
        return 1;
    }

    private static int adoptReject(CommandContext<ServerCommandSource> context) throws CommandSyntaxException{
        Collection<GameProfile> profiles = GameProfileArgumentType.getProfileArgument(context, "player");
        if (profiles.isEmpty()) {
            context.getSource().sendError(Text.literal("Player not found. Are you sure they're online?"));
            return 1;
        }

        GameProfile profile = profiles.iterator().next();
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity sourcePlayer = source.getPlayer();

        if (sourcePlayer == null) {
            source.sendError(Text.literal("This command can only be run by a player."));
            return 1;
        }
        UUID sourceUUID = sourcePlayer.getUuid();
        UUID targetUUID;

        ServerPlayerEntity targetPlayer = source.getServer().getPlayerManager().getPlayer(profile.getId());
        if (targetPlayer != null) {
            targetUUID = targetPlayer.getUuid();
        } else {
            Optional<GameProfile> cachedProfile = source.getServer().getUserCache().findByName(profile.getName());
            if (cachedProfile.isPresent()) {
                targetUUID = cachedProfile.get().getId();
            } else {
                source.sendError(Text.literal("This player could not be found. Are you sure you spelled the name correctly?"));
                return 1;
            }
        }

        AdoptRequest request = null;
        for(AdoptRequest ar : AdoptRequestHandler.getAdoptionRequests()){
            if(ar.getProposer().equals(targetUUID)){
                request = ar;
                break;
            }
        }
        if(request == null){
            source.sendError(Text.literal("No adoption requests from this player could be found."));
            return 1;
        }

        AdoptRequestHandler.removeAdoptionRequests(request);
        source.getPlayer().sendMessage(Text.literal("You've rejected the adoption request.").withColor(16755200));
        return 1;
    }

    private static int adoptLeave(CommandContext<ServerCommandSource> context) throws CommandSyntaxException{
        ServerCommandSource source = context.getSource();

        Couple couple = null;
        for(Couple c : MarriageHandler.getMarriages()) {
            if(c.getChildren().contains(source.getPlayer().getUuid())) {
                couple = c;
                break;
            }
        }
        if(couple == null){
            source.sendError(Text.literal("You can't turn yourself into an orphan, as you already are one."));
            return 1;
        }

        MarriageHandler.removeMarriage(couple);
        couple.removeChild(source.getPlayer());
        MarriageHandler.addMarriage(couple);
        source.getPlayer().sendMessage(Text.literal("You've left your family in the dark and have become an orphan.").withColor(16755200));
        return 1;
    }

    private static int changeRelationshipType(CommandContext<ServerCommandSource> context) throws CommandSyntaxException{
        ServerCommandSource source = context.getSource();
        String type = StringArgumentType.getString(context, "Straight|Gay|Lesbian");

        Couple couple = null;
        for(Couple c : MarriageHandler.getMarriages()) {
            if(c.getPartner1().equals(source.getPlayer().getUuid()) || c.getPartner2().equals(source.getPlayer().getUuid())) {
                couple = c;
                break;
            }
        }
        if(couple == null){
            source.sendError(Text.literal("You can't change your relationship type, as you're BIyourself ;p"));
            return 1;
        }

        try{
            RelationshipType relationshipType = RelationshipType.valueOf(type.toUpperCase());
            MarriageHandler.removeMarriage(couple);
            couple.setRelationshipType(relationshipType);
            MarriageHandler.addMarriage(couple);
        }catch(IllegalArgumentException e){
            source.sendError(Text.literal(type + " is not a valid relationship type. [STRAIGHT, GAY, LESBIAN]"));
            return 1;
        }

        source.getPlayer().sendMessage(Text.literal("Changed your relationship type to: " + type).withColor(16755200));
        return 1;
    }
}

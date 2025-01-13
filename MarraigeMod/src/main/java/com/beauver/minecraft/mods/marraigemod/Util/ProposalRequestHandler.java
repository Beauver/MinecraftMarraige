package com.beauver.minecraft.mods.marraigemod.Util;

import com.beauver.minecraft.mods.marraigemod.Objects.ProposalRequest;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ProposalRequestHandler {

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final Path path = Paths.get("mods/MarriageMod/proposals.json");
    private static List<ProposalRequest> proposals = new ArrayList<>();

    static {
        try {
            if (!Files.exists(path)) {
                Files.createDirectories(path.getParent());
                Files.createFile(path);
                saveProposals();
            } else {
                loadProposals();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadProposals() {
        try (Reader reader = Files.newBufferedReader(path)) {
            Type listType = new TypeToken<List<ProposalRequest>>() {}.getType();
            proposals = gson.fromJson(reader, listType);

            if (proposals == null) {
                proposals = new ArrayList<>();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveProposals() {
        try (Writer writer = Files.newBufferedWriter(path)) {
            gson.toJson(proposals, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void addProposal(ProposalRequest proposal) {
        proposals.add(proposal);
        saveProposals();
    }

    public static void removeProposal(ProposalRequest proposal) {
        proposals.remove(proposal);
        saveProposals();
    }

    public static List<ProposalRequest> getProposals() {
        loadProposals();
        return new ArrayList<>(proposals);
    }
}

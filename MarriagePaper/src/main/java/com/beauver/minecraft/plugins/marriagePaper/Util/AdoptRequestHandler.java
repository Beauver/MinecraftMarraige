package com.beauver.minecraft.plugins.marriagePaper.Util;


import com.beauver.minecraft.plugins.marriagePaper.Classes.AdoptRequest;
import com.beauver.minecraft.plugins.marriagePaper.MarriagePaper;
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

public class AdoptRequestHandler {

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final Path path = Path.of(MarriagePaper.plugin.getDataPath() + "/adoption-requests.json");
    private static List<AdoptRequest> adoptionRequests = new ArrayList<>();

    static {
        try {
            if (!Files.exists(path)) {
                Files.createDirectories(path.getParent());
                Files.createFile(path);
                saveAdoptionRequests();
            } else {
                loadAdoptionRequests();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadAdoptionRequests() {
        try (Reader reader = Files.newBufferedReader(path)) {
            Type listType = new TypeToken<List<AdoptRequest>>() {}.getType();
            adoptionRequests = gson.fromJson(reader, listType);

            if (adoptionRequests == null) {
                adoptionRequests = new ArrayList<>();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveAdoptionRequests() {
        try (Writer writer = Files.newBufferedWriter(path)) {
            gson.toJson(adoptionRequests, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void addAdoptionRequests(AdoptRequest proposal) {
        adoptionRequests.add(proposal);
        saveAdoptionRequests();
    }

    public static void removeAdoptionRequests(AdoptRequest proposal) {
        adoptionRequests.remove(proposal);
        saveAdoptionRequests();
    }

    public static List<AdoptRequest> getAdoptionRequests() {
        loadAdoptionRequests();
        return new ArrayList<>(adoptionRequests);
    }

}

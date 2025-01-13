package com.beauver.minecraft.plugins.marriagePaper.Util;

import com.beauver.minecraft.plugins.marriagePaper.Classes.Couple;
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

public class MarriageHandler {

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final Path path = Path.of(MarriagePaper.plugin.getDataPath() + "/marriages.json");
    private static List<Couple> marriages = new ArrayList<>();

    static {
        try {
            if (!Files.exists(path)) {
                Files.createDirectories(path.getParent());
                Files.createFile(path);
                saveMarriages();
            } else {
                loadMarriages();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadMarriages() {
        try (Reader reader = Files.newBufferedReader(path)) {
            Type listType = new TypeToken<List<Couple>>() {}.getType();
            marriages = gson.fromJson(reader, listType);

            if (marriages == null) {
                marriages = new ArrayList<>();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveMarriages() {
        try (Writer writer = Files.newBufferedWriter(path)) {
            gson.toJson(marriages, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void addMarriage(Couple couple) {
        marriages.add(couple);
        saveMarriages();
    }

    public static void removeMarriage(Couple couple) {
        marriages.remove(couple);
        saveMarriages();
    }

    public static List<Couple> getMarriages() {
        loadMarriages();
        return new ArrayList<>(marriages);
    }

}

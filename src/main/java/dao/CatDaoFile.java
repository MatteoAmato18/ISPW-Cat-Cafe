package dao;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import entity.Cat;

import java.io.*;
import java.lang.reflect.Type;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CatDaoFile implements GenericDao<Cat> {
    private static final String FILE_PATH = "cats.json";
    private final Gson gson;
    private final List<Cat> cats;

    public CatDaoFile() {
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
        this.cats = loadFromFile();
    }

    /* ----------- I/O ----------- */
    private List<Cat> loadFromFile() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return new ArrayList<>();

        try (Reader reader = new FileReader(file)) {
            Type listType = new TypeToken<List<Cat>>() {}.getType();
            List<Cat> loaded = gson.fromJson(reader, listType);
            return loaded != null ? loaded : new ArrayList<>();
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private void saveToFile() {
        try (Writer writer = new FileWriter(FILE_PATH)) {
            gson.toJson(cats, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* ----------- CRUD ----------- */

    @Override
    public void create(Cat cat) throws SQLException {
        if (read(cat.getNameCat()) != null) {
            throw new SQLException("Gatto già presente: " + cat.getNameCat());
        }
        cats.add(cat);
        saveToFile();
    }

    @Override
    public Cat read(Object... keys) {
        if (keys == null || keys.length == 0) return null;
        String name = (String) keys[0];
        return cats.stream()
                .filter(cat -> cat.getNameCat().equals(name))
                .findFirst()
                .orElse(null);
    }

    @Override
    public void update(Cat cat) throws SQLException {
        for (int i = 0; i < cats.size(); i++) {
            if (cats.get(i).getNameCat().equals(cat.getNameCat())) {
                cats.set(i, cat);
                saveToFile();
                return;
            }
        }
        throw new SQLException("Gatto non trovato: " + cat.getNameCat());
    }

    @Override
    public void delete(Object... keys) throws SQLException {
        if (keys == null || keys.length == 0) throw new SQLException("No key provided");
        String name = (String) keys[0];
        boolean removed = cats.removeIf(cat -> cat.getNameCat().equals(name));
        if (!removed) throw new SQLException("Gatto non trovato: " + name);
        saveToFile();
    }

    @Override
    public List<Cat> readAll() {
        return new ArrayList<>(cats);
    }

    public List<Cat> readAdoptableCats() {
        List<Cat> adoptable = new ArrayList<>();
        for (Cat cat : cats) {
            if (!cat.isStateAdoption()) {
                adoptable.add(cat);
            }
        }
        return adoptable;
    }
}

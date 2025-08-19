package org.guercifzone;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

public class JsonFileHandler {
    private String filePath;

    public JsonFileHandler(String filePath) {
        this.filePath = filePath;
    }

    public List<Reciter> readReciters() {
        List<Reciter> reciters = new ArrayList<>();
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                return reciters;
            }

            BufferedReader reader = new BufferedReader(new FileReader(file));
            StringBuilder jsonContent = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonContent.append(line);
            }
            reader.close();

            JSONArray jsonArray = new JSONArray(jsonContent.toString());
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Reciter reciter = new Reciter(
                        jsonObject.getString("name"),
                        jsonObject.getString("image"),
                        jsonObject.getString("link"),
                        jsonObject.getString("description")
                );
                reciters.add(reciter);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return reciters;
    }

    public void writeReciters(List<Reciter> reciters) {
        try {
            JSONArray jsonArray = new JSONArray();
            for (Reciter reciter : reciters) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("name", reciter.getName());
                jsonObject.put("image", reciter.getImage());
                jsonObject.put("link", reciter.getLink());
                jsonObject.put("description", reciter.getDescription());
                jsonArray.put(jsonObject);
            }

            FileWriter writer = new FileWriter(filePath);
            writer.write(jsonArray.toString(4)); // 4 spaces for indentation
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addReciter(Reciter reciter) {
        List<Reciter> reciters = readReciters();
        reciters.add(reciter);
        writeReciters(reciters);
    }
}

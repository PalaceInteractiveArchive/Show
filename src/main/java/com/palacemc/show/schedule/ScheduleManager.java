package com.palacemc.show.schedule;

import com.palacemc.show.handlers.schedule.ShowDay;
import com.palacemc.show.handlers.schedule.ShowType;
import com.palacemc.show.handlers.schedule.TimeStorage;
import org.bukkit.Bukkit;
import org.json.JSONArray;
import org.json.JSONObject;
import us.mcmagic.parkmanager.ParkManager;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class ScheduleManager {
    public String url = "https://spreadsheets.google.com/feeds/cells/10TSt2OhCQGb8Wh_uUn-PLZExmuLP6ROKXCaywN_Ai1U/od6/public/basic?alt=json";
    private List<ScheduledShow> shows = new ArrayList<>();

    public ScheduleManager() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(ParkManager.getInstance(), this::update, 0L, 36000L);
    }

    private static JSONObject readJsonFromUrl(String url) {
        try (InputStream is = new URL(url).openStream()) {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            JSONObject json = new JSONObject(jsonText);
            return json;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    public void update() {
        JSONObject obj = readJsonFromUrl(url);
        if (obj == null) {
            return;
        }
        JSONArray array = obj.getJSONObject("feed").getJSONArray("entry");
        shows.clear();
        TimeStorage timeStorage = new TimeStorage();
        for (int i = 0; i < array.length(); i++) {
            JSONObject ob = array.getJSONObject(i);
            JSONObject sch = ob.getJSONObject("content");
            JSONObject id = ob.getJSONObject("title");
            String column = id.getString("$t");
            Integer row = Integer.parseInt(column.substring(1, 2));
            if (column.substring(0, 1).equalsIgnoreCase("a")) {
                timeStorage.add(row, sch.getString("$t"));
                continue;
            }
            ShowType name = ShowType.fromString(sch.getString("$t"));
            ScheduledShow show = new ScheduledShow(name, dayFromCellID(id.getString("$t")), timeStorage.getTime(row));
            shows.add(show);
        }
    }

    public List<ScheduledShow> getShows() {
        return new ArrayList<>(shows);
    }

    private ShowDay dayFromCellID(String s) {
        String column = s.substring(0, 1);
        Integer row = Integer.parseInt(s.substring(1));
        ShowDay day = null;
        switch (column.toLowerCase()) {
            case "b":
                day = ShowDay.MONDAY;
                break;
            case "c":
                day = ShowDay.TUESDAY;
                break;
            case "d":
                day = ShowDay.WEDNESDAY;
                break;
            case "e":
                day = ShowDay.THURSDAY;
                break;
            case "f":
                day = ShowDay.FRIDAY;
                break;
            case "g":
                day = ShowDay.SATURDAY;
                break;
            case "h":
                day = ShowDay.SUNDAY;
                break;
        }
        return day;
    }
}
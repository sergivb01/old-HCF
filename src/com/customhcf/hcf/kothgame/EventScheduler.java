
package com.customhcf.hcf.kothgame;

import com.customhcf.hcf.HCF;
import com.google.common.primitives.Ints;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class EventScheduler {
    private static final String FILE_NAME = "event-schedules.txt";
    private static final long QUERY_DELAY = TimeUnit.SECONDS.toMillis(60);
    private final Map<LocalDateTime, String> scheduleMap = new LinkedHashMap<LocalDateTime, String>();
    private final HCF plugin;
    private long lastQuery;

    public EventScheduler(HCF plugin) {
        this.plugin = plugin;
        this.reloadSchedules();
    }

    private static LocalDateTime getFromString(String input) {
        if (!input.contains(",")) {
            return null;
        }
        String[] args = input.split(",");
        if (args.length != 5) {
            return null;
        }
        Integer year = Ints.tryParse((String)args[0]);
        if (year == null) {
            return null;
        }
        Integer month = Ints.tryParse((String)args[1]);
        if (month == null) {
            return null;
        }
        Integer day = Ints.tryParse((String)args[2]);
        if (day == null) {
            return null;
        }
        Integer hour = Ints.tryParse((String)args[3]);
        if (hour == null) {
            return null;
        }
        Integer minute = Ints.tryParse((String)args[4]);
        if (minute == null) {
            return null;
        }
        return LocalDateTime.of((int)year, month, (int)day, (int)hour, (int)minute);
    }

    private void reloadSchedules() {
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader((InputStream)new FileInputStream(new File(this.plugin.getDataFolder(), "event-schedules.txt")), StandardCharsets.UTF_8));
            Throwable throwable = null;
            try {
                String currentLine;
                while ((currentLine = bufferedReader.readLine()) != null) {
                    LocalDateTime localDateTime;
                    String[] args;
                    if (currentLine.startsWith("#") || (args = (currentLine = currentLine.trim()).split(":")).length != 2 || (localDateTime = EventScheduler.getFromString(args[0])) == null) continue;
                    this.scheduleMap.put(localDateTime, args[1]);
                }
            }
            catch (Throwable currentLine) {
                throwable = currentLine;
                throw currentLine;
            }
            finally {
                if (bufferedReader != null) {
                    if (throwable != null) {
                        try {
                            bufferedReader.close();
                        }
                        catch (Throwable currentLine) {
                            throwable.addSuppressed(currentLine);
                        }
                    } else {
                        bufferedReader.close();
                    }
                }
            }
        }
        catch (FileNotFoundException ex2) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "Could not find file event-schedules.txt");
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public Map<LocalDateTime, String> getScheduleMap() {
        long millis = System.currentTimeMillis();
        if (millis - QUERY_DELAY > this.lastQuery) {
            this.reloadSchedules();
            this.lastQuery = millis;
        }
        return this.scheduleMap;
    }
}


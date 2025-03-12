/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package getters;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import model.Location;
import nsofiasLib.databases.Mongo;
import nsofiasLib.others.SimpleDaemon;

/**
 *
 * @author gsofi
 */
public class LocationsGetterDaemon extends SimpleDaemon {

    Mongo myMongo;
    final Map<String, Location> data = new ConcurrentHashMap<>();

    public LocationsGetterDaemon(Mongo myMongo, int sleep, int step) {
        super(sleep, step);
        this.myMongo = myMongo;
    }

    @Override
    public void processData() {
        try {
            data.putAll(new LocationsGetter().get());
            data.values().stream().forEach(v -> {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
                LocalDateTime localDateTime = LocalDateTime.parse(v.getLast_updated(), formatter);
                myMongo.add("locations",v,  localDateTime);
            });
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
    }

    public Map<String, Location> getData() {
        return data;
    }

}

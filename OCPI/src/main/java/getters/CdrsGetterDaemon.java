package getters;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import model.CDR;
import nsofiasLib.databases.Mongo;
import nsofiasLib.others.SimpleDaemon;

/**
 *
 * @author gsofi
 */
public class CdrsGetterDaemon extends SimpleDaemon {

    Mongo myMongo;

    final Map<String, CDR> data = new ConcurrentHashMap<>();

    public CdrsGetterDaemon(Mongo myMongo, int sleep, int step) {
        super(sleep, step);
        this.myMongo = myMongo;
    }

    @Override
    public void processData() {
        try {
            data.putAll(new CdrsGetter().get());
            data.values().stream().forEach(v -> {
                //
                //DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.nnnnnnnXXX");
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSXXX");

                LocalDateTime localDateTime = LocalDateTime.parse(v.getStart_date_time(), formatter);
                //
                myMongo.add("cdrs", v, localDateTime);
            });
        } catch (com.mongodb.MongoWriteException ex) {
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
    }

    public Map<String, CDR> getData() {
        return data;
    }
}

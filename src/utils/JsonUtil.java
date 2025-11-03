package utils;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import tasks.AbstractTask;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class JsonUtil {
    public static String convertToJson(Object obj, Gson gson) {
        return gson.toJson(obj);
    }

    public  static <T extends AbstractTask<T>> T convertFromJson(String json, Class<T> tClass, Gson gson) {
        return gson.fromJson(json, tClass);
    }

    public static class CustomTypeAdapters {
        public static TypeAdapter<LocalDateTime> ofLocalDateTime(DateTimeFormatter dateTimeFormatter) {
            return new LocalDateTimeTypeAdapter(dateTimeFormatter);
        }

        public static TypeAdapter<Duration> ofDuration() {
            return new DurationInMinutesTypeAdapter();
        }

        static class LocalDateTimeTypeAdapter extends TypeAdapter<LocalDateTime> {
            private final DateTimeFormatter dateTimeFormatter;

            LocalDateTimeTypeAdapter(DateTimeFormatter dateTimeFormatter) {
                this.dateTimeFormatter = dateTimeFormatter;
            }

            @Override
            public void write(JsonWriter jsonWriter, LocalDateTime localDateTime) throws IOException {
                if (localDateTime == null)
                    jsonWriter.nullValue();
                else
                    jsonWriter.value(localDateTime.format(dateTimeFormatter));
            }

            @Override
            public LocalDateTime read(JsonReader jsonReader) throws IOException {
                if (jsonReader.peek() == JsonToken.NULL) {
                    jsonReader.nextNull();
                    return null;
                }

                return LocalDateTime.parse(jsonReader.nextString(), dateTimeFormatter);
            }
        }

        static class DurationInMinutesTypeAdapter extends TypeAdapter<Duration> {
            @Override
            public void write(JsonWriter jsonWriter, Duration duration) throws IOException {
                if (duration == null)
                    jsonWriter.nullValue();
                else
                    jsonWriter.value(duration.toMinutes());
            }

            @Override
            public Duration read(JsonReader jsonReader) throws IOException {
                if (jsonReader.peek() == JsonToken.NULL) {
                    jsonReader.nextNull();
                    return null;
                }
                return Duration.ofMinutes(jsonReader.nextLong());
            }
        }
    }
}

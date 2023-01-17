package ru.yandex.practicum.ewm.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.boot.jackson.JsonComponent;
import ru.yandex.practicum.ewm.event.model.EventState;

import java.io.IOException;

public class EventStateCompositeSerializer {

    @JsonComponent(type = EventState.class)
    public static class EventStateDeserializer extends JsonDeserializer<EventState> {

        @Override
        public EventState deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            try {
                return EventState.valueOf(p.getValueAsString());

            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Некорректное значение типа события в условиях фильтрации событий.");
            }
        }
    }

    @JsonComponent(type = EventState.class)
    public static class EventStateSerializer extends JsonSerializer<EventState> {

        @Override
        public void serialize(EventState value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            gen.writeString(value.toString());
        }
    }
}

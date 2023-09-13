package com.fsbtech.interviews.repository.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.fasterxml.jackson.module.jsonSchema.JsonSchemaGenerator;
import com.fsbtech.interviews.entities.Category;
import com.fsbtech.interviews.entities.Event;
import com.fsbtech.interviews.entities.MarketRefType;
import com.fsbtech.interviews.entities.SubCategory;
import com.fsbtech.interviews.repository.EventRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class EventRepositoryImpl implements EventRepository {

    private final Map<Integer, Event> events = new ConcurrentHashMap<>();

    public void saveEvent(Event event) {
        events.put(event.getId(), event);
    }

    public Event updateEventStatus(Integer id) {
        final Event event = events.get(id);
        if (Objects.nonNull(event)) {
            final Event updatedEvent = new Event(
                    event.getId(),
                    event.getName(),
                    event.getSubCategory(),
                    event.getMarketRefTypes(),
                    true);
            return events.put(id, updatedEvent);
        } else {
            return null;
        }
    }

    public Event attachMarketRefTypeToEvent(Integer id, MarketRefType marketRefType) {
        final Event event = events.get(id);
        if (Objects.nonNull(event)) {
            final Collection<MarketRefType> marketRefTypes = event.getMarketRefTypes();
            marketRefTypes.add(marketRefType);
            final Event updatedEvent = new Event(
                    event.getId(),
                    event.getName(),
                    event.getSubCategory(),
                    marketRefTypes,
                    event.getCompleted());
            return events.put(id, updatedEvent);
        } else {
            return null;
        }
    }

    public Event removeMarketRefTypeFromEvent(Integer id, MarketRefType marketRefType) {
        final Event event = events.get(id);
        if (Objects.nonNull(event)) {
            final Collection<MarketRefType> marketRefTypes = event.getMarketRefTypes();
            marketRefTypes.removeIf(refType -> refType.getMarketRefId().equals(marketRefType.getMarketRefId()));
            final Event updatedEvent = new Event(
                    event.getId(),
                    event.getName(),
                    event.getSubCategory(),
                    marketRefTypes,
                    event.getCompleted());
            return events.put(id, updatedEvent);
        } else {
            return null;
        }
    }

    public Collection<String> findAllIncompleteEvents(String cat, String subCat, String marketRefName) {
        final Collection<String> result = events
                .entrySet()
                .stream()
                .filter(event -> !event.getValue().getCompleted())
                .filter(event -> Objects.isNull(cat) || event.getValue().getSubCategory().getCategory().getRef().contains(cat))
                .filter(event -> Objects.isNull(subCat) || event.getValue().getSubCategory().getRef().contains(subCat))
                .filter(event -> Objects.isNull(marketRefName) || event.getValue().getMarketRefTypes().stream().anyMatch(marketRefType -> marketRefType.getMarketRefName().contains(marketRefName)))
                .map(entry -> entry.getValue().getName())
                .collect(Collectors.toList());
        return result;
    }

    public String dumpFullStructure() {
        final ObjectMapper mapper = new ObjectMapper();
        final JsonSchemaGenerator schemaGen = new JsonSchemaGenerator(mapper);
        JsonSchema schema;
        try {
            schema = schemaGen.generateSchema(Event[].class);
            return mapper.writeValueAsString(schema);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}

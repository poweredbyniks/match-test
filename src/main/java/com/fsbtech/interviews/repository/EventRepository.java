package com.fsbtech.interviews.repository;

import com.fsbtech.interviews.entities.Event;
import com.fsbtech.interviews.entities.MarketRefType;

import java.util.Collection;

public interface EventRepository {

    void saveEvent(Event event);

    Event updateEventStatus(Integer id);

    Event attachMarketRefTypeToEvent(Integer id, MarketRefType marketRefType);

    Event removeMarketRefTypeFromEvent(Integer id, MarketRefType marketRefType);

    Collection<String> findAllIncompleteEvents(String cat, String subcat, String marketRefName);

    String dumpFullStructure();


}

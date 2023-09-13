package com.fsbtech.interviews.client.impl;

import com.fsbtech.interviews.client.Client;
import com.fsbtech.interviews.entities.Event;
import com.fsbtech.interviews.entities.MarketRefType;
import com.fsbtech.interviews.repository.EventRepository;

import java.util.Collection;
import java.util.Objects;

/*

Hello dear reviewer(s),
let me explain this solution. The task was to implement Client interface without specified exact technological stack,
thus I decided to implement the interface using plain Java without using Spring or other IoC frameworks (only added as
a dependency a few useful things).
As far as I understood there was no need to implement REST API or any other protocols to communicate with this app
because it will be used like a dependency with no need to run a server.
More over I tried to avoid modifying current data model, but not adding functionality to it.
To sum up: based on the task, I tried to adhere to the KISS principle, and finally it appeared to be a console app

 */
public class ClientImpl implements Client {

    private final EventRepository eventRepository;

    public ClientImpl(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public void addEvent(Event event) {
        eventRepository.saveEvent(event);
        System.out.println("Successfully saved event with id " + event.getId());
    }

    public void eventCompleted(Integer id) {
        if (Objects.nonNull(eventRepository.updateEventStatus(id))) {
            System.out.println("Successfully updated event with COMPLETED status and id " + id);
        } else {
            System.out.println("Error in updating event with COMPLETED status and id " + id);
        }
    }

    public void attachMarketRefTypeToEvent(Integer id, MarketRefType marketRefType) {
        if (Objects.nonNull(eventRepository.attachMarketRefTypeToEvent(id, marketRefType))) {
            System.out.println("Successfully attached marketRefType to event with id " + id);
        } else {
            System.out.println("Error in attaching marketRefType to event with id " + id);
        }
    }

    public void removeMarketRefTypeFromEvent(Integer id, MarketRefType marketRefType) {
        if (Objects.nonNull(eventRepository.removeMarketRefTypeFromEvent(id, marketRefType))) {
            System.out.println("Successfully removed marketRefType from event with id " + id);
        } else {
            System.out.println("Error in removing marketRefType from event with id " + id);
        }
    }

    public Collection<String> futureEventNamesCollection(String cat, String subcat, String marketRefName) {
        return eventRepository.findAllIncompleteEvents(cat, subcat, marketRefName);
    }

    public String dumpFullStructure() {
        return eventRepository.dumpFullStructure();
    }
}

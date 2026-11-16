package com.campus.model;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Entity representing a Campus Event.
 */
public class Event implements Serializable {
    private final String eventId;
    private final String eventName;
    private final LocalDate eventDate;
    private final String venue;
    private int availableSeats;

    public Event(String eventId, String eventName, LocalDate eventDate, String venue, int availableSeats) {
        this.eventId = eventId;
        this.eventName = eventName;
        this.eventDate = eventDate;
        this.venue = venue;
        this.availableSeats = availableSeats;
    }

    public String getEventId() { return eventId; }
    public String getEventName() { return eventName; }
    public LocalDate getEventDate() { return eventDate; }
    public String getVenue() { return venue; }
    public int getAvailableSeats() { return availableSeats; }
    public void setAvailableSeats(int availableSeats) { this.availableSeats = availableSeats; }

    @Override
    public String toString() {
        return eventName + " [" + eventId + "] (" + availableSeats + " seats left)";
    }
}

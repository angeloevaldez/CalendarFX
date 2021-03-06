/**
 * Copyright (C) 2015, 2016 Dirk Lemmermann Software & Consulting (dlsc.com) 
 * 
 * This file is part of CalendarFX.
 */

package com.calendarfx.view;

import com.calendarfx.model.Calendar;
import com.calendarfx.model.Entry;
import com.calendarfx.util.LoggingDomain;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import java.time.ZonedDateTime;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

class CreateDeleteHandler {

    private DateControl dateControl;

    public CreateDeleteHandler(DateControl control) {
        this.dateControl = requireNonNull(control);

        dateControl.addEventHandler(MouseEvent.MOUSE_CLICKED, this::createEntry);
        dateControl.addEventHandler(KeyEvent.KEY_PRESSED, this::deleteEntries);
    }

    private void createEntry(MouseEvent evt) {
        if (evt.getButton().equals(MouseButton.PRIMARY) && evt.getClickCount() == 2) {
            LoggingDomain.VIEW.fine("create entry mouse event received inside control: " + dateControl.getClass().getSimpleName());

            ZonedDateTime time = ZonedDateTime.now();
            if (dateControl instanceof ZonedDateTimeProvider) {
                ZonedDateTimeProvider provider = (ZonedDateTimeProvider) dateControl;
                time = provider.getZonedDateTimeAt(evt.getX(), evt.getY());
            }

            Optional<Calendar> calendar = dateControl.getCalendarAt(evt.getX(), evt.getY());

            if (time != null) {
                dateControl.createEntryAt(time, calendar.orElse(null));
            }
        }
    }

    private void deleteEntries(KeyEvent evt) {
        switch (evt.getCode()) {
        case DELETE:
        case BACK_SPACE:
            for (Entry<?> entry : dateControl.getSelections()) {
                if (entry.isRecurrence()) {
                    entry = entry.getRecurrenceSourceEntry();
                }

                Calendar calendar = entry.getCalendar();
                if (calendar != null && !calendar.isReadOnly()) {
                    entry.removeFromCalendar();
                }
            }
            dateControl.clearSelection();
            break;
        case F5:
            dateControl.refreshData();
        default:
            break;
        }
    }
}
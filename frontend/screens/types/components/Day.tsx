import {CalendarEvent} from "../../../types/calendar/CalendarEvent";

export interface Day {
    day: string;
    events: CalendarEvent[];
}
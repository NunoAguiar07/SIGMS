import {CalendarEvent} from "../../../types/calendar/CalendarEvent";

export interface DayType {
    day: string;
    events: CalendarEvent[];
}
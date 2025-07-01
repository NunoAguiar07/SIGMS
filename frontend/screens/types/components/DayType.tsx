import {CalendarEvent} from "../../../types/calendar/CalendarEvent";

export interface DayType {
    day: string;
    events: CalendarEvent[];
    onClickProfile: (id:number) => void;
    onClickRoom: (id:number) => void;
}
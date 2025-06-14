import {EventType} from "./EventType";

export interface NotificationEvent {
    type: EventType;
    data: any
}
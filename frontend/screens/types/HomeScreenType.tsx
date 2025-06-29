import {Lecture} from "../../types/calendar/Lecture";
import {Notification} from "../../types/notifications/Notification";


export interface HomeScreenType {
    onLogout: () => void;
    username: string,
    schedule: Lecture[],
    notifications: Notification[],
    clearNotification:  (notificationToBeRemoved: Notification) => void
}
import {Lecture} from "../../types/calendar/Lecture";
import {Notification} from "../../types/notifications/Notification";
import {ScheduleItem} from "../../types/ScheduleItem";


export interface HomeScreenType {
    onLogout: () => void;
    username: string,
    schedule: ScheduleItem[],
    notifications: Notification[],
    clearNotification:  (notificationToBeRemoved: Notification) => void,
    onClickProfile: (id:number) => void,
    onClickRoom: (roomId: number) => void

}
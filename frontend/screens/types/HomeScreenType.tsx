
import {Notification} from "../../types/notifications/Notification";
import {LectureWithTeacher} from "../../types/LectureWithTeacher";


export interface HomeScreenType {
    shouldShowCalendar: boolean;
    onLogout: () => void;
    username: string,
    schedule: LectureWithTeacher[],
    notifications: Notification[],
    clearNotification:  (notificationToBeRemoved: Notification) => void,
    onClickProfile: (id:number) => void,
    onClickRoom: (roomId: number) => void

}
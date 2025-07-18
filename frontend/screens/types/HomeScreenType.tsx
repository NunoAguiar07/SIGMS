
import {Notification} from "../../types/notifications/Notification";

export interface HomeScreenType {
    shouldShowCalendar: boolean;
    onLogout: () => void;
    username: string,
    notifications: Notification[],
    clearNotification:  (notificationToBeRemoved: Notification) => void,
    onClickProfile: (id:number) => void,
    onClickRoom: (roomId: number) => void,
    getEventsForDay: (dayName: string) => any[],
    getCurrentDay: () => string,
    selectedDay: string,
    setSelectedDay: (day: string) => void,
    daysOrder: string[],
    navigateDay: (direction: 'prev' | 'next') => void
}
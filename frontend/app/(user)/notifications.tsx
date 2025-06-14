import {useNotifications} from "../../hooks/notifications/useNotifications";
import {NotificationsScreen} from "../../screens/mainScreens/NotificationsScreen";

const Notifications = () => {
    const {notifications, clearNotification} = useNotifications()
    return (
        <NotificationsScreen notifications={notifications} clearNotification={clearNotification}/>
    )
}

export default Notifications;
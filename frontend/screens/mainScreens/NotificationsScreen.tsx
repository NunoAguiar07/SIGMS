import {View} from "react-native";
import {commonStyles} from "../css_styling/common/CommonProps";
import {NotificationComponent} from "../components/notifications/Notification";
import {Notification} from "../../types/notifications/Notification";

export const NotificationsScreen = ({notifications, clearNotification}: {notifications: Notification[], clearNotification: (notification: Notification) => void }) => {
    return (
        <View style={commonStyles.container}>
            {notifications.map((notification) => (
                <NotificationComponent notification={notification} clear={clearNotification}/>
            ))}
        </View>
    );
}
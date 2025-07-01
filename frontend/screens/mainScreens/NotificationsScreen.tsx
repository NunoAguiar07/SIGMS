import {Text, View} from "react-native";
import {NotificationComponent} from "../components/notifications/Notification";
import {Notification} from "../../types/notifications/Notification";
import {ContainerStyles, getColumnStyle} from "../css_styling/common/Containers";
import {TextStyles} from "../css_styling/common/Text";

export const NotificationsScreen = ({notifications, clearNotification}: {notifications: Notification[], clearNotification: (notification: Notification) => void }) => {
    return (
        <View style={[ContainerStyles.columnBase,ContainerStyles.backgroundSecondary,ContainerStyles.roundedContainer, ContainerStyles.border, ContainerStyles.borderPrimary, ContainerStyles.fullHeight]}>
            <View style={[ContainerStyles.roundedContainer,{backgroundColor: '#d7c1ba'}]}>
                <Text style={[TextStyles.black, TextStyles.h1]}>Notifications</Text>
            </View>
            <View style={getColumnStyle(100,10)}>
                {notifications.map((notification) => (
                    <NotificationComponent notification={notification} clear={clearNotification}/>
                ))}
            </View>
        </View>
    );
}
import {Notification} from "../../../types/notifications/Notification";
import {Text, TouchableOpacity, View} from "react-native";
import {Ionicons} from "@expo/vector-icons";
import {ContainerStyles, getColumnStyle} from "../../css_styling/common/Containers";
import {TextStyles} from "../../css_styling/common/Text";

export const NotificationComponent = ({notification, clear}: {notification: Notification, clear: (notification: Notification) => void}) => {
    return (
        <View style={[getColumnStyle(100, 10), ContainerStyles.roundedContainer, ContainerStyles.backgroundOnPrimary]}>
            <Text style={[TextStyles.light, TextStyles.h2]}>{notification.title}</Text>
            <Text>{notification.message}</Text>
            <TouchableOpacity onPress={() => {
                console.log(`Clearing ${notification.id}`)
                clear(notification)
            }}>
                <Ionicons name={'arrow-redo-outline'} size={30} color="white" />
            </TouchableOpacity>
    </View>)
}
import {Notification} from "../../../types/notifications/Notification";
import {Text, TouchableOpacity, View} from "react-native";
import {commonStyles} from "../../css_styling/common/CommonProps";
import {Ionicons} from "@expo/vector-icons";

export const NotificationComponent = ({notification, clear}: {notification: Notification, clear: (notification: Notification) => void}) => {
    return (<View style={commonStyles.card}>
        <Text>{notification.title}</Text>
        <Text>{notification.message}</Text>
        <TouchableOpacity onPress={() => {
            console.log(`Clearing ${notification.id}`)
            clear(notification)
        }}>
            <Ionicons name={'arrow-redo-outline'} size={30} color="white" />
        </TouchableOpacity>
    </View>)
}
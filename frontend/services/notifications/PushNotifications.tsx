import * as Device from "expo-device";
import * as Notifications from "expo-notifications";
import {Platform} from "react-native";

export const setUpPushNotifications = async () => {
    if (!Device.isDevice) {
        alert('Must use physical device for Push Notifications')
        return
    }

    if (Platform.OS === 'android') {
        await Notifications.setNotificationChannelAsync('SIGMS-NOTIFICATIONS', {
            name: 'The default channel for notifications of the SIGMS app',
            importance: Notifications.AndroidImportance.DEFAULT,
            vibrationPattern: [250, 0, 250, 0],
            lightColor: '#FF231F7C',
        });
    }
    const { status: existingStatus } = await Notifications.getPermissionsAsync()
    let finalStatus = existingStatus

    if (existingStatus !== 'granted') {
        const { status } = await Notifications.requestPermissionsAsync()
        finalStatus = status
    }

    if (finalStatus !== 'granted') {
        alert('Failed to get push token for push notification!')
        return
    }
}
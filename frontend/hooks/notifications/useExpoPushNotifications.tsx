import * as ExpoNotifications from "expo-notifications";
import {useEffect, useState} from "react";
import {Notification} from "../../types/notifications/Notification";
import {setUpPushNotifications} from "../../services/notifications/PushNotifications";
import {router} from "expo-router";

export const useExpoPushNotifications = () => {
    const [notifications, setNotifications] = useState<Notification[]>([]);
    ExpoNotifications.setNotificationHandler({
        handleNotification: async () => ({
            shouldPlaySound: false,
            shouldSetBadge: false,
            shouldShowBanner: true,
            shouldShowList: true,
        }),
    });
    useEffect(() => {
        setUpPushNotifications()
        const receiveNotificationSubscription = ExpoNotifications.addNotificationReceivedListener(expoNotification => {
            const title = expoNotification.request.content.title as string
            const body = expoNotification.request.content.body as string
            const url = expoNotification.request.content.data.url as string
            const notification: Notification = {
                title: title,
                message: body,
                data: {url: url}
            }
            setNotifications((prev) => [...prev, notification])
        })
        const responseReceiveNotificationSubscription = ExpoNotifications.addNotificationResponseReceivedListener(expoNotification =>{
            router.push(expoNotification.notification.request.content.data.url as string)
        })
        return () => {
            receiveNotificationSubscription.remove()
            responseReceiveNotificationSubscription.remove()
        };
    }, []);

    const clearNotification = (notificationToBeRemoved: Notification) => {
        setNotifications(notifications.filter(
            (notification) => notification === notificationToBeRemoved)
        )
    }

    return {notifications, clearNotification}
}
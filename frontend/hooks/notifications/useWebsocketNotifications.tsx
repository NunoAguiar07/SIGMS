import {useEffect, useRef, useState} from "react";
import {WebsocketInformationProps} from "../../types/websocket/WebsocketInformationProps";
import {Greet} from "../../types/notifications/Greet";
import {NotificationEvent} from "../../types/notifications/NotificationEvent";
import {EventType} from "../../types/notifications/EventType";
import {connect} from "../../services/websockets/Connect";
import {Notification} from "../../types/notifications/Notification";
import {router} from "expo-router";

export const useWebsocketNotifications = (websocketInformation: WebsocketInformationProps) => {
    const [notifications, setNotifications] = useState<Notification[]>([])
    const idCounter = useRef<number>(0)
    const websocket = useRef<WebSocket | null>(null)
    const retry = useRef<number>(0)

    const setUp = () => {
        if(websocket.current || websocketInformation.userId == -1) return
        const onConnect = (ws: WebSocket, _: Event) => {
            console.log("Greeting backend")
            const greet: Greet =  {
                userId: websocketInformation.userId
            }
            const event: NotificationEvent = {
                type: EventType.GREET,
                data: greet
            }
            ws.send(JSON.stringify(event))
        }
        const onMessage = (ws: WebSocket, ev: MessageEvent) => {
            const event: NotificationEvent = JSON.parse(ev.data)
            if(event.type === EventType.NOTIFICATION) {
                const notification = event.data as Notification
                notification.id = ++idCounter.current
                setNotifications(current => current.concat(notification))
            }
        }
        const onClose = (ws: WebSocket, ev: CloseEvent) => {
            console.log("Closing Connection")
            console.log(ev.code, ev.reason)
            websocket.current = null
            retry.current = setTimeout(() => connect({onConnect, onMessage, onClose}), 3*1000) as unknown as number
        }
        websocket.current = connect({onConnect, onMessage, onClose})
    }

    const clearNotification = (notificationToBeRemoved: Notification) => {
        setNotifications(notifications.filter(notification => notification.id !== notificationToBeRemoved.id))
        router.push(notificationToBeRemoved.data.url)
    }

    useEffect(() => {
        setUp()
        return () => {
            if (retry.current) {
                clearTimeout(retry.current);
            }
            websocket.current?.close();
            websocket.current = null;
        }
    }, [websocketInformation.userId])

    return {notifications, clearNotification}
}
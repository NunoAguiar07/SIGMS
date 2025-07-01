import React, { createContext, ReactNode, useEffect, useState } from 'react';
import AsyncStorage from '@react-native-async-storage/async-storage';
import {useWebsocketNotifications} from "../hooks/notifications/useWebsocketNotifications";
import {useExpoPushNotifications} from "../hooks/notifications/useExpoPushNotifications";
import {Notification} from "../types/notifications/Notification";
import LoadingPresentation from "../screens/auxScreens/LoadingScreen";
import {isMobile} from "../utils/DeviceType";


interface NotificationContextType {
    notifications: Notification[];
    clearNotification: (notificationToBeRemoved: Notification) => void;
}


export const NotificationContext = createContext<NotificationContextType | undefined>(undefined);

// Provider props
interface NotificationProviderProps {
    children: ReactNode;
}

// The provider component
export const NotificationProvider: React.FC<NotificationProviderProps> = ({children}) => {
    const [userId, setUserId] = useState<number>(-1);
    const [isLoading, setIsLoading] = useState(true);
    const notificationData: NotificationContextType = useNotificationsInternal(userId);
    // Fetch userId from AsyncStorage
    useEffect(() => {
        const fetchUserId = async () => {
            try {
                const storedUserId = await AsyncStorage.getItem('userId');
                if (storedUserId) {
                    setUserId(parseInt(storedUserId, 10));
                }
            } catch (error) {
                console.error('Error fetching userId from AsyncStorage:', error);
            } finally {
                setIsLoading(false);
            }
        };

        fetchUserId();
    }, []);
    if (isLoading) {return <LoadingPresentation />;}

    return (
        <NotificationContext.Provider value={notificationData}>
            {children}
        </NotificationContext.Provider>
    );
};


const useNotificationsInternal = (userId: number) => {
    if (!isMobile) {
        return useWebsocketNotifications({ userId });
    } else {
        return useExpoPushNotifications();
    }
};
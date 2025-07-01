import {Stack} from "expo-router";
import {NotificationProvider} from "../contexts/NotificationContext";

export default function RootLayout() {

    return (
        <NotificationProvider>
            <Stack screenOptions={{
                    headerShown: false, // Hides the header for all screens in (user)
                }}
            >

            </Stack>
        </NotificationProvider>
    );
}
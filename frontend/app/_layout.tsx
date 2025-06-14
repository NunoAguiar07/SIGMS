import {Stack} from "expo-router";
import {NotificationProvider} from "../contexts/NotificationContext";

export default function RootLayout() {

    return (
        <NotificationProvider>
            <Stack screenOptions={{
                    headerShown: false, // Hides the header for all screens in (user)
                }}
            >
                {/* Optional: If you still need tabs somewhere */}
                {/*<Stack.Screen name="(tabs)" options={{ headerShown: false }} />*/}
            </Stack>
        </NotificationProvider>
    );
}
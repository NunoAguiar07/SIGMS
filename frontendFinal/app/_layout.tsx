import {Stack} from "expo-router";

export default function RootLayout() {
    return (
        <Stack
            screenOptions={{
                headerShown: false, // Hides the header for all screens in (user)
            }}
        >

            {/* Optional: If you still need tabs somewhere */}
            {/*<Stack.Screen name="(tabs)" options={{ headerShown: false }} />*/}
        </Stack>
    );
}
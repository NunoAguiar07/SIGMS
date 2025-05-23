import {Stack} from "expo-router";

export default function RootLayout() {
    return (
        <Stack
            screenOptions={{
                headerShown: false, // Hides the header for all screens in (user)
            }}
        >
            {/* Public screens (no tabs) */}
            <Stack.Screen name="(public)" />

            {/* Authenticated user screens (minimal top navbar) */}
            <Stack.Screen name="(user)"  />

            {/* Optional: If you still need tabs somewhere */}
            {/*<Stack.Screen name="(tabs)" options={{ headerShown: false }} />*/}
        </Stack>
    );
}
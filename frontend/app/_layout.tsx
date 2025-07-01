import {Stack} from "expo-router";
import {NotificationProvider} from "../contexts/NotificationContext";
import {useFontsLoad} from "../hooks/useFontsLoad";
import LoadingPresentation from "../screens/auxScreens/LoadingScreen";
import {BackgroundImage} from "../screens/components/BackgroundImage";

export default function RootLayout() {
    const fontsLoaded = useFontsLoad();
    if (!fontsLoaded) {
        return <LoadingPresentation />;
    }
    return (
            <NotificationProvider>
                <Stack screenOptions={{headerShown: false}}/>
            </NotificationProvider>
    );
}
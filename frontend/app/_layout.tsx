import '../screens/css_styling/common/global.css'

import {Stack} from "expo-router";
import {NotificationProvider} from "../contexts/NotificationContext";
import {useFontsLoad} from "../hooks/useFontsLoad";
import LoadingPresentation from "../screens/auxScreens/LoadingScreen";
import {theme} from "../screens/css_styling/common/Theme";
import {ThemeProvider} from "styled-components/native";
import {BackgroundImage} from "../screens/components/BackgroundImage";


export default function RootLayout() {
    const fontsLoaded = useFontsLoad();


    if (!fontsLoaded) {
        return <LoadingPresentation />;
    }
    return (
        <ThemeProvider theme={theme}>
            <NotificationProvider>
                <BackgroundImage >
                    <Stack screenOptions={{
                        headerShown: false,
                        contentStyle: { backgroundColor: 'transparent' },
                    }}
                    />
                </BackgroundImage>
            </NotificationProvider>
        </ThemeProvider>
    );
}
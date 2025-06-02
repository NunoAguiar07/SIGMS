import {useEffect, useState} from "react";
import * as Font from 'expo-font';

export const useFontsLoad = () => {
    const [fontsLoaded, setFontsLoaded] = useState(false);

    useEffect(() => {
        async function loadFonts() {
            try {
                await Font.loadAsync({
                    'Roboto Condensed': require('../assets/fonts/roboto/RobotoCondensed-Regular.ttf'),
                    'Roboto Condensed Bold': require('../assets/fonts/roboto/RobotoCondensed-Bold.ttf'),
                });
                setFontsLoaded(true);
            } catch (error) {
                console.error('Error loading fonts', error);
            }
        }
        loadFonts();
    }, []);

    return fontsLoaded;
};
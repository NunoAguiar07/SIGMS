import {useFonts} from "expo-font";

export const useFontsLoad = () => {
    const [fontsLoaded, error] = useFonts({
        'RobotoCondensed-Regular': require('../assets/fonts/roboto/RobotoCondensed-Regular.ttf'),
        'RobotoCondensed-Light': require('../assets/fonts/roboto/RobotoCondensed-Light.ttf'),
        'RobotoCondensed-Black': require('../assets/fonts/roboto/RobotoCondensed-Black.ttf'),
        'RobotoCondensed-Bold': require('../assets/fonts/roboto/RobotoCondensed-Bold.ttf')
    })
    return fontsLoaded;
};
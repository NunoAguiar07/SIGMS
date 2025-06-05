import {Platform} from "react-native";


export const getDeviceType = () => {
    if (Platform.OS === 'web') return 'WEB';
    if (Platform.OS === 'ios') return 'IOS';
    if (Platform.OS === 'android') return 'ANDROID';
    return 'UNKNOWN';
};
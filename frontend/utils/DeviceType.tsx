import {Dimensions, Platform} from "react-native";

export const isMobile = Platform.OS !== 'web' || Dimensions.get('window').width < 768;

export const getDeviceType = () => {
    if (Platform.OS === 'web') return 'WEB';
    if (Platform.OS === 'ios') return 'IOS';
    if (Platform.OS === 'android') return 'ANDROID';
    return 'UNKNOWN';
};
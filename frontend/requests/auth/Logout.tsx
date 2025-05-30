import {useRouter} from "expo-router";
import AsyncStorage from "@react-native-async-storage/async-storage";
import {Alert} from "react-native";
import * as SecureStore from 'expo-secure-store';
import {getDeviceType} from "../../Utils/DeviceType";

const router = useRouter();

const handleLogout = async () => {
    try {
        const deviceType = getDeviceType();

        if (deviceType !== 'WEB') {
            await SecureStore.deleteItemAsync('authToken');
            await SecureStore.deleteItemAsync('refreshToken');
        }else {
            await AsyncStorage.removeItem('refreshToken');
            await AsyncStorage.multiRemove(['userId', 'universityId', 'userRole']);
        }

        Alert.alert('Logged out', 'You have been logged out.');
        router.replace('/welcome');
    } catch (error) {
        console.error('Logout failed:', error);
        Alert.alert('Logout failed', 'An error occurred while logging out.');
    }
};


export default handleLogout;

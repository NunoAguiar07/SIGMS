import AsyncStorage from "@react-native-async-storage/async-storage";
import * as SecureStore from 'expo-secure-store';

export const clearWebStorage = async () => {
    await AsyncStorage.removeItem('refreshToken');
    await AsyncStorage.multiRemove(['userId', 'universityId', 'userRole']);
};

export const clearMobileStorage = async () => {
    await SecureStore.deleteItemAsync('authToken');
    await SecureStore.deleteItemAsync('refreshToken');
};

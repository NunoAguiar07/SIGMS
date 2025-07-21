import AsyncStorage from "@react-native-async-storage/async-storage";
import * as SecureStore from 'expo-secure-store';
import api from "../services/interceptors/DeviceInterceptor";
import {apiUrl} from "../services/fetchWelcome";


/**
 * Clears all stored credentials from both web and mobile storage.
 *
 * This function removes the refresh token from AsyncStorage and deletes
 * the auth token and refresh token from SecureStore.
 */
export const clearWebStorage = async () => {
    await AsyncStorage.removeItem('refreshToken');
    await AsyncStorage.multiRemove(['userId', 'universityId', 'userRole']);
    await api.post(
        `${apiUrl}auth/logout`,{},{withCredentials: true}
    );
};


/**
 * Clears mobile storage by deleting auth and refresh tokens.
 *
 * This function uses Expo SecureStore to delete the stored authToken and refreshToken.
 */
export const clearMobileStorage = async () => {
    await SecureStore.deleteItemAsync('authToken');
    await SecureStore.deleteItemAsync('refreshToken');
};

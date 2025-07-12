import {Alert, Platform} from "react-native";
import {useCallback} from "react";


export const useAlert = () => {
    return useCallback((title: string, message: string) => {
        if (Platform.OS === 'web') {
            window.alert(`${title}\n${message}`);
        } else {
            Alert.alert(title, message);
        }
    }, []);
};
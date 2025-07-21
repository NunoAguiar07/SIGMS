import {useEffect, useState} from "react";
import {fetchWelcome} from "../services/fetchWelcome";
import {WelcomeData} from "../types/welcome/WelcomeInterfaces";
import {ParsedError} from "../types/errors/ParseErrorTypes";
import * as SecureStore from 'expo-secure-store';
import {router} from "expo-router";
import {fetchUserInfo} from "../services/authorized/FetchUserInfo";
import {getDeviceType} from "../utils/DeviceType";


export const useWelcomeData = () => {
    const [data, setData] = useState<WelcomeData | null>(null);
    const [error, setError] = useState<ParsedError | null>(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const loadData = async () => {
            try {
                const welcomeData = await fetchWelcome();
                if(getDeviceType() == "WEB"){
                    try {
                        await fetchUserInfo();
                        router.push('/home')
                    } catch (err) {
                        setData(welcomeData);
                    }
                } else {
                    const authToken = await SecureStore.getItemAsync("authToken")
                    const refreshToken = await SecureStore.getItemAsync("refreshToken")
                    console.log(authToken, refreshToken)
                    if(authToken != null && refreshToken != null){
                        await fetchUserInfo();
                        router.push('/home')
                    }
                    setData(welcomeData);
                }
            } catch (err) {
                const error = (err as ParsedError)
                console.log('From Welcome: ',error)
                if(error.status != 401){
                    setError(error);
                }
            } finally {
                setLoading(false);
            }
        };
        loadData();
    }, []);

    return { data, errorWelcomeData: error, loadingWelcomeData: loading };
};
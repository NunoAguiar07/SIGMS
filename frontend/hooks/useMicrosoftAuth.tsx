import {useEffect, useState} from 'react';
import { useRouter } from 'expo-router';
import * as WebBrowser from 'expo-web-browser';
import {AuthService} from '../services/auth/microsoft/AuthService';
import {ParsedError} from "../types/errors/ParseErrorTypes";
import {fetchUserInfo} from "../services/authorized/FetchUserInfo";
import {isMobile} from "../utils/DeviceType";

WebBrowser.maybeCompleteAuthSession();

export const useMicrosoftAuth = () => {
    const router = useRouter();
    const [error, setError] = useState<ParsedError | null>(null);
    const [loading, setLoading] = useState(false);
    const [request, response, promptAsync] = AuthService.initiateMicrosoftAuth();

    const handleAuthPress = async () => {
        try {

            if (isMobile) {
                await WebBrowser.warmUpAsync();
            }

            const result = await promptAsync();


            if (isMobile && result.type !== 'cancel') {
                await WebBrowser.coolDownAsync();
            }
        } catch (err) {
            setError(err as ParsedError);
        }
    };

    useEffect(() => {
        const handleAuthResponse = async () => {
            if (response?.type === 'success') {
                try {
                    setLoading(true);
                    const { code } = response.params;
                    const accessToken = await AuthService.exchangeCodeForTokens(
                        code,
                        request?.codeVerifier ?? ''
                    );
                    await AuthService.authenticateWithBackend(accessToken);
                    await fetchUserInfo();
                    router.push('/home');
                } catch (error) {
                    setError(error as ParsedError);
                } finally {
                    setLoading(false);
                }
            } else if (response?.type === 'error') {
                setError({
                    status: 500,
                    message: response.error?.message || 'Authentication failed'
                });
            }
        };
        handleAuthResponse();
    }, [response]);

    return {
        promptAsync: handleAuthPress,
        disabled: !request,
        errorMicrosoftAuth: error,
        loadingMicrosoftAuth: loading
    };
};
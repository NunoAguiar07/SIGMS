import {useEffect, useState} from 'react';
import { useRouter } from 'expo-router';
import * as WebBrowser from 'expo-web-browser';
import {AuthService} from '../services/auth/microsoft/AuthService';
import {ParsedError} from "../types/errors/ParseErrorTypes";
import {fetchUserInfo} from "../services/authorized/fetchUserInfo";

WebBrowser.maybeCompleteAuthSession();

export const useMicrosoftAuth = () => {
    const router = useRouter();
    const [error, setError] = useState<ParsedError | null>(null);
    const [loading, setLoading] = useState(false);
    const [request, response, promptAsync] = AuthService.initiateMicrosoftAuth();

    useEffect(() => {
        const handleAuthResponse = async () => {
            if (response?.type === 'success') {
                try {
                    setLoading(true);
                    const { code } = response.params;
                    const accessToken = await AuthService.exchangeCodeForTokens(code, request?.codeVerifier ?? '');
                    await AuthService.authenticateWithBackend(accessToken);
                    await fetchUserInfo();
                    setLoading(false);
                    router.push('/home');
                } catch (error) {
                    setLoading(false);
                    setError(error as ParsedError);
                }
            }
        };

        handleAuthResponse();
    }, [response]);

    return { promptAsync, disabled: !request, errorMicrosoftAuth :error, loadingMicrosoftAuth:loading };
};
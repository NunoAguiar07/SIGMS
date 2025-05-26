import {useEffect, useState} from "react";
import {WelcomeInterface} from "../../interfaces/WelcomeInterface";
import {ErrorInterface} from "../../interfaces/ErrorInterface";
import {WelcomeScreen} from "../../screens/WelcomeScreen";
import {WelcomeRequest} from "../../requests/WelcomeRequest";
import ErrorHandler from "./error";
import LoadingPresentation from "../../screens/LoadingScreen";
import {useRouter} from "expo-router";
import {initiateMicrosoftAuthRequest} from "../../requests/auth/Microsoft/InitiateMicrosoftAuthRequest";
import {exchangeCodeForTokens} from "../../requests/auth/Microsoft/ExchangeCodeForTokenRequest";
import {authenticateWithBackend} from "../../requests/auth/Microsoft/AuthenticateWithBackendRequest";
import {fetchUserInfo} from "../../requests/authorized/FetchUserInfoRequest";
import * as WebBrowser from 'expo-web-browser';

WebBrowser.maybeCompleteAuthSession();

const Welcome = () => {
    const router = useRouter();
    const [welcome, setHome] = useState<WelcomeInterface | null>(null);
    const [error, setError] = useState<ErrorInterface | null>(null);
    const [request, response, promptAsync] = initiateMicrosoftAuthRequest();

    useEffect(() => {
        const fetchData = WelcomeRequest(setHome, setError);
        fetchData();
    }, []);

    useEffect(() => {
        const handleAuthResponse = async () => {
            if (response?.type === 'success') {
                try {
                    const { code } = response.params;
                    const accessToken = await exchangeCodeForTokens(code, request?.codeVerifier ?? '');
                    await authenticateWithBackend(accessToken);
                    await fetchUserInfo(setError)
                    router.push('/userHome');
                } catch (error) {
                    console.error('Authentication failed:', error);
                }
            }
        };

        handleAuthResponse();
    }, [response]);

    if (error) return <ErrorHandler errorStatus={error.status} errorMessage={error.message} />;
    if (!welcome) return <LoadingPresentation />;

    return (
        <WelcomeScreen
            welcome={welcome}
            request={request}
            promptAsync={promptAsync}
        />
    );
};

export default Welcome;
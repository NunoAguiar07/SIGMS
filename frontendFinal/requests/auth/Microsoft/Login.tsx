import * as AuthSession from 'expo-auth-session';
import * as WebBrowser from 'expo-web-browser';
import {Button} from 'react-native';
import * as SecureStore from 'expo-secure-store';
import {useEffect} from "react";
import AsyncStorage from '@react-native-async-storage/async-storage';
import axios from "axios";
import {useRouter} from "expo-router";




WebBrowser.maybeCompleteAuthSession();

const MicrosoftAuthButton = () => {
    const router = useRouter();

  const discovery = {
    authorizationEndpoint: `https://login.microsoftonline.com/common/oauth2/v2.0/authorize`,
    tokenEndpoint: `https://login.microsoftonline.com/common/oauth2/v2.0/token`,
};

const [request, response, promptAsync] = AuthSession.useAuthRequest(
    {
        clientId: process.env.EXPO_PUBLIC_MICROSOFT_CLIENT_ID,
        scopes: ['openid', 'profile', 'email', 'offline_access', 'https://graph.microsoft.com/User.Read'],
        redirectUri: AuthSession.makeRedirectUri({
            scheme: 'frontendFinal',
            path: 'auth/microsoft/callback'
        }),
        responseType: AuthSession.ResponseType.Code,
        usePKCE: true,
    },
    discovery
);

useEffect(() => {
    if (response?.type === 'success') {
        const { code } = response.params;

        // Exchange code for tokens directly in the frontend
        exchangeCodeForTokens(code);
    }
}, [response]);

const exchangeCodeForTokens = async (code: string) => {
    try {
        console.log(code)
        console.log(request)
        const tokenResponse = await AuthSession.exchangeCodeAsync(
            {
                clientId: process.env.EXPO_PUBLIC_MICROSOFT_CLIENT_ID,
                code: code,
                redirectUri: AuthSession.makeRedirectUri({
                    scheme: 'frontendFinal',
                    path: 'auth/microsoft/callback'
                }),
                extraParams: {
                    code_verifier: request?.codeVerifier ?? '',
                    grant_type: 'authorization_code'
                },

            },
            discovery
        );

        await AsyncStorage.setItem('refreshToken', tokenResponse.refreshToken ?? '');


        // Now send the access token to your backend
        console.log('Access Token:', tokenResponse.accessToken);
        authenticateWithBackend(tokenResponse.accessToken);
    } catch (error) {
        console.error('Token exchange failed:', error);
    }
};



const authenticateWithBackend = async (accessToken: string) => {
        try {
            const response = await axios.post(
                `${process.env.EXPO_PUBLIC_API_URL}/auth/microsoft`,
                {},
                {
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${accessToken}`
                    }
                }
            );

            if (response.data.token) {
                // Store your backend JWT token
                await AsyncStorage.setItem('authToken', response.data.token);
                console.log('Backend authentication successful:', response.data.token);
                router.replace('/profile');
            } else {
                console.error('Backend authentication failed: No token received');
            }
        } catch (error) {
            if (axios.isAxiosError(error)) {
                if (error.response) {
                    console.error('Backend authentication failed:', error.response.data.message);
                } else if (error.request) {
                    console.error('No response from server:', error.request);
                } else {
                    console.error('Request setup error:', error.message);
                }
            } else {
                console.error('Unexpected error:', error);
            }
        }
};

const handleRefreshToken = async () => {
    const refreshToken = await SecureStore.getItemAsync('refreshToken');
    if (!refreshToken) return;

    try {
        const tokenResponse = await AuthSession.refreshAsync(
            {
                clientId: process.env.EXPO_PUBLIC_MICROSOFT_CLIENT_ID,
                refreshToken,
                scopes: ['openid', 'profile', 'email', 'https://graph.microsoft.com/User.Read'],
            },
            discovery
        );

        await AsyncStorage.setItem('refreshToken', tokenResponse.refreshToken ?? '');
        authenticateWithBackend(tokenResponse.accessToken);
    } catch (error) {
        console.error('Token refresh failed:', error);
    }
};

return (
    <Button
        disabled={!request}
        title="Sign in with Microsoft"
        onPress={() => promptAsync()}
    />
);
};

export default MicrosoftAuthButton;
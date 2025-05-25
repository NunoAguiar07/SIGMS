import * as AuthSession from 'expo-auth-session';
import * as WebBrowser from 'expo-web-browser';
import {Image, Text, TouchableOpacity} from 'react-native';
import * as SecureStore from 'expo-secure-store';
import {useEffect} from "react";
import AsyncStorage from '@react-native-async-storage/async-storage';
import axios from "axios";
import {useRouter} from "expo-router";
import {getDeviceType} from "../../../Utils/DeviceType";
import api from "../../interceptors/DeviceInterceptor";
import {styles} from "../../../css_styling/profile/RectangleProps";


WebBrowser.maybeCompleteAuthSession();

const MicrosoftAuthButton = () => {
    const router = useRouter();
    const deviceType = getDeviceType();

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
            await authenticateWithBackend(tokenResponse.accessToken);
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
                    withCredentials: true,
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${accessToken}`,
                        'X-Device': deviceType
                    }
                }
            );
            if (deviceType !== 'WEB' && response.data.token) {
                await SecureStore.setItemAsync('authToken', response.data.token);
            }
            await getUserInfo()
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

    const getUserInfo = async () => {
        try {
            const response = await api.get(`/userInfo`, { withCredentials: true });
            const { userId, universityId, userRole } = response.data;
            console.log(response.data)
            await AsyncStorage.multiSet([
                ['userId', userId],
                ['universityId', universityId],
                ['userRole', userRole]
            ]);
            router.replace('/userHome');
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
    }

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
            await authenticateWithBackend(tokenResponse.accessToken);
        } catch (error) {
            console.error('Token refresh failed:', error);
        }
    };

    return (
        <TouchableOpacity
            style={styles.microsoftButton}
            onPress={() => promptAsync()}
            disabled={!request}
        >
            <Image source={require('../../../assets/microsoft-logo.jpg')} style={{ width: 24, height: 24 }} resizeMode="contain"/>
            <Text style={styles.microsoftButtonText}>Microsoft</Text>
        </TouchableOpacity>
    );
};

export default MicrosoftAuthButton;
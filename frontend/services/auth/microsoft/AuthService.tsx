import * as AuthSession from 'expo-auth-session';
import {AuthConfig, BackendAuthResponse, TokenResponse} from "../../../types/auth/microsoft/authMicrosoftTypes";
import axios from "axios";
import {apiUrl} from "../../fetchWelcome";
import {getDeviceType} from "../../../utils/DeviceType";
import AsyncStorage from "@react-native-async-storage/async-storage";
import * as SecureStore from 'expo-secure-store';
import {handleAxiosError} from "../../../utils/HandleAxiosError";
import {pushToken} from "../../notifications/PushToken";

const discovery = {
    authorizationEndpoint: 'https://login.microsoftonline.com/common/oauth2/v2.0/authorize',
    tokenEndpoint: 'https://login.microsoftonline.com/common/oauth2/v2.0/token',
};

const authConfig: AuthConfig = {
    clientId: process.env.EXPO_PUBLIC_MICROSOFT_CLIENT_ID || '',
    scopes: ['openid', 'profile', 'email', 'offline_access', 'https://graph.microsoft.com/User.Read'],
    redirectUri: AuthSession.makeRedirectUri({
        scheme: 'frontendfinal',
        path: 'welcome'
    }),
    responseType: AuthSession.ResponseType.Code,
    usePKCE: true,
};

export const AuthService = {
    async authenticateWithBackend(accessToken: string): Promise<TokenResponse> {
        try {

            const response = await axios.post<TokenResponse>(
                `${apiUrl}auth/microsoft`,
                {},
                {
                    withCredentials: true,
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${accessToken}`,
                        'X-Device': getDeviceType()
                    }
                }
            );
            if (getDeviceType() !== 'WEB' && response.data.accessToken) {
                await SecureStore.setItemAsync('authToken', response.data.accessToken);
                await pushToken()
            }

            return response.data;
        } catch (error) {
            throw handleAxiosError(error);
        }
    },

    async exchangeCodeForTokens(code: string, codeVerifier: string): Promise<string> {
        try {
            const tokenResponse = await AuthSession.exchangeCodeAsync(
                {
                    clientId: authConfig.clientId,
                    code,
                    redirectUri: authConfig.redirectUri,
                    extraParams: {
                        code_verifier: codeVerifier,
                        grant_type: 'authorization_code'
                    },
                },
                discovery
            );

            if (tokenResponse.refreshToken) {
                await AsyncStorage.setItem('refreshToken', tokenResponse.refreshToken);
            }

            return tokenResponse.accessToken;
        } catch (error) {
            console.error('Token exchange failed:', error);
            throw error;
        }
    },

    initiateMicrosoftAuth(): ReturnType<typeof AuthSession.useAuthRequest> {
        return AuthSession.useAuthRequest(
            {
                clientId: authConfig.clientId,
                scopes: authConfig.scopes,
                redirectUri: authConfig.redirectUri,
                responseType: authConfig.responseType,
                usePKCE: authConfig.usePKCE,
            },
            discovery
        );
    }
};
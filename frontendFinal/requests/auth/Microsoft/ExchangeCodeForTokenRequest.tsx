import * as AuthSession from 'expo-auth-session';
import {discovery} from "./Discovery";
import AsyncStorage from '@react-native-async-storage/async-storage';


export const exchangeCodeForTokens = async (code: string, codeVerifier: string) => {
    try {
        const tokenResponse = await AuthSession.exchangeCodeAsync(
            {
                clientId: process.env.EXPO_PUBLIC_MICROSOFT_CLIENT_ID!,
                code,
                redirectUri: AuthSession.makeRedirectUri({
                    scheme: 'frontendFinal',
                    path: 'auth/microsoft/callback'
                }),
                extraParams: {
                    code_verifier: codeVerifier,
                    grant_type: 'authorization_code'
                },
            },
            discovery
        );

        await AsyncStorage.setItem('refreshToken', tokenResponse.refreshToken ?? '');
        return tokenResponse.accessToken;
    } catch (error) {
        console.error('Token exchange failed:', error);
        throw error;
    }
};
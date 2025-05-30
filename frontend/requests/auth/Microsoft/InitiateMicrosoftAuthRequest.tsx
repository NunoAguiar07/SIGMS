import * as AuthSession from 'expo-auth-session';
import {discovery} from './Discovery'; // Ensure this path is correct


export const initiateMicrosoftAuthRequest = () => {
    return AuthSession.useAuthRequest(
        {
            clientId: process.env.EXPO_PUBLIC_MICROSOFT_CLIENT_ID!,
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
};
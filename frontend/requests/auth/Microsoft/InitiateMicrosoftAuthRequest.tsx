import * as AuthSession from 'expo-auth-session';
import {discovery} from './Discovery'; // Ensure this path is correct


export const initiateMicrosoftAuthRequest = () => {
    const clientId = process.env.EXPO_PUBLIC_MICROSOFT_CLIENT_ID
    return AuthSession.useAuthRequest(
        {
            clientId: clientId,
            scopes: ['openid', 'profile', 'email', 'offline_access', 'https://graph.microsoft.com/User.Read'],
            redirectUri: AuthSession.makeRedirectUri({
                scheme: 'frontendFinal',
                path: 'welcome'
            }),
            responseType: AuthSession.ResponseType.Code,
            usePKCE: true,
        },
        discovery
    );
};
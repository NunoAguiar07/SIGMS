import * as AuthSession from 'expo-auth-session';

export interface AuthConfig {
    clientId: string;
    scopes: string[];
    redirectUri: string;
    responseType: AuthSession.ResponseType;
    usePKCE: boolean;
}

export interface TokenResponse {
    accessToken: string;
    refreshToken?: string;
    expiresIn?: number;
    tokenType?: string;
}

export interface BackendAuthResponse {
    token?: string;
    user?: any; // Replace with your user type
}
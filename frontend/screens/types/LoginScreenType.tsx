

export interface LoginScreenType {
    email: string;
    password: string;
    onEmailChange: (text: string) => void;
    onPasswordChange: (text: string) => void;
    onLogin: () => void;
    onNavigateToRegister: () => void;
}
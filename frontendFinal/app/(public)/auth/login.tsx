import React, {useState} from 'react';
import {LoginScreen} from '../../../screens/LoginScreen'; // presentation
import {LoginRequest} from '../../../requests/auth/LoginRequest';
import {ErrorInterface} from '../../../interfaces/ErrorInterface';
import {getDeviceType} from '../../../Utils/DeviceType';
import {useRouter} from 'expo-router';
import ErrorHandler from "../error";

const Login = () => {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState<ErrorInterface | null>(null);
    const deviceType = getDeviceType();
    const router = useRouter();

    const handleNavigateToRegister = () => {
        router.push('/auth/register');
    };

    const handleLogin = async () => {
        const login = LoginRequest(email, password, deviceType, setError);
        const success = await login();
        router.replace('/userHome');
    };

    if (error) return <ErrorHandler errorStatus={error.status} errorMessage={error.message} />;

    return (
        <LoginScreen
            email={email}
            password={password}
            onEmailChange={setEmail}
            onPasswordChange={setPassword}
            onLogin={handleLogin}
            onNavigateToRegister={handleNavigateToRegister}
        />
    );
};

export default Login;
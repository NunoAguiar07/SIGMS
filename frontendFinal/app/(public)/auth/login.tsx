import React, {useState} from 'react';
import {LoginScreen} from '../../../screens/LoginScreen';
import {LoginRequest} from '../../../requests/auth/LoginRequest';
import {ErrorInterface} from '../../../interfaces/ErrorInterface';
import {getDeviceType} from '../../../Utils/DeviceType';
import {useRouter} from 'expo-router';
import ErrorHandler from "../error";
import {fetchUserInfo} from "../../../requests/authorized/FetchUserInfoRequest";

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
        console.log("ffh")
        const login = LoginRequest(email, password, deviceType, setError);
        const success = await login();
        console.log("ffh")
        if(success) {
            console.log("ffh")
            await fetchUserInfo(setError)
            return router.push('/userHome');
        }
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
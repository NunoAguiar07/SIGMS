import {LoginScreen} from '../../../screens/auth/LoginScreen';
import ErrorHandler from "../error";
import LoadingPresentation from "../../../screens/auxScreens/LoadingScreen";
import {useLogin} from "../../../hooks/useAuth";

const Login = () => {
    const {
        email,
        setEmail,
        password,
        setPassword,
        error,
        loading,
        handleLogin,
        handleNavigateToRegister,
    } = useLogin();

    if (loading) return <LoadingPresentation />;
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
import {LoginScreen} from '../../../screens/mainScreens/LoginScreen';
import ErrorHandler from "../error";
import LoadingPresentation from "../../../screens/auxScreens/LoadingScreen";
import {useLogin} from "../../../hooks/useAuth";
import {BackgroundImage} from "../../../screens/components/BackgroundImage";

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
        <BackgroundImage>
            <LoginScreen
                email={email}
                password={password}
                onEmailChange={setEmail}
                onPasswordChange={setPassword}
                onLogin={handleLogin}
                onNavigateToRegister={handleNavigateToRegister}
            />
        </BackgroundImage>
    );
};

export default Login;
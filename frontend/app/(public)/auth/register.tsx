import {RegisterScreen} from '../../../screens/mainScreens/RegisterScreen';
import ErrorHandler from "../error";
import {useRegister} from "../../../hooks/useAuth";
import LoadingPresentation from "../../../screens/auxScreens/LoadingScreen";
import {BackgroundImage} from "../../../screens/components/BackgroundImage";

const Register = () => {
    const {
        email,
        setEmail,
        username,
        setUsername,
        password,
        setPassword,
        role,
        handleRoleChange,
        universities,
        searchQuery,
        setSearchQuery,
        error,
        loading,
        handleUniversitySelect,
        handleRegister,
        handleNavigateToLogin,
    } = useRegister();

    if (loading) return <LoadingPresentation />;
    if (error) return <ErrorHandler errorStatus={error.status} errorMessage={error.message} />;

    return (
        <BackgroundImage>
            <RegisterScreen
                email={email}
                username={username}
                password={password}
                role={role}
                universities={universities}
                searchQuery={searchQuery}
                onEmailChange={setEmail}
                onUsernameChange={setUsername}
                onPasswordChange={setPassword}
                onRoleChange={handleRoleChange}
                onSearchChange={setSearchQuery}
                onUniversitySelect={handleUniversitySelect}
                onRegister={handleRegister}
                onNavigateToLogin={handleNavigateToLogin}
            />
        </BackgroundImage>
    );
};

export default Register;
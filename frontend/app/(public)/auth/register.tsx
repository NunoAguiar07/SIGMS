import {RegisterScreen} from '../../../screens/auth/RegisterScreen';
import ErrorHandler from "../error";
import {useRegister} from "../../../hooks/useAuth";
import LoadingPresentation from "../../../screens/auxScreens/LoadingScreen";

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
        universityId,
        universities,
        selectedUniversity,
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
        <RegisterScreen
            email={email}
            username={username}
            password={password}
            role={role}
            universityId={universityId}
            universities={universities}
            selectedUniversity={selectedUniversity}
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
    );
};

export default Register;
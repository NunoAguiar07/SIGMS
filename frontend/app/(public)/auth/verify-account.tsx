import ErrorHandler from "../error";
import {VerifyAccountScreen} from "../../../screens/mainScreens/VerifyAccountScreen";
import LoadingPresentation from "../../../screens/auxScreens/LoadingScreen";
import {useVerifyAccount} from "../../../hooks/useAuth";

const VerifyAccount = () => {
    const {
        verificationSuccess,
        loading,
        error,
        handleNavigateToLogin
    } = useVerifyAccount();

    if (loading) return <LoadingPresentation />;
    if (error) return <ErrorHandler errorStatus={error.status} errorMessage={error.message} />;

    return (
        <VerifyAccountScreen
            onNavigateToLogin={handleNavigateToLogin}
            verificationSuccess={verificationSuccess}
        />
    );
};

export default VerifyAccount;
import ErrorHandler from "../error";
import {VerifyAccountScreen} from "../../../screens/mainScreens/VerifyAccountScreen";
import LoadingPresentation from "../../../screens/auxScreens/LoadingScreen";
import {useVerifyAccount} from "../../../hooks/useAuth";
import {BackgroundImage} from "../../../screens/components/BackgroundImage";

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
        <BackgroundImage>
            <VerifyAccountScreen
                onNavigateToLogin={handleNavigateToLogin}
                verificationSuccess={verificationSuccess}
            />
        </BackgroundImage>
    );
};

export default VerifyAccount;
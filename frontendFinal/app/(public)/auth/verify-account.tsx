import {useEffect, useState} from "react";
import {ErrorInterface} from "../../../interfaces/ErrorInterface";
import {useLocalSearchParams, useRouter} from "expo-router";
import {VerifyAccountRequest} from "../../../requests/auth/VerifyAccountRequest";
import ErrorHandler from "../error";
import {VerifyAccountScreen} from "../../../screens/VerifyAccountScreen";

export const VerifyAccount = () => {
    const [verificationSuccess, setVerificationSuccess] = useState(false);
    const [error, setError] = useState<ErrorInterface | null>(null);
    const router = useRouter();
    const params = useLocalSearchParams();
    const { token } = params;

    useEffect(() => {
        const verifyAccount = async () => {
            if (typeof token === 'string') {
                const verify = VerifyAccountRequest(token, setError);
                const success = await verify();
                setVerificationSuccess(success);
            }
        };
        verifyAccount();
    }, [token]);

    const handleNavigateToLogin = () => {
        router.replace('/auth/login');
    };

    if (error) return <ErrorHandler errorStatus={error.status} errorMessage={error.message} />;

    return (
        <VerifyAccountScreen
            onNavigateToLogin={handleNavigateToLogin}
            verificationSuccess={verificationSuccess}
        />
    );
};

export default VerifyAccount;
import {Text, TouchableOpacity, View} from "react-native";
import {commonStyles} from "../css_styling/common/CommonProps";
import {VerifyAccountScreenType} from "../types/VerifyAccountScreenType";

export const VerifyAccountScreen = ({
    onNavigateToLogin,
    verificationSuccess
}: VerifyAccountScreenType) => {
    return (
        <View style={commonStyles.container}>
            <View style={commonStyles.card}>
                {verificationSuccess ? (
                    <SuccessState onNavigateToLogin={onNavigateToLogin} />
                ) : (
                    <FailureState />
                )}
            </View>
        </View>
    );
};


interface SuccessStateType {
    onNavigateToLogin: () => void;
}

export const SuccessState = ({ onNavigateToLogin }: SuccessStateType) => (
    <>
        <Text style={commonStyles.title}>Account Verified</Text>
        <Text style={commonStyles.message}>
            Your account has been verified successfully!
        </Text>
        <Text style={commonStyles.message}>
            You can now close this window and log in to your account.
        </Text>
        <TouchableOpacity
            style={commonStyles.loginButton}
            onPress={onNavigateToLogin}
        >
            <Text style={commonStyles.buttonText}>Go to Login</Text>
        </TouchableOpacity>
    </>
);

export const FailureState = () => (
    <>
        <Text style={commonStyles.title}>Verification Failed</Text>
        <Text style={commonStyles.message}>
            The verification link is invalid or has expired.
        </Text>
        <Text style={commonStyles.message}>
            Please request a new verification email.
        </Text>
    </>
);
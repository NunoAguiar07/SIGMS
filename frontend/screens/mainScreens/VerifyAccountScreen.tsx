import {Text, TouchableOpacity} from "react-native";
import {commonStyles} from "../css_styling/common/CommonProps";
import {VerifyAccountScreenType} from "../types/VerifyAccountScreenType";
import {Card, CenteredContainer} from "../css_styling/common/NewContainers";
import {Subtitle, Title} from "../css_styling/common/Typography";
import {Button, ButtonText} from "../css_styling/common/Buttons";

export const VerifyAccountScreen = ({
    onNavigateToLogin,
    verificationSuccess
}: VerifyAccountScreenType) => {
    return (
        <CenteredContainer flex={1} justifyContent="center" padding="md">
                {verificationSuccess ? (
                    <Card shadow="medium" alignItems={"center"} gap="md">
                        <Title>Account Verified</Title>
                        <Subtitle>You can now close this window and log in to your account.</Subtitle>
                        <Button
                            onPress={onNavigateToLogin}
                            variant="primary"
                        >
                            <ButtonText>Login</ButtonText>
                        </Button>
                    </Card>
                ) : (
                    <Card shadow="medium" alignItems={"center"} gap="md">
                        <Title>Verification Failed</Title>
                        <Subtitle>The verification link is invalid or has expired.</Subtitle>
                        <Subtitle>Please request a new verification email.</Subtitle>
                    </Card>
                )}
        </CenteredContainer>
        // <View style={commonStyles.container}>
        //     <View style={commonStyles.card}>
        //         {verificationSuccess ? (
        //             <SuccessState onNavigateToLogin={onNavigateToLogin} />
        //         ) : (
        //             <FailureState />
        //         )}
        //     </View>
        // </View>
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
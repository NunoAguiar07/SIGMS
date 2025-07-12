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
    );
};

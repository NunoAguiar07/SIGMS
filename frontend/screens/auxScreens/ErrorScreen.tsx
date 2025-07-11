import {ErrorScreenType} from "../types/ErrorScreenType";
import {Card, CenteredContainer} from "../css_styling/common/NewContainers";
import {Subtitle, Title} from "../css_styling/common/Typography";
import {Button, ButtonText} from "../css_styling/common/Buttons";
import {useRouter} from "expo-router";


/**
 * Const "ErrorScreen" representing the page when an error happens in the application.
 * @param errorStatus represents the error code that happens.
 * @param errorMessage represents the error message we want to represent to the user.
 * @param goBack represent the button/function to do when we want to go back before the error.
 * @return the error page.
 */
export const ErrorScreen = ({ errorStatus, errorMessage, goBack } : ErrorScreenType) => {
    const router = useRouter();

    const handleAction = () => {
        switch (errorStatus) {
            case 401:
                router.replace('/welcome');
                break;
            case 404:
                goBack ? goBack() : router.back();
                break;
            case 500:
                router.reload();
                break;
            default:
                goBack ? goBack() : router.back();
                break;
        }
    };

    const getActionButton = () => {
        switch (errorStatus) {
            case 401:
                return (
                    <Button variant="primary" onPress={handleAction}>
                        <ButtonText>Go to Welcome</ButtonText>
                    </Button>
                );
            case 404:
                return (
                    <Button variant="primary" onPress={handleAction}>
                        <ButtonText>Go Back</ButtonText>
                    </Button>
                );
            case 500:
                return (
                    <Button variant="primary" onPress={handleAction}>
                        <ButtonText>Refresh</ButtonText>
                    </Button>
                );
            default:
                // For other errors, we can show a dismiss button
                return (
                    <Button variant="primary" onPress={handleAction}>
                        <ButtonText>Dismiss</ButtonText>
                    </Button>
                );
        }
    };
    if (errorStatus === 400 || errorStatus === 422) {
        alert(errorMessage);
        return null;
    }

    return (
        <CenteredContainer flex={1} padding={"md"}>
            <Card shadow="medium" alignItems={"center"} gap="md">
                <Title>Error {errorStatus}</Title>
                <Subtitle>{errorMessage}</Subtitle>
                {getActionButton()}
            </Card>
        </CenteredContainer>
    );
};
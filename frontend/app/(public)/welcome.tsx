import {WelcomeScreen} from "../../screens/mainScreens/WelcomeScreen";
import ErrorHandler from "./error";
import LoadingPresentation from "../../screens/auxScreens/LoadingScreen";
import * as WebBrowser from 'expo-web-browser';
import {useMicrosoftAuth} from "../../hooks/useMicrosoftAuth";
import {useWelcomeData} from "../../hooks/useWelcomeData";

WebBrowser.maybeCompleteAuthSession();

const Welcome = () => {
    const { data, errorWelcomeData, loadingWelcomeData } = useWelcomeData();
    const { promptAsync, disabled , errorMicrosoftAuth, loadingMicrosoftAuth } = useMicrosoftAuth();

    if (errorWelcomeData) return <ErrorHandler errorStatus={errorWelcomeData.status} errorMessage={errorWelcomeData.message} />;
    if (errorMicrosoftAuth) return <ErrorHandler errorStatus={errorMicrosoftAuth.status} errorMessage={errorMicrosoftAuth.message} />;
    if (loadingWelcomeData || !data || loadingMicrosoftAuth) return <LoadingPresentation />;

    return (
        <WelcomeScreen
            welcome={data}
            onMicrosoftPress={promptAsync}
            microsoftDisabled={disabled}
        />
    );
};

export default Welcome;
import {ActivityIndicator} from "react-native";
import {CenteredContainer} from "../css_styling/common/NewContainers";

/**
 * Const "LoadingPresentation" representing the loading screen when we are loading something.
 * @return the new page for the loading screen.
 */
const LoadingPresentation = () => {
    return (
        <CenteredContainer flex={1}>
            <ActivityIndicator size="large" />
        </CenteredContainer>
    );
};
export default LoadingPresentation;

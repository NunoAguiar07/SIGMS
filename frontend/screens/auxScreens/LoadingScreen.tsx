import {ActivityIndicator, View} from "react-native";
import {commonStyles} from "../../css_styling/common/CommonProps";

/**
 * Const "Loadingpresentation" representing the loading screen when we are loading something.
 * @return the new page for the loading screen.
 */
const LoadingPresentation = () => {
    return (
        <View style={commonStyles.container}>
            <ActivityIndicator size="large" />
        </View>
    );
};
export default LoadingPresentation;

import * as WebBrowser from 'expo-web-browser';
import {Image, Text, TouchableOpacity, View} from 'react-native';
import {welcomeStyles} from "../css_styling/welcome/WelcomeProps";

WebBrowser.maybeCompleteAuthSession();

// @ts-ignore
const MicrosoftAuthButton = ({ request, promptAsync }) => {
    return (
        <View style={welcomeStyles.microsoftButtonContainer}>
                <TouchableOpacity
                    style={welcomeStyles.microsoftButton}
                    onPress={() => promptAsync()}
                    disabled={!request}
                >
                    <Image
                        source={require('../assets/microsoft-logo.jpg')}
                        style={{ width: 24, height: 24 }}
                        resizeMode="contain"
                    />
                    <Text style={welcomeStyles.microsoftButtonText}>Microsoft</Text>
                </TouchableOpacity>
        </View>
    );
};

export default MicrosoftAuthButton;
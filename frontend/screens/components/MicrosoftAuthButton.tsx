import * as WebBrowser from 'expo-web-browser';
import {Image, Text, TouchableOpacity, View} from 'react-native';
import {welcomeStyles} from "../css_styling/welcome/WelcomeProps";
import {MicrosoftAuthButtonType} from "../types/MicrosoftAuthButtonType";

WebBrowser.maybeCompleteAuthSession();

const MicrosoftAuthButton = ({ onPress, disabled } : MicrosoftAuthButtonType) => {
    return (
        <View style={welcomeStyles.microsoftButtonContainer}>
            <TouchableOpacity
                style={welcomeStyles.microsoftButton}
                onPress={onPress}
                disabled={disabled}
            >
                <Image
                    source={require('../../assets/microsoft_logo.jpg')}
                    style={{ width: 24, height: 24 }}
                    resizeMode="contain"
                />
                <Text style={welcomeStyles.microsoftButtonText}>Microsoft</Text>
            </TouchableOpacity>
        </View>
    );
};

export default MicrosoftAuthButton;
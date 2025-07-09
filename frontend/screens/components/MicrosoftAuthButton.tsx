import * as WebBrowser from 'expo-web-browser';
import {MicrosoftAuthButtonType} from "../types/MicrosoftAuthButtonType";
import {MicrosoftAuthButton, MicrosoftButtonText, MicrosoftLogo} from "../css_styling/common/Buttons";

WebBrowser.maybeCompleteAuthSession();

const MicrosoftAuthButtonComponent = ({ onPress, disabled } : MicrosoftAuthButtonType) => {
    return (
        <MicrosoftAuthButton onPress={onPress} disabled={disabled}>
            <MicrosoftLogo />
            <MicrosoftButtonText>Microsoft</MicrosoftButtonText>
        </MicrosoftAuthButton>
    );
};

export default MicrosoftAuthButton;
import {useState} from "react";
import {Image} from "expo-image";
import MicrosoftAuthButton from "../components/MicrosoftAuthButton";
import {commonStyles} from "../css_styling/common/CommonProps";
import {welcomeStyles} from "../css_styling/welcome/WelcomeProps";
import {Text, TouchableOpacity, View} from "react-native";
import {useRouter} from "expo-router";
import {About} from "../components/footer/About";
import {FAQ} from "../components/footer/FAQ";
import {Privacy} from "../components/footer/Privacy";
import {WelcomeScreenType} from "../types/WelcomeScreenType";

export const WelcomeScreen = ({ welcome, onMicrosoftPress, microsoftDisabled } : WelcomeScreenType) => {
    const [showAbout, setShowAbout] = useState(false);
    const [showFAQ, setShowFAQ] = useState(false);
    const [showPrivacy, setShowPrivacy] = useState(false);
    const router = useRouter();

    return (
        <View style={commonStyles.container}>
            <ModalsContainer
                showAbout={showAbout}
                showFAQ={showFAQ}
                showPrivacy={showPrivacy}
                onCloseAbout={() => setShowAbout(false)}
                onCloseFAQ={() => setShowFAQ(false)}
                onClosePrivacy={() => setShowPrivacy(false)}
            />

            <View style={commonStyles.centerContainer}>
                <Image source={require('../../assets/Logo.webp')} style={commonStyles.logo} />
                <Text style={welcomeStyles.slogan}>{welcome.title}</Text>
                <Text style={welcomeStyles.slogan}>A tua sala de aula, mais simples.</Text>

                <AuthButtons
                    onLoginPress={() => router.push('/auth/login')}
                    onRegisterPress={() => router.push('/auth/register')}
                    onMicrosoftPress={onMicrosoftPress}
                    microsoftDisabled={microsoftDisabled}
                />
            </View>

            <FooterLinks
                onAboutPress={() => setShowAbout(true)}
                onFAQPress={() => setShowFAQ(true)}
                onPrivacyPress={() => setShowPrivacy(true)}
            />
        </View>
    );
};

interface FooterLinksType {
    onAboutPress: () => void;
    onFAQPress: () => void;
    onPrivacyPress: () => void;
}

const FooterLinks = ({
    onAboutPress,
    onFAQPress,
    onPrivacyPress,
}: FooterLinksType) => (
    <View style={commonStyles.footerContainer}>
        <TouchableOpacity onPress={onAboutPress}>
            <Text style={welcomeStyles.footerText}>About</Text>
        </TouchableOpacity>
        <TouchableOpacity onPress={onFAQPress}>
            <Text style={welcomeStyles.footerText}>FAQ</Text>
        </TouchableOpacity>
        <TouchableOpacity onPress={onPrivacyPress}>
            <Text style={welcomeStyles.footerText}>Privacy</Text>
        </TouchableOpacity>
    </View>
);

interface AuthButtonsType {
    onLoginPress: () => void;
    onRegisterPress: () => void;
    onMicrosoftPress: () => void;
    microsoftDisabled: boolean;
}

const AuthButtons = ({
    onLoginPress,
    onRegisterPress,
    onMicrosoftPress,
    microsoftDisabled
}: AuthButtonsType) => (
    <View style={{ marginTop: 10 }}>
        <TouchableOpacity
            style={commonStyles.loginRegisterButton}
            onPress={onLoginPress}
        >
            <Text style={commonStyles.loginRegisterButtonText}>Login</Text>
        </TouchableOpacity>

        <TouchableOpacity
            style={commonStyles.loginRegisterButton}
            onPress={onRegisterPress}
        >
            <Text style={commonStyles.loginRegisterButtonText}>Register</Text>
        </TouchableOpacity>

        <MicrosoftAuthButton
            onPress={onMicrosoftPress}
            disabled={microsoftDisabled}
        />
    </View>
);

interface ModalsContainerType {
    showAbout: boolean;
    showFAQ: boolean;
    showPrivacy: boolean;
    onCloseAbout: () => void;
    onCloseFAQ: () => void;
    onClosePrivacy: () => void;
}

const ModalsContainer = ({
    showAbout,
    showFAQ,
    showPrivacy,
    onCloseAbout,
    onCloseFAQ,
    onClosePrivacy
}: ModalsContainerType) => (
    <>
        {showAbout && <About onClose={onCloseAbout} />}
        {showFAQ && <FAQ onClose={onCloseFAQ} />}
        {showPrivacy && <Privacy onClose={onClosePrivacy} />}
    </>
);
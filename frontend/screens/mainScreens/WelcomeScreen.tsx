import {useState} from "react";
import {useRouter} from "expo-router";
import {About} from "../components/footer/About";
import {FAQ} from "../components/footer/FAQ";
import {Privacy} from "../components/footer/Privacy";
import {WelcomeScreenType} from "../types/WelcomeScreenType";
import {CenteredContainer} from "../css_styling/common/NewContainers";
import {FooterContainer, FooterLink, FooterLinkText, ScreenContainer} from "../css_styling/common/Layout";
import {
    Button,
    ButtonsContainer,
    ButtonText,
    MicrosoftAuthButton,
    MicrosoftButtonText,
    MicrosoftLogo
} from "../css_styling/common/Buttons";
import {Title} from "../css_styling/common/Typography";
import {Logo} from "../css_styling/common/Images";

export const WelcomeScreen = ({ welcome, onMicrosoftPress, microsoftDisabled } : WelcomeScreenType) => {
    const [showAbout, setShowAbout] = useState(false);
    const [showFAQ, setShowFAQ] = useState(false);
    const [showPrivacy, setShowPrivacy] = useState(false);
    const router = useRouter();

    return (
        <ScreenContainer>
            <ModalsContainer
                showAbout={showAbout}
                showFAQ={showFAQ}
                showPrivacy={showPrivacy}
                onCloseAbout={() => setShowAbout(false)}
                onCloseFAQ={() => setShowFAQ(false)}
                onClosePrivacy={() => setShowPrivacy(false)}
            />
            <CenteredContainer flex={1}>
                <Logo />
                <Title>{welcome.title}</Title>
                <Title>A tua sala de aula, mais simples.</Title>
                <AuthButtons
                    onLoginPress={() => router.push('/auth/login')}
                    onRegisterPress={() => router.push('/auth/register')}
                    onMicrosoftPress={onMicrosoftPress}
                    microsoftDisabled={microsoftDisabled}
                />
            </CenteredContainer>

            <FooterLinks
                onAboutPress={() => setShowAbout(true)}
                onFAQPress={() => setShowFAQ(true)}
                onPrivacyPress={() => setShowPrivacy(true)}
            />
        </ScreenContainer>
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
    <FooterContainer>
        <FooterLink onPress={onAboutPress}>
            <FooterLinkText>About</FooterLinkText>
        </FooterLink>
        <FooterLink onPress={onFAQPress}>
            <FooterLinkText>FAQ</FooterLinkText>
        </FooterLink>
        <FooterLink onPress={onPrivacyPress}>
            <FooterLinkText>Privacy</FooterLinkText>
        </FooterLink>
    </FooterContainer>
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
    <ButtonsContainer gap="md">
        <Button variant="primary" onPress={onLoginPress}>
            <ButtonText>Login</ButtonText>
        </Button>
        <Button variant="primary" onPress={onRegisterPress}>
            <ButtonText>Register</ButtonText>
        </Button>
        <MicrosoftAuthButton onPress={onMicrosoftPress} disabled={microsoftDisabled}>
            <MicrosoftLogo />
            <MicrosoftButtonText>Microsoft</MicrosoftButtonText>
        </MicrosoftAuthButton>
    </ButtonsContainer>
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
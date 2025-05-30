import React, {useState} from "react";
import {Image} from "expo-image";
import MicrosoftAuthButton from "./MicrosoftAuthButton";
import About from "./secondary/about";
import Privacy from "./secondary/privacy";
import FAQ from "./secondary/faq";
import {commonStyles} from "../css_styling/common/CommonProps";
import {welcomeStyles} from "../css_styling/welcome/WelcomeProps";
import {Text, TouchableOpacity, View} from "react-native";
import {useRouter} from "expo-router";

// @ts-ignore
export const WelcomeScreen = ({ welcome, request, promptAsync }) => {
    const [showAbout, setShowAbout] = useState(false);
    const [showFAQ, setShowFAQ] = useState(false);
    const [showPrivacy, setShowPrivacy] = useState(false);
    const router = useRouter();

    return (
        <View style={commonStyles.container}>
            {showAbout && <About onClose={() => setShowAbout(false)} />}
            {showFAQ && <FAQ onClose={() => setShowFAQ(false)} />}
            {showPrivacy && <Privacy onClose={() => setShowPrivacy(false)} />}

            <View style={commonStyles.centerContainer}>
                <Image source={require('../assets/Logo.webp')} style={commonStyles.logo} />
                <Text style={welcomeStyles.slogan}>{welcome.title}</Text>
                <Text style={welcomeStyles.slogan}>A tua sala de aula, mais simples.</Text>
                <View style={{ marginTop: 10 }}>
                    <TouchableOpacity
                        style={commonStyles.loginRegisterButton}
                        onPress={() => router.push('/auth/login')}
                    >
                        <Text style={commonStyles.loginRegisterButtonText}>Login</Text>
                    </TouchableOpacity>

                    <TouchableOpacity
                        style={commonStyles.loginRegisterButton}
                        onPress={() => router.push('/auth/register')}
                    >
                        <Text style={commonStyles.loginRegisterButtonText}>Register</Text>
                    </TouchableOpacity>
                </View>
                <MicrosoftAuthButton
                    request={request}
                    promptAsync={promptAsync}
                />
            </View>

            <View style={commonStyles.footerContainer}>
                <TouchableOpacity onPress={() => setShowAbout(true)}>
                    <Text style={welcomeStyles.footerText}>About</Text>
                </TouchableOpacity>
                <TouchableOpacity onPress={() => setShowFAQ(true)}>
                    <Text style={welcomeStyles.footerText}>FAQ</Text>
                </TouchableOpacity>
                <TouchableOpacity onPress={() => setShowPrivacy(true)}>
                    <Text style={welcomeStyles.footerText}>Privacy</Text>
                </TouchableOpacity>
            </View>
        </View>
    );
};
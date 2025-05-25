import React, {useState} from "react";
import '../css_styling/PropertiesWelcome.css'
import {Image} from "expo-image";
import MicrosoftAuthButton from "../requests/auth/Microsoft/Login";
import About from "./secondary/about";
import Privacy from "./secondary/privacy";
import FAQ from "./secondary/faq";
import {styles} from "../css_styling/profile/RectangleProps";
import {Text, TouchableOpacity, View} from "react-native";
import {useRouter} from "expo-router";
// @ts-ignore
export const WelcomeScreen = ({ welcome }) => {
    const [showAbout, setShowAbout] = useState(false);
    const [showFAQ, setShowFAQ] = useState(false);
    const [showPrivacy, setShowPrivacy] = useState(false);
    const router = useRouter();

    return (
        <View style={styles.container}>
            {showAbout && <About onClose={() => setShowAbout(false)} />}
            {showFAQ && <FAQ onClose={() => setShowFAQ(false)} />}
            {showPrivacy && <Privacy onClose={() => setShowPrivacy(false)} />}

            <View style={styles.centerContainer}>
                <Image source={require('../assets/Logo.webp')} style={styles.logo} />
                <Text style={styles.slogan}>{welcome.title}</Text>
                <Text style={styles.slogan}>A tua sala de aula, mais simples.</Text>
                <View style={{ marginTop: 10 }}>
                    <TouchableOpacity
                        style={styles.loginRegisterButton}
                        onPress={() => router.push('/auth/login')}
                    >
                        <Text style={styles.loginRegisterButtonText}>Login</Text>
                    </TouchableOpacity>

                    <TouchableOpacity
                        style={styles.loginRegisterButton}
                        onPress={() => router.push('/auth/register')}
                    >
                        <Text style={styles.loginRegisterButtonText}>Register</Text>
                    </TouchableOpacity>
                </View>
                <View style={styles.microsoftButtonContainer}>
                    <MicrosoftAuthButton />
                </View>
            </View>

            <View style={styles.footerContainer}>
                <TouchableOpacity onPress={() => setShowAbout(true)}>
                    <Text style={styles.footerText}>About</Text>
                </TouchableOpacity>
                <TouchableOpacity onPress={() => setShowFAQ(true)}>
                    <Text style={styles.footerText}>FAQ</Text>
                </TouchableOpacity>
                <TouchableOpacity onPress={() => setShowPrivacy(true)}>
                    <Text style={styles.footerText}>Privacy</Text>
                </TouchableOpacity>
            </View>
        </View>
    );
};
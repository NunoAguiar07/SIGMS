import {Text, TouchableOpacity, View} from "react-native";
import {styles} from "../css_styling/profile/RectangleProps";


// @ts-ignore
export const VerifyAccountScreen = ({onNavigateToLogin, verificationSuccess}) => {
    return (
        <View style={styles.container}>
            <View style={styles.card}>
                {verificationSuccess ? (
                    <>
                        <Text style={styles.title}>Account Verified</Text>
                        <Text style={styles.message}>
                            Your account has been verified successfully!
                        </Text>
                        <Text style={styles.message}>
                            You can now close this window and log in to your account.
                        </Text>
                        <TouchableOpacity
                            style={styles.loginButton}
                            onPress={onNavigateToLogin}
                        >
                            <Text style={styles.buttonText}>Go to Login</Text>
                        </TouchableOpacity>
                    </>
                ) : (
                    <>
                        <Text style={styles.title}>Verification Failed</Text>
                        <Text style={styles.message}>
                            The verification link is invalid or has expired.
                        </Text>
                        <Text style={styles.message}>
                            Please request a new verification email.
                        </Text>
                    </>
                )}
            </View>
        </View>
    );
};

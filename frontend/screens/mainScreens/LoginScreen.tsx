import {Text, TextInput, TouchableOpacity, View} from 'react-native';
import {commonStyles} from '../css_styling/common/CommonProps';
import {LoginScreenType} from "../types/LoginScreenType";


export const LoginScreen = ({ email, password, onEmailChange, onPasswordChange, onLogin, onNavigateToRegister } : LoginScreenType) => (
    <View style={commonStyles.container}>
        <View style={commonStyles.card}>
            <Text style={commonStyles.title}>Login</Text>

            <TextInput
                style={commonStyles.searchInput}
                placeholder="Email"
                value={email}
                onChangeText={onEmailChange}
                keyboardType="email-address"
                autoCapitalize="none"
            />

            <TextInput
                style={commonStyles.searchInput}
                placeholder="Password"
                value={password}
                onChangeText={onPasswordChange}
                secureTextEntry
            />

            <View style={commonStyles.buttonsContainer}>
                <AuthButton text="Login" onPress={onLogin} testID="login-button" />
                <AuthButton
                    text="Go to Register"
                    onPress={onNavigateToRegister}
                    testID="register-button"
                />
            </View>
        </View>
    </View>
);

interface AuthButtonType {
    text: string;
    onPress: () => void;
    testID?: string;
}

export const AuthButton = ({ text, onPress, testID }: AuthButtonType) => (
    <TouchableOpacity
        style={commonStyles.loginRegisterButton}
        onPress={onPress}
        testID={testID}
    >
        <Text style={commonStyles.loginRegisterButtonText}>{text}</Text>
    </TouchableOpacity>
);
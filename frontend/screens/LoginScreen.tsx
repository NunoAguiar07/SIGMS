import React from 'react';
import {Text, TextInput, TouchableOpacity, View} from 'react-native';
import {commonStyles} from '../css_styling/common/CommonProps';

// @ts-ignore
export const LoginScreen = ({ email, password, onEmailChange, onPasswordChange, onLogin, onNavigateToRegister }) => (
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
                <TouchableOpacity
                    style={commonStyles.loginRegisterButton}
                    onPress={onLogin}
                >
                    <Text style={commonStyles.loginRegisterButtonText}>Login</Text>
                </TouchableOpacity>
                <TouchableOpacity
                    style={commonStyles.loginRegisterButton}
                    onPress={onNavigateToRegister}
                >
                    <Text style={commonStyles.loginRegisterButtonText}>Go to Register</Text>
                </TouchableOpacity>
            </View>
        </View>
    </View>
);
import React from 'react';
import {Text, TextInput, TouchableOpacity, View} from 'react-native';
import {styles} from '../css_styling/profile/RectangleProps';

// @ts-ignore
export const LoginScreen = ({ email, password, onEmailChange, onPasswordChange, onLogin, onNavigateToRegister }) => (
    <View style={styles.container}>
        <View style={styles.card}>
            <Text style={styles.title}>Login</Text>

            <TextInput
                style={styles.searchInput}
                placeholder="Email"
                value={email}
                onChangeText={onEmailChange}
                keyboardType="email-address"
                autoCapitalize="none"
            />

            <TextInput
                style={styles.searchInput}
                placeholder="Password"
                value={password}
                onChangeText={onPasswordChange}
                secureTextEntry
            />

            <View style={styles.buttonsContainer}>
                <TouchableOpacity
                    style={styles.loginRegisterButton}
                    onPress={onLogin}
                >
                    <Text style={styles.loginRegisterButtonText}>Login</Text>
                </TouchableOpacity>
                <TouchableOpacity
                    style={styles.loginRegisterButton}
                    onPress={onNavigateToRegister}
                >
                    <Text style={styles.loginRegisterButtonText}>Go to Register</Text>
                </TouchableOpacity>
            </View>
        </View>
    </View>
);
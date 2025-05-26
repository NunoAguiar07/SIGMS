import React from "react";
import "../css_styling/Error.css"
import {Text, TouchableOpacity, View} from "react-native";
import {commonStyles} from "../css_styling/common/CommonProps";


/**
 * Const "ErrorScreen" representing the page when an error happens in the application.
 * @param errorStatus represents the error code that happens.
 * @param errorMessage represents the error message we want to represent to the user.
 * @param goBack represent the button/function to do when we want to go back before the error.
 * @return the error page.
 */
// @ts-ignore
export const ErrorScreen = ({ errorStatus, errorMessage, goBack }) => {
    return (
        <View style={commonStyles.container}>
            <View style={commonStyles.card}>
                <Text style={[commonStyles.title, { color: '#671b22' }]}>Error {errorStatus}</Text>
                <Text style={[commonStyles.message, { marginBottom: 32 }]}>{errorMessage}</Text>
                <TouchableOpacity
                    style={commonStyles.loginRegisterButton}
                    onPress={goBack}
                >
                    <Text style={commonStyles.loginRegisterButtonText}>Go Back</Text>
                </TouchableOpacity>
            </View>
        </View>
    );
};
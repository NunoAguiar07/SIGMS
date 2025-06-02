import React from "react";
import {Modal, TouchableOpacity, View, Text} from "react-native";
import {welcomeStyles} from "../../../css_styling/welcome/WelcomeProps";


export const Privacy = ({ onClose }) => (
    <Modal visible={true} animationType="fade" transparent={true} onRequestClose={onClose}>
        <View style={welcomeStyles.modalOverlay}>
            <View style={welcomeStyles.modalContent}>
                <TouchableOpacity style={welcomeStyles.closeButton} onPress={onClose}>
                    <Text style={welcomeStyles.closeButtonText}>×</Text>
                </TouchableOpacity>

                <Text style={welcomeStyles.title}>Privacy Policy</Text>
                <Text>This is the privacy policy for SIGMS.</Text>
                <Text>We take your privacy seriously and are committed to protecting your personal information.</Text>
            </View>
        </View>
    </Modal>
);
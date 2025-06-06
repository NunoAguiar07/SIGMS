import React from "react";
import {Modal, TouchableOpacity, View, Text} from "react-native";
import {welcomeStyles} from "../../css_styling/welcome/WelcomeProps";
import {FooterType} from "../../types/FooterType";

export const FAQ = ({ onClose } :FooterType) => (
    <Modal visible={true} animationType="fade" transparent={true} onRequestClose={onClose}>
        <View style={welcomeStyles.modalOverlay}>
            <View style={welcomeStyles.modalContent}>
                <TouchableOpacity style={welcomeStyles.closeButton} onPress={onClose}>
                    <Text style={welcomeStyles.closeButtonText}>×</Text>
                </TouchableOpacity>

                <Text style={welcomeStyles.title}>Frequently Asked Questions</Text>

                <View style={{ marginVertical: 8 }}>
                    <Text style={welcomeStyles.question}>What is SIGMS?</Text>
                    <Text style={welcomeStyles.answer}>
                        SIGMS is a platform designed to simplify classroom management and enhance the learning experience.
                    </Text>
                </View>

                <View style={{ marginVertical: 8 }}>
                    <Text style={welcomeStyles.question}>How do I create an account?</Text>
                    <Text style={welcomeStyles.answer}>
                        You can create an account by clicking on the "SIGN IN WITH MICROSOFT" button on the homepage and following the instructions.
                    </Text>
                </View>
            </View>
        </View>
    </Modal>
);

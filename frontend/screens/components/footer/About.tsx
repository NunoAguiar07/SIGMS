import React from "react";
import { Modal, TouchableOpacity, View, Text } from "react-native";
import {welcomeStyles} from "../../../css_styling/welcome/WelcomeProps";

// @ts-ignore
export const About = ({ onClose }) => (
    <Modal visible={true} animationType="fade" transparent={true} onRequestClose={onClose}>
        <View style={welcomeStyles.modalOverlay}>
            <View style={welcomeStyles.modalContent}>
                <TouchableOpacity style={welcomeStyles.closeButton} onPress={onClose}>
                    <Text style={welcomeStyles.closeButtonText}>×</Text>
                </TouchableOpacity>

                <Text style={welcomeStyles.title}>About SIGMS</Text>
                <Text>This app was made by:</Text>
                <View style={{ marginVertical: 8 }}>
                    <Text>- Nuno Aguiar</Text>
                    <Text>- Tomás Martinho</Text>
                    <Text>- Felipe Alvarez</Text>
                </View>
                <Text>Now we need to write a bit more!!</Text>
            </View>
        </View>
    </Modal>
);

import React from "react";
import {Modal} from "react-native";
import {FooterType} from "../../types/FooterType";
import {Card, CenteredContainer} from "../../css_styling/common/NewContainers";
import {BodyText, Subtitle} from "../../css_styling/common/Typography";
import {ActionButton} from "../../css_styling/common/Buttons";
import {Ionicons} from "@expo/vector-icons";


export const Privacy = ({ onClose } : FooterType) => (
   <Modal visible={true} animationType="fade" transparent={true} onRequestClose={onClose}>
       <CenteredContainer flex={1} justifyContent="center" padding="md">
           <Card shadow="medium" alignItems={"center"} gap="md">
                <Subtitle>Privacy Policy</Subtitle>
                <BodyText>This is the privacy policy for SIGMS.</BodyText>
                <BodyText>We take your privacy seriously and are committed to protecting your personal information.</BodyText>
                <ActionButton
                    variant="primary"
                    onPress={onClose}
                >
                    <Ionicons name="close" size={24} color="white" />
                </ActionButton>
            </Card>
       </CenteredContainer>
   </Modal>
);
import React from "react";
import {FooterType} from "../../types/FooterType";
import {StyledModal} from "../../css_styling/common/Modal";
import {Card, CenteredContainer} from "../../css_styling/common/NewContainers";
import {BodyText, Subtitle} from "../../css_styling/common/Typography";
import {ActionButton} from "../../css_styling/common/Buttons";
import {Ionicons} from "@expo/vector-icons";

export const About = ({ onClose }: FooterType) => (
    <StyledModal visible={true} animationType="fade" transparent={true} onRequestClose={onClose}>
        <CenteredContainer flex={1} justifyContent="center" padding="md">
            <Card shadow="medium" alignItems={"center"} gap="md">
                <Subtitle>About SIGMS</Subtitle>
                <BodyText>This app was made by:</BodyText>
                <BodyText>- Nuno Aguiar</BodyText>
                <BodyText>- Tomás Martinho</BodyText>
                <BodyText>- Felipe Alvarez</BodyText>
                <ActionButton
                    variant="primary"
                    onPress={onClose}
                >
                    <Ionicons name={"close"} size={24} color="white" />
                </ActionButton>
            </Card>
        </CenteredContainer>
    </StyledModal>
);

import React from "react";
import {FooterType} from "../../types/FooterType";
import {StyledModal} from "../../css_styling/common/Modal";
import {Card, CenteredContainer} from "../../css_styling/common/NewContainers";
import {BodyText, Subtitle} from "../../css_styling/common/Typography";
import {ActionButton} from "../../css_styling/common/Buttons";
import {Ionicons} from "@expo/vector-icons";

export const FAQ = ({ onClose } :FooterType) => (
    <StyledModal visible={true} animationType="fade" transparent={true} onRequestClose={onClose}>
        <CenteredContainer flex={1} justifyContent="center" padding="md">
            <Card shadow="medium" alignItems={"center"} gap="md">
                <Subtitle>Frequently Asked Questions</Subtitle>
                <BodyText>What is SIGMS?</BodyText>
                <BodyText>
                    SIGMS is a platform designed to simplify classroom management and enhance the learning experience.
                </BodyText>
                <BodyText>How do I create an account?</BodyText>
                <BodyText>
                    You can create an account by clicking on the "SIGN IN WITH MICROSOFT" button on the homepage and following the instructions.
                </BodyText>
                <ActionButton
                    variant="primary"
                    onPress={onClose}
                >
                    <Ionicons name={"close"} size={24} color="white"/>
                </ActionButton>
            </Card>
        </CenteredContainer>
    </StyledModal>
);

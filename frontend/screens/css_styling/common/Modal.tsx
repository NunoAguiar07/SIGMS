import {Theme} from "./Theme";
import styled from "styled-components/native";
import {Modal} from "react-native";


interface ModalProps {
    theme: Theme;
    visible: boolean;
    animationType?: 'none' | 'slide' | 'fade';
    transparent?: boolean;
    onRequestClose?: () => void;
    padding?: keyof Theme['spacing'];
    borderRadius?: keyof Theme['borderRadius'];
    backgroundColor?: string;
    overlayBackgroundColor?: string;
}

export const StyledModal = styled(Modal).attrs<ModalProps>(
    ({ theme, animationType = 'fade', transparent = true }) => ({
        animationType,
        transparent,
        hardwareAccelerated: true,
    })
)<ModalProps>``;
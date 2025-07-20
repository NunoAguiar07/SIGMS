import {Picker} from "@react-native-picker/picker";
import styled from "styled-components/native";
import {Theme} from "./Theme";
import {Container} from "./NewContainers";
import {isMobile} from "../../../utils/DeviceType";

interface StyledPickerProps {
    theme: Theme;
    dropdownIconColor?: string;
    mode?: 'dialog' | 'dropdown';
}

export const StyledPicker = styled(Picker).attrs<StyledPickerProps>((props) => ({
    dropdownIconColor: props.dropdownIconColor || "#666",
    mode: isMobile ? 'dropdown' : 'dialog',
}))<StyledPickerProps>`
    color: ${({ theme }) => theme.colors.text.black};
    border-radius: ${({ theme }) => theme.borderRadius.medium}px;
    border-width: 2px;
    border-color: ${({ theme }) => theme.colors.border};
    font-size: ${({ theme }) => theme.fonts.sizes.medium}px;
    min-height: 50px;
    width: 100%;
`;

export const StyledPickerItem = styled(Picker.Item)<StyledPickerProps>`
    font-family: ${({ theme }) => theme.fonts.family.regular};
    font-size: ${({ theme }) => theme.fonts.sizes.medium}px;
    color: ${({ theme }) => theme.colors.text.black};
    min-height: 50px;
`;

export const PickerContainer = styled(Container)`
    background-color: ${({ theme }) => theme.colors.background.transparent};
    align-self: center; 
`;

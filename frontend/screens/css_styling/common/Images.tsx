import styled from "styled-components/native";
import { Image } from 'react-native';
import {Theme} from "./Theme";

interface LogoProps {
    width?: string;
    height?: number;
    resizeMode?: 'cover' | 'contain' | 'stretch' | 'repeat' | 'center';
}

export const Logo = styled(Image).attrs({
    source: require('../../../assets/Logo.webp')
})<LogoProps>`
    width: ${({ width = '100%' }) => width};
    height: ${({ height = 250 }) => height}px;
    resize-mode: ${({ resizeMode = 'contain' }) => resizeMode};
`;

interface ImageComponentProps {
    size?: number;
    borderRadius?: keyof Theme['borderRadius'];
    borderWidth?: number;
    borderColor?: string;
    backgroundColor?: string;
    resizeMode?: 'cover' | 'contain' | 'stretch' | 'repeat' | 'center';
    theme?: Theme;
}

export const ImageComponent = styled.Image<ImageComponentProps>`
    width: ${({ size }) => size || 100}px;
    height: ${({ size }) => size || 100}px;
    border-radius: ${({ borderRadius, theme }) =>
    borderRadius ? theme.borderRadius[borderRadius] : 0}px;
    ${({ borderWidth }) => borderWidth && `border-width: ${borderWidth}px;`}
    ${({ borderColor }) => borderColor && `border-color: ${borderColor};`}
    ${({ backgroundColor }) => backgroundColor && `background-color: ${backgroundColor};`}
    resize-mode: ${({ resizeMode = 'cover' }) => resizeMode};
`;

interface ImageWrapperProps extends ImageComponentProps {
    onPress?: () => void;
}

export const ImageWrapper = styled.TouchableOpacity<ImageWrapperProps>`
    width: ${({ size }) => size || 100}px;
    height: ${({ size }) => size || 100}px;
    border-radius: ${({ borderRadius, theme }) =>
    borderRadius ? theme.borderRadius[borderRadius] : 0}px;
    ${({ borderWidth }) => borderWidth && `border-width: ${borderWidth}px;`}
    ${({ borderColor }) => borderColor && `border-color: ${borderColor};`}
    ${({ backgroundColor }) => backgroundColor && `background-color: ${backgroundColor};`}
    align-items: center;
    justify-content: center;
    overflow: hidden;
`;
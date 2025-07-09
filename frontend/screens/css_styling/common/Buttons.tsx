import styled from 'styled-components/native';
import { Theme } from './Theme';
import {StyledText} from "./Typography";
import {Container, ContainerProps} from "./NewContainers";
import { Image } from 'react-native';

interface ButtonProps {
    theme: Theme;
    variant?: 'primary' | 'success' | 'error' | 'warning' | 'white';
    size?: 'small' | 'medium' | 'large';
    fullWidth?: boolean;
    disabled?: boolean;
}

const getButtonColor = (variant: string, theme: Theme) => {
    switch (variant) {
        case 'primary':
            return theme.colors.primary;
        case 'success':
            return theme.colors.status.success;
        case 'error':
            return theme.colors.status.error;
        case 'warning':
            return theme.colors.status.warning;
        case 'white':
            return theme.colors.background.white;
        default:
            return theme.colors.primary;
    }
};

const getButtonSize = (size: string, theme: Theme) => {
    switch (size) {
        case 'small':
            return {
                paddingVertical: theme.spacing.sm,
                paddingHorizontal: theme.spacing.lg,
                fontSize: theme.fonts.sizes.small,
            };
        case 'large':
            return {
                paddingVertical: theme.spacing.lg,
                paddingHorizontal: theme.spacing.xxxl,
                fontSize: theme.fonts.sizes.large,
            };
        default:
            return {
                paddingVertical: theme.spacing.md,
                paddingHorizontal: theme.spacing.xxl,
                fontSize: theme.fonts.sizes.large,
            };
    }
};

export const Button = styled.TouchableOpacity<ButtonProps>`
  background-color: ${({ variant = 'primary', theme, disabled }) =>
    disabled ? theme.colors.text.light : getButtonColor(variant, theme)
};
  border-radius: ${({ theme }) => theme.borderRadius.medium}px;
  align-items: center;
  justify-content: center;
  ${({ size = 'medium', theme }) => {
    const buttonSize = getButtonSize(size, theme);
    return `
      padding-vertical: ${buttonSize.paddingVertical}px;
      padding-horizontal: ${buttonSize.paddingHorizontal}px;
    `;
}}
  ${({ fullWidth }) => fullWidth && 'width: 100%;'}
  ${({ disabled }) => disabled && 'opacity: 0.5;'}
`;

export const ButtonText = styled(StyledText)<{ size?: 'small' | 'medium' | 'large' }>`
  color: ${({ theme }) => theme.colors.text.white};
  font-family: ${({ theme }) => theme.fonts.family.regular};
  font-weight: ${({ theme }) => theme.fonts.weights.regular};
  font-size: ${({ size = 'medium', theme }) => getButtonSize(size, theme).fontSize}px;
`;

export const ActionButton = styled(Button)`
  width: 32px;
  height: 32px;
  border-radius: 16px;
  padding: 0;
  margin: 2px;
`;

interface ButtonsContainerProps extends ContainerProps {
    gap?: keyof Theme['spacing'];
    theme: Theme;
}

export const ButtonsContainer = styled(Container)<ButtonsContainerProps>`
    margin-top: ${({ theme }) => theme.spacing.md}px;
    ${({ gap, theme }) => gap && `gap: ${theme.spacing[gap]}px;`}
`;

interface MicrosoftAuthButtonProps {
    disabled?: boolean;
    theme: Theme;
}

export const MicrosoftAuthButton = styled.TouchableOpacity<MicrosoftAuthButtonProps>`
    flex-direction: row;
    align-items: center;
    justify-content: center;
    padding-vertical: ${({ theme }) => theme.spacing.md}px;
    padding-horizontal: ${({ theme }) => theme.spacing.xl}px;
    border-radius: ${({ theme }) => theme.borderRadius.medium}px;
    border-width: 1px;
    border-color: ${({ theme }) => theme.colors.primary};
    background-color: transparent;
    margin-vertical: ${({ theme }) => theme.spacing.xs}px;
    width: 200px;
    opacity: ${({ disabled }) => (disabled ? 0.5 : 1)};
`;

export const MicrosoftLogo = styled(Image).attrs({
    source: require('../../../assets/Microsoft_logo.svg.png'),
    resizeMode: 'contain'
})`
    width: 24px;
    height: 24px;
`;

export const MicrosoftButtonText = styled(ButtonText)`
    color: ${({ theme }) => theme.colors.primary};
    margin-left: ${({ theme }) => theme.spacing.sm}px;
`;
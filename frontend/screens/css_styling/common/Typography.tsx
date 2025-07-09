
import styled from 'styled-components/native';
import { Theme } from './Theme';

interface TextProps {
    theme: Theme;
    color?: string;
    size?: keyof Theme['fonts']['sizes'];
    weight?: keyof Theme['fonts']['weights'];
    family?: keyof Theme['fonts']['family'];
    textAlign?: 'left' | 'center' | 'right';
    marginBottom?: keyof Theme['spacing'];
    marginTop?: keyof Theme['spacing'];
}

export const StyledText = styled.Text<TextProps>`
    color: ${({ color, theme }) => color || theme.colors.text.primary};
    font-size: ${({ size, theme }) => theme.fonts.sizes[size || 'medium']}px;
    font-weight: ${({ weight, theme }) => theme.fonts.weights[weight || 'regular']};
    font-family: ${({ family, theme }) => theme.fonts.family[family || 'regular']};
    ${({ textAlign }) => textAlign && `text-align: ${textAlign};`}
    ${({ marginBottom, theme }) => marginBottom && `margin-bottom: ${theme.spacing[marginBottom]}px;`}
    ${({ marginTop, theme }) => marginTop && `margin-top: ${theme.spacing[marginTop]}px;`}
`;

export const Title = styled(StyledText)`
    font-size: ${({ theme }) => theme.fonts.sizes.h1}px;
    font-family: ${({ theme }) => theme.fonts.family.regular};
    text-align: center;
`;

export const Subtitle = styled(StyledText)`
    font-size: ${({ theme }) => theme.fonts.sizes.xxl}px;
    font-family: ${({ theme }) => theme.fonts.family.regular};
    text-align: center;
    color: ${({ color, theme }) => color || theme.colors.text.white};
`;


export const SectionTitle = styled(StyledText)`
    font-size: ${({ theme }) => theme.fonts.sizes.large}px;
    font-weight: ${({ theme }) => theme.fonts.weights.bold};
    text-align: center;
    margin-bottom: ${({ theme }) => theme.spacing.lg}px;
`;

export const BodyText = styled(StyledText)`
    font-size: ${({ theme }) => theme.fonts.sizes.medium}px;
    color: ${({ theme }) => theme.colors.text.black};
`;

export const EmptyText = styled(StyledText)`
    font-size: ${({ theme }) => theme.fonts.sizes.large}px;
    color: ${({ theme }) => theme.colors.text.light};
    text-align: center;
    margin-top: ${({ theme }) => theme.spacing.xl}px;
`;



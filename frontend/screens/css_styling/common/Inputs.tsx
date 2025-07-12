import {Theme} from "./Theme";
import styled from "styled-components/native";

interface InputProps {
    theme: Theme;
    hasError?: boolean;
    multiline?: boolean;
    width?: string;
}

export const Input = styled.TextInput<InputProps>`
    border-width: 2px;
    border-color: ${({ hasError, theme }) =>
        hasError ? theme.colors.status.error : theme.colors.border
    };
    width: ${({ width }) => width || '200px'};
    border-radius: ${({ theme }) => theme.borderRadius.medium}px;
    padding: ${({ theme }) => theme.spacing.md}px;
    font-size: ${({ theme }) => theme.fonts.sizes.medium}px;
    background-color: ${({ theme }) => theme.colors.text.white};
    ${({ multiline }) => multiline && `
        min-height: 100px;
        max-height: 200px;
        text-align-vertical: top;
    `}
`;

export const SearchInput = styled(Input)`
    margin-bottom: ${({ theme }) => theme.spacing.lg}px;
`;


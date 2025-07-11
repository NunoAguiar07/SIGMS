import styled from "styled-components/native";
import {TouchableOpacity} from "react-native";
import {Theme} from "./Theme";

interface StyledFlatListProps {
    theme: Theme;
    position?: 'absolute' | 'relative' | 'static';
}

const BaseListItem = styled.View<StyledFlatListProps>`
    padding: ${({ theme }) => theme.spacing.md}px;
    border-bottom-width: 1px;
    border-bottom-color: ${({ theme }) => theme.colors.background.secondary};
    background-color: ${({ theme }) => theme.colors.text.white};
    min-width: 100%;
`;

export const FlatListItem = styled(BaseListItem).attrs({
    as: TouchableOpacity,
})`
    &:hover {
        background-color: ${({ theme }) => theme.colors.background.onPrimary};
    }
`;

export const FlatListDisplayItem = styled(BaseListItem)`
    flex-direction: row;
    justify-content: space-between; 
    align-items: center;           
    padding: ${({ theme }) => theme.spacing.md}px;
    gap: ${({ theme }) => theme.spacing.md}px;
    max-width: 200px;
`;

export const FlatListContainer = styled.View.attrs<StyledFlatListProps>((props) => ({
    position: props.position || 'absolute',
}))<StyledFlatListProps>`
    position: ${({ position }) => position};
    ${({ position }) => position === 'static' ? `
        width: auto;
        align-self: stretch; 
    ` : `
        top: 90%;
        width: 200px;
    `}
    background-color: ${({ theme }) => theme.colors.text.white};
    max-height: 250px;
    border-width: 2px;
    border-color: ${({ theme }) => theme.colors.border};
    border-radius: ${({ theme }) => theme.borderRadius.medium}px;
    z-index: 11;
    elevation: 5;
    shadow-color: ${({ theme }) => theme.colors.shadow};
    shadow-offset: 0px 2px;
    shadow-opacity: 0.2;
    shadow-radius: 4px;
`;
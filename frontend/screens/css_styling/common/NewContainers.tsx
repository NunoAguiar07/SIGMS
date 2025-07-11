import {Theme} from "./Theme";
import styled from 'styled-components/native';

export interface ContainerProps {
    theme: Theme;
    padding?: keyof Theme['spacing'];
    margin?: keyof Theme['spacing'];
    backgroundColor?: string;
    borderRadius?: keyof Theme['borderRadius'];
    flex?: number;
    justifyContent?: 'center' | 'flex-start' | 'flex-end' | 'space-between' | 'space-around' | 'space-evenly';
    alignItems?: 'center' | 'flex-start' | 'flex-end' | 'stretch';
    flexDirection?: 'row' | 'column';
    width?: string | number;
    height?: string | number;
    gap?: keyof Theme['spacing'];
}

export const Container = styled.View<ContainerProps>`
    ${({ flex }) => flex && `flex: ${flex};`}
    ${({ padding, theme }) => padding && `padding: ${theme.spacing[padding]}px;`}
    ${({ margin, theme }) => margin && `margin: ${theme.spacing[margin]}px;`}
    ${({ backgroundColor }) => backgroundColor && `background-color: ${backgroundColor};`}
    ${({ borderRadius, theme }) => borderRadius && `border-radius: ${theme.borderRadius[borderRadius]}px;`}
    ${({ justifyContent }) => justifyContent && `justify-content: ${justifyContent};`}
    ${({ alignItems }) => alignItems && `align-items: ${alignItems};`}
    ${({ flexDirection }) => flexDirection && `flex-direction: ${flexDirection};`}
    ${({ width }) => width && `width: ${typeof width === 'number' ? `${width}px` : width};`}
    ${({ height }) => height && `height: ${typeof height === 'number' ? `${height}px` : height};`}
    ${({ gap, theme }) => gap && `gap: ${theme.spacing[gap]}px;`} 
    
`;

export const SafeContainer = styled(Container)`
    flex: 1;
`;

export const CenteredContainer = styled(Container)`
    justify-content: center;
    align-items: center;
`;

export const RowContainer = styled(Container)`
    flex-direction: row;
`;

export const ColumnContainer = styled(Container)`
    flex-direction: column;
`;

interface CardProps {
    theme: Theme;
    variant?: 'primary' | 'secondary';
    shadow?: keyof Theme['shadows'];
}

export const Card = styled(Container).attrs({
    padding: 'huge',
    borderRadius: 'huge'
})<CardProps>`
    background-color: ${({ theme, variant = 'primary' }) =>
            variant === 'primary' ? theme.colors.background.card : theme.colors.background.secondary
    };
    border-width: 8px;
    border-color: ${({ theme }) => theme.colors.primaryDark};
    ${({ shadow, theme }) => shadow && {
        ...theme.shadows[shadow]
    }}
`;

export const GridRow = styled(RowContainer)<{ heightPercent?: number }>`
    height: ${({ heightPercent = 100 }) => heightPercent}%;
    width: 100%;
    justify-content: center;
    align-items: center;
`;

export const GridColumn = styled(ColumnContainer)<{ widthPercent?: number }>`
    width: ${({ widthPercent = 100 }) => widthPercent}%;
    height: 100%;
    justify-content: center;
    align-items: center;
`;

export const IssuesListContainer = styled(Container)`
    margin-top: 0;
    padding: ${({ theme }) => theme.spacing.md}px;
    width: 60%;
    background-color: ${({ theme }) => theme.colors.text.white};
    border-radius: ${({ theme }) => theme.borderRadius.medium}px;
    border-left-width: 6px;
    border-left-color: ${({ theme }) => theme.colors.primaryDark};
    ${({ theme }) => theme.shadows.medium}
`;

export const PaginationContainer = styled(RowContainer).attrs({
    justifyContent: 'space-between',
    alignItems: 'center',
    margin: 'lg',
    padding: 'lg',
})`
    border-top-width: 1px;
    border-color: ${({ theme }) => theme.colors.border}; 
`;

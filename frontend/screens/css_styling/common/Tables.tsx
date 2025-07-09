import styled from 'styled-components/native';
import {Container, RowContainer} from "./NewContainers";
import {StyledText} from "./Typography";
import {Input} from "./Inputs";
import {Theme} from "./Theme";

export const TableContainer = styled(Container)`
    flex: 1;
    width: 100%;
    border-radius: ${({ theme }) => theme.borderRadius.medium}px;
    
`;

export const TableHeader = styled(RowContainer)`
  background-color: ${({ theme }) => theme.colors.primaryLight};
  padding: ${({ theme }) => theme.spacing.md}px 0;
  align-items: center;
    width: 100%;
`;

export const TableRow = styled(RowContainer)`
  background-color: ${({ theme }) => theme.colors.background.secondary};
  border-bottom-width: 4px;
  border-left-width: 4px;
  border-right-width: 4px;
  border-color: ${({ theme }) => theme.colors.primaryLight};
  padding: ${({ theme }) => theme.spacing.md}px 0;
  align-items: center;
    width: 100%;
    min-height: 60px;
`;

export const HeaderText = styled(StyledText)`
  font-size: ${({ theme }) => theme.fonts.sizes.xl}px;
  font-weight: ${({ theme }) => theme.fonts.weights.bold};
  color: ${({ theme }) => theme.colors.text.white};
`;

export const CellText = styled(StyledText)`
  font-size: ${({ theme }) => theme.fonts.sizes.medium}px;
  color: ${({ theme }) => theme.colors.text.black};
`;

export const EditableCell = styled(Input)`
  margin: 0;
  padding: ${({ theme }) => theme.spacing.sm}px;
  border-color: ${({ theme }) => theme.colors.text.light};
`;

interface TableColumnProps {
    width?: number; // Flex basis (1-12 grid system like Bootstrap)
    align?: 'left' | 'center' | 'right';
    padding?: keyof Theme['spacing'];
    last?: boolean;
    first?: boolean;
    theme: Theme;
}

export const TableColumn = styled.View<TableColumnProps>`
    flex: ${({ width = 1 }) => width};
    justify-content: ${({ align = 'left' }) =>
            align === 'left' ? 'flex-start' :
                    align === 'right' ? 'flex-end' :
                            'center'
    };
    ${({ padding, theme }) => padding && `padding: 0 ${theme.spacing[padding]}px;`}
    ${({ first, theme }) => first && `padding-left: ${theme.spacing.md}px;`}
    ${({ last, theme }) => last && `padding-right: ${theme.spacing.md}px;`}
`;
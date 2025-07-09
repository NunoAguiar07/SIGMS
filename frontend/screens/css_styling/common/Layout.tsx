import styled from 'styled-components/native';
import {ColumnContainer, Container, RowContainer, SafeContainer} from "./NewContainers";
import {StyledText} from "./Typography";

export const ScreenContainer = styled(SafeContainer)`
  padding: ${({ theme }) => theme.spacing.xxxl}px;
`;

export const SplitContainer = styled(RowContainer)`
  flex: 1;
  background-color: ${({ theme }) => theme.colors.background.primary};
`;

export const LeftColumn = styled(ColumnContainer)`
  flex: 1;
  padding: ${({ theme }) => theme.spacing.lg}px;
  padding-top: 10%;
  border-right-width: 1px;
  border-right-color: ${({ theme }) => theme.colors.primaryLight};
  align-items: center;
  justify-content: center;
`;

export const RightColumn = styled(ColumnContainer)`
  flex: 1;
  padding: ${({ theme }) => theme.spacing.lg}px;
  justify-content: center;
  align-items: center;
`;

export const FooterContainer = styled(Container)`
  position: absolute;
  bottom: ${({ theme }) => theme.spacing.xl}px;
  left: ${({ theme }) => theme.spacing.xl}px;
  flex-direction: row;
  gap: ${({ theme }) => theme.spacing.xl}px;
`;

export const FooterLink = styled.TouchableOpacity``;

export const FooterLinkText = styled(StyledText)`
  color: ${({ theme }) => theme.colors.text.light};
  font-size: ${({ theme }) => theme.fonts.sizes.xxl}px;
  font-family: ${({ theme }) => theme.fonts.family.regular};
  font-weight: ${({ theme }) => theme.fonts.weights.regular};
  text-transform: none;
`;
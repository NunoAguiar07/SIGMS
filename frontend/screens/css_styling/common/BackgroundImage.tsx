
import styled from 'styled-components/native';
import { ImageBackground } from 'react-native';

// Styled ImageBackground component
export const StyledBackgroundImage = styled(ImageBackground)`
    flex: 1;
    width: 100%;
    height: 100%;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
`;

// Styled container for children
export const BackgroundContentContainer = styled.View`
    flex: 1;
`;
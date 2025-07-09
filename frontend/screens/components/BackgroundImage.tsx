import {ReactNode} from "react";
import {BackgroundContentContainer, StyledBackgroundImage} from "../css_styling/common/BackgroundImage";

interface BackgroundImageProps {
    children: ReactNode;
}

export const BackgroundImage = ({ children }: BackgroundImageProps) => {
    return (
        <StyledBackgroundImage
            source={require('../../assets/WebsiteBackground.png')}
            imageStyle={{ resizeMode: 'cover' }}
        >
            <BackgroundContentContainer>
                {children}
            </BackgroundContentContainer>
        </StyledBackgroundImage>
    );
};
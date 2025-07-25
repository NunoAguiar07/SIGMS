import {ProfileScreenType} from "../types/ProfileScreenType";
import {Card, CenteredContainer} from "../css_styling/common/NewContainers";
import {ImageComponent, ImageWrapper} from "../css_styling/common/Images";
import {theme} from "../css_styling/common/Theme";
import {Subtitle, Title} from "../css_styling/common/Typography";
import {ProfileInterface} from "../../types/ProfileInterface";
import {isMobile} from "../../utils/DeviceType";


export const ProfileScreen = ({
    profile,
    image,
    onPickImage
}: ProfileScreenType) => {
    return (
        <CenteredContainer flex={1} >
            <Card shadow="medium" alignItems={"center"} gap="md">
                <ProfileImage
                    imageUri={image}
                    onPress={onPickImage}
                />
                <ProfileInfo
                    username={profile.username}
                    email={profile.email}
                    university={profile.university}
                    officeRoomName={profile.officeRoomName || ''}
                    image={profile.image}
                />
            </Card>
        </CenteredContainer>
    )
};

interface ProfileImageType {
    imageUri: string | null;
    onPress: () => void;
}

const ProfileImage = ({ imageUri, onPress }: ProfileImageType) => (
    <ImageWrapper
        onPress={onPress}
        size={120}
        borderRadius="huge"
        borderWidth={4}
        borderColor={theme.colors.primaryDark}
    >
        <ImageComponent
            size={120}
            source={
                imageUri && imageUri.startsWith('data:image/jpeg;base64,')
                    ? { uri: imageUri }
                    : require('../../assets/default_user_profile.png')
            }
        />
    </ImageWrapper>
);

const ProfileInfo = ({username, email, university, officeRoomName, image}: ProfileInterface) => (
    <CenteredContainer>
        { !isMobile ? (
            <CenteredContainer>
                <Title color={theme.colors.text.white}>{username}</Title>
                <Title color={theme.colors.text.white}>{email}</Title>
                <Title color={theme.colors.text.white}>{university}</Title>
                {officeRoomName && (
                    <Title color={theme.colors.text.white}>
                        Office: {officeRoomName}
                    </Title>
                )}
            </CenteredContainer>
        ):(
            <CenteredContainer>
                <Subtitle color={theme.colors.text.black}>{username}</Subtitle>
                <Subtitle color={theme.colors.text.black}>{email}</Subtitle>
                <Subtitle color={theme.colors.text.black}>{university}</Subtitle>
                {officeRoomName && (
                    <Subtitle color={theme.colors.text.black}>
                        Office: {officeRoomName}
                    </Subtitle>
                )}
            </CenteredContainer>
        )}
    </CenteredContainer>
);


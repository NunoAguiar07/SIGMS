import {profileStyles} from "../css_styling/profile/RectangleProps";
import {commonStyles} from "../css_styling/common/CommonProps";
import {ProfileScreenType} from "../types/ProfileScreenType";
import {Image} from "expo-image";
import {Text, TouchableOpacity, View} from "react-native";


export const ProfileScreen = ({
    profile,
    image,
    onPickImage
}: ProfileScreenType) => {
    return (
            <View style={commonStyles.container}>
                <View style={commonStyles.card}>
                    <ProfileImage
                        imageUri={image}
                        onPress={onPickImage}
                    />
                    <ProfileInfo
                        name={profile.username}
                        email={profile.email}
                        university={profile.university}
                    />
                </View>
            </View>
        )
};

interface ProfileImageType {
    imageUri: string | null;
    onPress: () => void;
}

const ProfileImage = ({ imageUri, onPress }: ProfileImageType) => (
    <TouchableOpacity onPress={onPress} style={profileStyles.imageWrapper}>
        <Image
            source={
                typeof imageUri === 'string' && imageUri.startsWith('data:image/jpeg;base64,')
                    ? { uri: imageUri }
                    : require('../../assets/default_user_profile.png')
            }
            style={profileStyles.image}
            contentFit="cover"
        />
    </TouchableOpacity>
);

interface ProfileInfoType {
    name: string;
    email: string;
    university: string;
}

const ProfileInfo = ({ name, email, university }: ProfileInfoType) => (
    <>
        <Text style={profileStyles.name}>{name}</Text>
        <Text style={profileStyles.info}>{email}</Text>
        <Text style={profileStyles.info}>{university}</Text>
    </>
);


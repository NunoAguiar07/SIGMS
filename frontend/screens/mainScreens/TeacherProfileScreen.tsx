import {View, Image, Text} from "react-native";
import {commonStyles} from "../css_styling/common/CommonProps";
import {profileStyles} from "../css_styling/profile/RectangleProps";
import {TeacherProfileScreenType} from "../types/TeacherProfileScreenType";
import {TeacherProfileImageType} from "../../types/teacher/TeacherProfileImageType";
import {TeacherProfileInfoType} from "../../types/teacher/TeacherProfileInfoType";




export const TeacherProfileScreen = ({ profile }: TeacherProfileScreenType) => {
    const imageBase64 = profile.user.image?.length
        ? `data:image/jpeg;base64,${Buffer.from(profile.user.image).toString("base64")}`
        : null;


    return (
        <View style={commonStyles.container}>
            <View style={commonStyles.card}>
                <TeacherProfileImage imageUri={imageBase64} />
                <TeacherProfileInfo
                    name={profile.user.username}
                    email={profile.user.email}
                    university={profile.user.university}
                    office={profile.office.room.name}
                />
            </View>
        </View>
    );
};


const TeacherProfileImage = ({ imageUri }: TeacherProfileImageType) => (
    <View style={profileStyles.imageWrapper}>
        <Image
            source={
                typeof imageUri === 'string' && imageUri.startsWith('data:image/jpeg;base64,')
                    ? { uri: imageUri }
                    : require('../../assets/default_user_profile.png')
            }
            style={profileStyles.image}
            resizeMode="cover"
        />
    </View>
);



const TeacherProfileInfo = ({ name, email, university, office }: TeacherProfileInfoType) => (
    <>
        <Text style={profileStyles.name}>{name}</Text>
        <Text style={profileStyles.info}>{email}</Text>
        <Text style={profileStyles.info}>{university}</Text>
        <Text style={profileStyles.info}>Office: {office}</Text>
    </>
);
import {useLocalSearchParams} from "expo-router";
import {useTeacherProfile} from "../../../hooks/useTeacherProfile";
import ErrorHandler from "../../(public)/error";
import LoadingPresentation from "../../../screens/auxScreens/LoadingScreen";
import {TeacherProfileScreen} from "../../../screens/mainScreens/TeacherProfileScreen";
import {BackgroundImage} from "../../../screens/components/BackgroundImage";

const TeacherProfilePage = () => {
    const { id } = useLocalSearchParams();
    const { profile, loading, error } = useTeacherProfile(Number(id));

    if (error) {
        return <ErrorHandler errorStatus={error.status} errorMessage={error.message} />;
    }

    if (loading || !profile) {
        return <LoadingPresentation />;
    }

    return (
        <BackgroundImage>
        <TeacherProfileScreen profile={profile}/>
        </BackgroundImage>
    );
};

export default TeacherProfilePage;
import ErrorHandler from "../(public)/error";
import LoadingPresentation from "../../screens/auxScreens/LoadingScreen";
import {ProfileScreen} from "../../screens/mainScreens/ProfileScreen";
import {useProfile} from "../../hooks/useProfile";
import {BackgroundImage} from "../../screens/components/BackgroundImage";


const Profile = () => {
    const {
        profile,
        imageUri,
        loading,
        error,
        updateLoading,
        updateError,
        pickImage,
    } = useProfile();

    if (error) {
        return <ErrorHandler errorStatus={error.status} errorMessage={error.message} />;
    }
    if(updateError) {
        return <ErrorHandler errorStatus={updateError.status} errorMessage={updateError.message} />;
    }
    if (loading || !profile || updateLoading) {
        return <LoadingPresentation />;
    }


    return (
        <BackgroundImage>
            <ProfileScreen
                profile={profile}
                image={imageUri}
                onPickImage={pickImage}
            />
        </BackgroundImage>
    );
};

export default Profile;
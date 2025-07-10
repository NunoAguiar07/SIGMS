import ErrorHandler from "../(public)/error";
import LoadingPresentation from "../../screens/auxScreens/LoadingScreen";
import {ProfileScreen} from "../../screens/mainScreens/ProfileScreen";
import {useProfile} from "../../hooks/useProfile";


const Profile = () => {
    const {
        profile,
        imageUri,
        loading,
        error,
        updateLoading,
        updateError,
        pickImage,
    } = useProfile(undefined);

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
        <ProfileScreen
            profile={profile}
            image={imageUri}
            onPickImage={pickImage}
        />
    );
};

export default Profile;
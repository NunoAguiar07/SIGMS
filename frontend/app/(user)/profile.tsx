import ErrorHandler from "../(public)/error";
import LoadingPresentation from "../../screens/auxScreens/LoadingScreen";
import {ProfileScreen} from "../../screens/ProfileScreen";
import {useUpdateProfile} from "../../hooks/useUpdateProfile";
import {useProfileData} from "../../hooks/useProfileData";


const Profile = () => {
    const { profile, errorProfileData, loadingProfileData } = useProfileData();
    const { updateProfile, errorUpdateProfile, loadingUpdateProfile } = useUpdateProfile();

    if (errorProfileData) return <ErrorHandler errorStatus={errorProfileData.status} errorMessage={errorProfileData.message} />;
    if (errorUpdateProfile) return <ErrorHandler errorStatus={errorUpdateProfile.status} errorMessage={errorUpdateProfile.message} />;
    if (loadingProfileData || !profile || loadingUpdateProfile) return <LoadingPresentation />;

    return <ProfileScreen profile={profile} onUpdateProfile={updateProfile} />;
};

export default Profile;
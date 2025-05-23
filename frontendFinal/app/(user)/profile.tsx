import {useEffect, useState} from "react";
import {ErrorInterface} from "../../interfaces/ErrorInterface";
import {GetProfile} from "../../requests/ProfilePage";
import ErrorHandler from "../(public)/error";
import LoadingPresentation from "../../screens/Loading";
import {ProfileScreen} from "../../screens/ProfileScreen";


const Profile = () => {
    // @ts-ignore
    const [profile, setProfile] = useState<IProfile | null>(null);
    const [error, setError] = useState<ErrorInterface | null>(null);

    useEffect(() => {
        const fetchData = GetProfile(setProfile, setError);
        fetchData();
    }, []);

    // Handle errors
    if (error) return <ErrorHandler errorStatus={error.status} errorMessage={error.message} />;

    // Show loading while data is being fetched
    if (!profile) return <LoadingPresentation />;
    return <ProfileScreen profile ={profile}  />;
};

export default Profile;
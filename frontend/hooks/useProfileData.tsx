import {useEffect, useState} from "react";
import {ProfileInterface} from "../types/ProfileInterface";
import {ParsedError} from "../types/errors/ParseErrorTypes";
import {fetchProfile} from "../services/authorized/fetchProfile";

export const useProfileData = () => {
    const [profile, setProfile] = useState<ProfileInterface | null>(null);
    const [error, setError] = useState<ParsedError | null>(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const loadData = async () => {
            try {
                const profileData = await fetchProfile();
                setProfile(profileData);
            } catch (error) {
                setError(error as ParsedError);
            } finally {
                setLoading(false);
            }
        };
        loadData();
    }, []);

    return { profile, errorProfileData: error, loadingProfileData: loading };
};
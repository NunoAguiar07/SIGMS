import {useState} from "react";
import {ParsedError} from "../types/errors/ParseErrorTypes";
import {ProfileInterface} from "../types/ProfileInterface";
import {requestUpdateProfile} from "../services/authorized/requestUpdateProfile";


export const useUpdateProfile = () => {
    const [error, setError] = useState<ParsedError | null>(null);
    const [loading, setLoading] = useState(false);

    const update = async (profileData: Partial<ProfileInterface>) => {
        setLoading(true);
        try {
            return await requestUpdateProfile(profileData);
        } catch (error) {
            setError(error as ParsedError);
        } finally {
            setLoading(false);
        }
    };

    return { updateProfile: update, errorUpdateProfile: error, loadingUpdateProfile: loading };
};

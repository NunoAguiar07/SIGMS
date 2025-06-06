import {ParsedError} from "../types/errors/ParseErrorTypes";
import {fetchProfile} from "../services/authorized/fetchProfile";
import {useEffect, useState} from "react";
import {ProfileInterface} from "../types/ProfileInterface";
import * as ImagePicker from "expo-image-picker";
import {requestUpdateProfile} from "../services/authorized/requestUpdateProfile";


export const useProfile = () => {
    const [profile, setProfile] = useState<ProfileInterface | null>(null);
    const [error, setError] = useState<ParsedError | null>(null);
    const [loading, setLoading] = useState(true);
    const [image, setImage] = useState<string | null>(null);
    const [updateLoading, setUpdateLoading] = useState(false);
    const [updateError, setUpdateError] = useState<ParsedError | null>(null);

    useEffect(() => {
        const loadData = async () => {
            try {
                const profileData = await fetchProfile();
                setProfile(profileData);
                setImage(profileData.image || null);
            } catch (err) {
                setError(err as ParsedError);
            } finally {
                setLoading(false);
            }
        };
        loadData();
    }, []);

    const pickImage = async () => {
        const permissionResult = await ImagePicker.requestMediaLibraryPermissionsAsync();
        if (!permissionResult.granted) {
            alert('Permission to access media library is required!');
            return;
        }

        const result = await ImagePicker.launchImageLibraryAsync({
            mediaTypes: ImagePicker.MediaTypeOptions.Images,
            allowsEditing: true,
            aspect: [1, 1],
            quality: 1,
        });
        console.log(result);
        if (!result.canceled) {
            const newImage = result.assets[0].uri;
            try {
                await updateProfile({ image: newImage });
                console.log(newImage);
            } catch (err) {
                setImage(profile?.image || null);
            }
        }
    };

    const updateProfile = async (profileData: Partial<ProfileInterface>) => {
        setUpdateLoading(true);
        try {
            const updatedProfile = await requestUpdateProfile(profileData);
            setProfile(updatedProfile);
            if (profileData.image) {
                setImage(profileData.image);
            }
            return updatedProfile;
        } catch (err) {
            setUpdateError(err as ParsedError);
            throw err;
        } finally {
            setUpdateLoading(false);
        }
    };

    return {
        profile,
        image,
        loading,
        error,
        updateLoading,
        updateError,
        pickImage,
        updateProfile,
    };
};
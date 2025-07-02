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
    const [image, setImage] = useState<number[] | null>(null);
    const [imageUri, setImageUri] = useState<string | null>(null);
    const [updateLoading, setUpdateLoading] = useState(false);
    const [updateError, setUpdateError] = useState<ParsedError | null>(null);



    useEffect(() => {
        const loadData = async () => {
            try {
                const profileData = await fetchProfile();
                setProfile(profileData);
                console.log('Profile data loaded:', profileData);

                if (profileData.image && profileData.image.length > 0) {
                    setImageUri(`data:image/jpeg;base64,${profileData.image}`);
                    console.log('imageUri:' + imageUri);
                    
                } else {
                    setImageUri(null);
                }
            } catch (err) {
                setError(err as ParsedError);
            } finally {
                setLoading(false);
            }
        };
        loadData();
    }, [imageUri]);

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
            base64: true,
        });

        if (!result.canceled && result.assets[0]) {
            const asset = result.assets[0];
            const base64Image = asset.base64;
            const uri = asset.uri;

            if (base64Image) {
                const byteCharacters = atob(base64Image);
                const byteNumbers = Array.from(byteCharacters, c => c.charCodeAt(0));
                const byteArray = Array.from(byteNumbers);

                try {
                    await updateProfile({
                        image: byteArray,
                        username: profile?.username
                    });

                    setImage(byteArray);
                    setImageUri(`data:image/jpeg;base64,${base64Image}`);
                } catch (err) {
                    console.error('Image update failed', err);
                    setImage(profile?.image || null);
                    setImageUri(profile?.image ? `data:image/jpeg;base64,${profile.image}` : null);
                }
            }
        }
    };

    const updateProfile = async (profileData: Partial<ProfileInterface>) => {
        setUpdateLoading(true);
        try {
            console.log(profileData);
            const updatedProfile = await requestUpdateProfile(profileData);
            setProfile(updatedProfile);
            if (profileData.image) {
                setImage(profileData.image);
            }
            console.log(updatedProfile)
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
        imageUri,
        loading,
        error,
        updateLoading,
        updateError,
        pickImage,
        updateProfile,
    };
};
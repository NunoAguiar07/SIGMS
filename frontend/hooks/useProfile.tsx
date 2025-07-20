import {ParsedError} from "../types/errors/ParseErrorTypes";
import {fetchProfile} from "../services/authorized/FetchProfile";
import {useEffect, useState} from "react";
import {ProfileInterface} from "../types/ProfileInterface";
import * as ImagePicker from "expo-image-picker";
import {requestUpdateProfile} from "../services/authorized/RequestUpdateProfile";
import {fetchTeacherProfileById} from "../services/authorized/FetchTeacherProfileById";
import AsyncStorage from "@react-native-async-storage/async-storage";
import {TeacherUser} from "../types/teacher/TeacherUser";
import {useAlert} from "./notifications/useAlert";
import * as ImageManipulator from 'expo-image-manipulator';



export const useProfile = (id:number | undefined) => {
    const [profile, setProfile] = useState<ProfileInterface | null>(null);
    const [error, setError] = useState<ParsedError | null>(null);
    const [loading, setLoading] = useState(true);
    const [image, setImage] = useState<number[] | null>(null);
    const [imageUri, setImageUri] = useState<string | null>(null);
    const [updateLoading, setUpdateLoading] = useState(false);
    const [updateError, setUpdateError] = useState<ParsedError | null>(null);
    const MAX_IMAGE_SIZE_BYTES = 512000; // 500KB
    const showAlert = useAlert();

    const isTeacherUser = (data: any): data is TeacherUser =>
        data.user && data.user.email;



    useEffect(() => {
        const loadData = async () => {
            setLoading(true);
            try {
                const myId = await AsyncStorage.getItem('userId');
                const checkTeacherAsStudent = typeof myId === 'string' && +myId !== id && id !== undefined
                    ? await fetchTeacherProfileById(id)
                    : await fetchProfile();
                const isTeacherProfile = await AsyncStorage.getItem('userRole') == 'TEACHER'
                let profileData: ProfileInterface | TeacherUser;
                let filteredProfileData: ProfileInterface;
                if ( isTeacherProfile && id !== undefined) {
                    profileData = await fetchTeacherProfileById(id);
                    filteredProfileData =
                         {
                            username: profileData.user.username,
                            email: profileData.user.email,
                            image: profileData.user.image,
                            university: profileData.user.university,
                            officeRoomName: profileData.office?.room?.name
                        }
                } else {
                   if (isTeacherUser(checkTeacherAsStudent)) {
                       console.log('verifying teacher as student');
                        profileData = checkTeacherAsStudent;
                        filteredProfileData = {
                            username: profileData.user.username,
                            email: profileData.user.email,
                            image: profileData.user.image,
                            university: profileData.user.university,
                            officeRoomName: profileData.office?.room?.name
                        };
                    }else {
                       filteredProfileData = checkTeacherAsStudent as ProfileInterface;
                   }
                }

                setProfile(filteredProfileData);

                if (filteredProfileData.image && filteredProfileData.image.length > 0) {
                    setImageUri(`data:image/jpeg;base64,${filteredProfileData.image}`);

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
        const myId = await AsyncStorage.getItem('userId');
        const isViewingOtherUser = typeof myId === 'string' && +myId !== id;

        if (isViewingOtherUser) {
            showAlert('Denied', 'You cannot change the profile image of another user!');
            return;
        }

        const permissionResult = await ImagePicker.requestMediaLibraryPermissionsAsync();
        if (!permissionResult.granted) {
            showAlert('Permission Denied', 'You need to grant permission to access the media library.');
            return;
        }

        const result = await ImagePicker.launchImageLibraryAsync({
            mediaTypes: ImagePicker.MediaTypeOptions.Images,
            allowsEditing: true,
            aspect: [1, 1],
            quality: 1,
        });

        if (!result.canceled && result.assets[0]) {
            const asset = result.assets[0];

            const manipulated = await ImageManipulator.manipulateAsync(
                asset.uri,
                [{ resize: { width: 256 } }],
                { compress: 0.15, base64: true }
            );

            const base64Image = manipulated.base64;
            if (!base64Image) {
                showAlert('Error', 'Failed to read image data.');
                return;
            }

            const byteCharacters = atob(base64Image);
            const byteNumbers = Array.from(byteCharacters, c => c.charCodeAt(0));
            const byteArray = Array.from(byteNumbers);

            if (byteArray.length > MAX_IMAGE_SIZE_BYTES) {
                showAlert('Image too large', 'Please choose an image smaller than 500KB.');
                return;
            }

            try {
                await updateProfile({
                    image: byteArray,
                    username: profile?.username
                });

                setImage(byteArray);
                setImageUri(`data:image/jpeg;base64,${base64Image}`);
            } catch (err) {
                showAlert('Update failed', 'Could not update your profile image.');
                setImage(profile?.image || null);
                setImageUri(profile?.image ? `data:image/jpeg;base64,${profile.image}` : null);
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
        imageUri,
        loading,
        error,
        updateLoading,
        updateError,
        pickImage,
        updateProfile,
    };
};
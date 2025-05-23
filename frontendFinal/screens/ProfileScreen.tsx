// @ts-ignore
import {styles} from "../css_styling/profile/RectangleProps";
// @ts-ignore
import {Image} from "expo-image";
import React, {useState} from "react";
import * as ImagePicker from "expo-image-picker";
import {Text, TouchableOpacity, View} from "react-native";

// @ts-ignore
export const ProfileScreen = ({profile} ) => {
    const [image, setImage] = useState(profile.image || null);

    const pickImage = async () => {
        console.log('pickImage');
        const permissionResult = await ImagePicker.requestMediaLibraryPermissionsAsync();

        if (!permissionResult.granted) {
            alert('Permission to access media library is required!');
            return;
        }

        const result = await ImagePicker.launchImageLibraryAsync({
            mediaTypes: ['images'],
            allowsEditing: true,
            aspect: [1, 1],
            quality: 1,
        });
        console.log(result);

        if (!result.canceled) {
            setImage(result.assets[0].uri);
        }
    };
    // @ts-ignore
    return (
        <View style={styles.container}>
            <View style={styles.card}>
                    <TouchableOpacity onPress={pickImage} style={styles.imageWrapper}>
                        <Image
                            source={
                                typeof image === 'string' && image.length > 0
                                    ? image
                                    : require('../assets/default-user-profile.png')
                            }
                            style={styles.image}
                            contentFit="cover" // optional
                        />
                    </TouchableOpacity>

                <Text style={styles.name}>{profile.name}</Text>
                <Text style={styles.info}>{profile.email}</Text>
                <Text style={styles.info}>{profile.university}</Text>
            </View>
        </View>
    );
}

/**
 * export const ProfileScreen = ({ profile }) => {
 *     const [image, setImage] = useState(profile.image || null);
 *
 *     const pickImage = async () => {
 *         const permissionResult = await ImagePicker.requestMediaLibraryPermissionsAsync();
 *
 *         if (!permissionResult.granted) {
 *             alert('Permission to access media library is required!');
 *             return;
 *         }
 *
 *         const result = await ImagePicker.launchImageLibraryAsync({
 *             mediaTypes: ImagePicker.MediaTypeOptions.Images,
 *             allowsEditing: true,
 *             aspect: [1, 1],
 *             quality: 1,
 *         });
 *
 *         if (!result.canceled) {
 *             setImage(result.assets[0].uri);
 *         }
 *     };
 *
 *     return (
 *         <View style={styles.container}>
 *             <View style={styles.card}>
 *                 <TouchableOpacity onPress={pickImage} style={styles.imageWrapper}>
 *                     <Image
 *                         source={
 *                             image
 *                                 ? { uri: image }
 *                                 : require('../assets/default-user-profile.png')
 *                         }
 *                         style={styles.image}
 *                     />
 *
 *                 </TouchableOpacity>
 *
 *                 <Text style={styles.name}>{profile.name}</Text>
 *                 <Text style={styles.info}>{profile.email}</Text>
 *                 <Text style={styles.info}>{profile.university}</Text>
 *
 *                 <Button title="Edit" onPress={pickImage} />
 *             </View>
 *         </View>
 *     );
 * };
 */
import {TouchableOpacity, View} from "react-native";
import {styles} from "../../css_styling/profile/RectangleProps";
import {Link} from "expo-router";
import {Ionicons} from "@expo/vector-icons";


export const UserNavBar = () => {
    return (
        <View style={styles.navBar}>
            <Link href="/userHome" asChild>
                <TouchableOpacity style={styles.iconContainer}>
                    <Ionicons name="home-outline" size={30} color="white" />
                </TouchableOpacity>
            </Link>
            <Link href="/calendar" asChild>
                <TouchableOpacity style={styles.iconContainer}>
                    <Ionicons name="calendar-outline" size={30} color="white" />
                </TouchableOpacity>
            </Link>
            <Link href="/joinSubject" asChild>
                <TouchableOpacity style={styles.iconContainer}>
                    <Ionicons name="checkbox-outline" size={30} color="white" />
                </TouchableOpacity>
            </Link>
            <Link href="/profile" asChild>
                <TouchableOpacity style={styles.iconContainer}>
                    <Ionicons name="person-outline" size={30} color="white" />
                </TouchableOpacity>
            </Link>
            <Link href="/notifications" asChild>
                <TouchableOpacity style={styles.iconContainer}>
                    <Ionicons name="notifications-outline" size={30} color="white" />
                </TouchableOpacity>
            </Link>
        </View>
    );
};
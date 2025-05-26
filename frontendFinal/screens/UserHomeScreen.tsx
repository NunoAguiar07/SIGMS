import {Text, TouchableOpacity, View} from "react-native";
import {styles} from "../css_styling/userHome/Props";
import handleLogout from "../requests/auth/Logout";

export const UserHomeScreen = () => {
    return (
        <View style={styles.container}>
            <View style={styles.welcomeContent}>
                <Text style={styles.centerMiddleText}>Welcome Back!</Text>
            </View>
            <View style={styles.logoutContainer}>
                <TouchableOpacity style={styles.logoutButton} onPress={handleLogout}>
                    <Text style={styles.logoutButtonText}>Logout</Text>
                </TouchableOpacity>
            </View>
        </View>
    );
};
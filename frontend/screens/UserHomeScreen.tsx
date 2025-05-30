import {commonStyles} from "../css_styling/common/CommonProps";
import {styles} from "../css_styling/userHome/Props";
import {Text, TouchableOpacity, View} from "react-native";
import handleLogout from "../requests/auth/Logout";

export const UserHomeScreen = () => {
    return (
        <View style={commonStyles.container}>
            <Text style={styles.centerMiddleText}>Welcome Back!</Text>
            <View style={commonStyles.footerContainer}>
                <TouchableOpacity style={styles.logoutButton} onPress={handleLogout}>
                    <Text style={styles.logoutButtonText}>Logout</Text>
                </TouchableOpacity>
            </View>
        </View>
    );
};
import {commonStyles} from "../css_styling/common/CommonProps";
import {styles} from "../css_styling/userHome/Props";
import {HomeScreenType} from "./types/HomeScreenType";
import {Text, TouchableOpacity, View} from "react-native";

export const HomeScreen = ({ onLogout }: HomeScreenType) => {
    return (
        <View style={commonStyles.container}>
            <Text style={styles.centerMiddleText}>Welcome Back!</Text>
            <View style={commonStyles.footerContainer}>
                <TouchableOpacity style={styles.logoutButton} onPress={onLogout}>
                    <Text style={styles.logoutButtonText}>Logout</Text>
                </TouchableOpacity>
            </View>
        </View>
    );
};
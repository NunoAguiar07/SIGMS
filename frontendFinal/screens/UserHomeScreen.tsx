import {Text, View} from "react-native";
import {styles} from "../css_styling/profile/RectangleProps";


export const UserHomeScreen = () => {
    return (
        <View style={styles.container}>
            {/* Main Content */}
            <View style={styles.welcomeContent}>
                <Text style={styles.centerMiddleText}>Welcome Back!</Text>
                {/* Add your user home content here */}
            </View>
        </View>
    );
};
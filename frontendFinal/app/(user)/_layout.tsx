import {styles} from "../../css_styling/profile/RectangleProps";
import {View} from "react-native";
import {Stack} from "expo-router";
import {UserNavBar} from "../../screens/navBar/UserNavBar";


export default function UserLayout() {
    return (
        <View style={styles.navContainer}>
            <Stack
                screenOptions={{
                    headerShown: false,
                }}
            />
            <UserNavBar />
        </View>
    );
}
import {navBarStyles} from "../../css_styling/navBar/NavBarProps";
import {View} from "react-native";
import {Stack} from "expo-router";
import {UserNavBar} from "../../screens/navBar/UserNavBar";


export default function UserLayout() {
    return (
        <View style={navBarStyles.navContainer}>
            <Stack
                screenOptions={{
                    headerShown: false,
                }}
            />
            <UserNavBar />
        </View>
    );
}
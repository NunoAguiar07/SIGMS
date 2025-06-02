import {TouchableOpacity, View} from "react-native";
import {navBarStyles} from "../../css_styling/navBar/NavBarProps";
import {Link} from "expo-router";
import {Ionicons} from "@expo/vector-icons";
import {useEffect, useState} from "react";
import {NavInterface, UserRole} from "../../types/navBar/NavInterface";
import AsyncStorage from "@react-native-async-storage/async-storage";
import {NAV_CONFIG} from "../../Utils/NavConfig";


export const UserNavBar = () => {
    const [userRole, setUserRole] = useState<UserRole>('STUDENT');
    const [navItems, setNavItems] = useState<NavInterface[]>([]);

    useEffect(() => {
        const fetchUserRole = async () => {
            try {
                const role = await AsyncStorage.getItem('userRole');
                if (role && Object.keys(NAV_CONFIG).includes(role)) {
                    setUserRole(role as UserRole);
                    setNavItems(NAV_CONFIG[role as UserRole]);
                }
            } catch (error) {
                console.error('Failed to fetch user role', error);
            }
        };
        fetchUserRole();
    }, []);

    if (navItems.length === 0) return null;

    return (
        <View style={navBarStyles.navBar}>
            {navItems.map((item) => (
                <Link href={item.href} asChild key={item.href}>
                    <TouchableOpacity style={navBarStyles.iconContainer}>
                        <Ionicons name={item.iconName} size={30} color="white" />
                    </TouchableOpacity>
                </Link>
            ))}
        </View>
    );
};
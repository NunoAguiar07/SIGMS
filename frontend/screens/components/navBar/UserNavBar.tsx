import {TouchableOpacity, View, Text} from "react-native";
import {navBarStyles} from "../../css_styling/navBar/NavBarProps";
import {Link, useRouter} from "expo-router";
import {Ionicons} from "@expo/vector-icons";
import {useEffect, useRef, useState} from "react";
import {NavInterface, UserRole} from "../../../types/navBar/NavInterface";
import AsyncStorage from "@react-native-async-storage/async-storage";
import {NAV_CONFIG} from "../../../utils/NavConfig";
import { Animated } from 'react-native';
import {isMobile} from "../../../utils/DeviceType";

export const UserNavBar = () => {
    const [userRole, setUserRole] = useState<UserRole>('STUDENT');
    const [navItems, setNavItems] = useState<NavInterface[]>([]);
    const [drawerOpen, setDrawerOpen] = useState(false);
    const drawerAnim = useRef(new Animated.Value(0)).current;
    const router = useRouter();

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

    const toggleDrawer = () => {
        Animated.timing(drawerAnim, {
            toValue: drawerOpen ? 0 : 1,
            duration: 300,
            useNativeDriver: true,
        }).start();
        setDrawerOpen(!drawerOpen);
    };

    const navigateAndClose = (href: string) => {
        toggleDrawer(); // Close drawer first
        setTimeout(() => router.push(href), 300); // Wait for animation to complete
    };

    const drawerTranslateX = drawerAnim.interpolate({
        inputRange: [0, 1],
        outputRange: [-300, 0]
    });

    const backdropOpacity = drawerAnim.interpolate({
        inputRange: [0, 1],
        outputRange: [0, 0.5]
    });

    if (navItems.length === 0) return null;

    if (isMobile) {
        return (
            <>
                <TouchableOpacity
                    style={navBarStyles.menuButton}
                    onPress={toggleDrawer}
                >
                    <Ionicons name="menu" size={30} color="white"/>
                </TouchableOpacity>

                {drawerOpen && (
                    <Animated.View
                        style={[
                            navBarStyles.drawerBackdrop,
                            {opacity: backdropOpacity}
                        ]}
                    >
                        <TouchableOpacity
                            style={navBarStyles.drawerBackdropTouchable}
                            onPress={toggleDrawer}
                            activeOpacity={1} // Prevent flash effect on press
                        />
                    </Animated.View>
                )}

                <Animated.View
                    style={[
                        navBarStyles.drawerContainer,
                        {transform: [{translateX: drawerTranslateX}]}
                    ]}
                >
                    {/* Drawer Header */}
                    <View style={navBarStyles.drawerHeader}>
                        <Text style={navBarStyles.drawerTitle}>Menu</Text>
                    </View>

                    {/* Navigation Items */}
                    {navItems.map((item) => (
                        <TouchableOpacity
                            key={item.href}
                            style={navBarStyles.drawerItem}
                            onPress={() => navigateAndClose(item.href)}
                        >
                            <Ionicons
                                name={item.iconName}
                                size={24}
                                color="#651c24"
                                style={navBarStyles.drawerIcon}
                            />
                            <Text style={navBarStyles.drawerItemText}>{item.label}</Text>
                        </TouchableOpacity>
                    ))}
                </Animated.View>
            </>
        );
    }

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
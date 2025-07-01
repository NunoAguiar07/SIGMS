import {commonStyles} from "../css_styling/common/CommonProps";
import {styles} from "../css_styling/userHome/Props";
import {HomeScreenType} from "../types/HomeScreenType";
import {Text, TouchableOpacity, View} from "react-native";
import {Logo} from "../components/Logo";
import {ContainerStyles, getColumnStyle} from "../css_styling/common/Containers";
import {TextStyles} from "../css_styling/common/Text";
import CalendarScreen from "./CalendarScreen";
import {NotificationsScreen} from "./NotificationsScreen";

export const HomeScreen = ({ onLogout, username, schedule, notifications, clearNotification, onClickProfile, onClickRoom }: HomeScreenType) => {
    return (
        <View style={commonStyles.container}>

            <View style={ContainerStyles.container}>
                <View style={getColumnStyle(30, 0)}>
                    <Logo/>
                    <View style={getColumnStyle(100, 60)}>
                        <Text style={[TextStyles.h1, TextStyles.regular, TextStyles.primary]}>A tua sala de aula, mais simples.</Text>
                        <Text style={[TextStyles.h1, TextStyles.regular, TextStyles.primary]}>Bem vindo, {username}</Text>
                    </View>
                </View>
                <View style={[getColumnStyle(60, 0), {margin: 10}]}>
                    <CalendarScreen schedule={schedule} onClickProfile={onClickProfile} onClickRoom={onClickRoom}/>
                </View>
                <View style={[getColumnStyle(10, 0), {margin: 10}]}>
                    <NotificationsScreen notifications={notifications} clearNotification={clearNotification}/>
                </View>
            </View>

            <View style={commonStyles.footerContainer}>
                <TouchableOpacity style={styles.logoutButton} onPress={onLogout}>
                    <Text style={styles.logoutButtonText}>Logout</Text>
                </TouchableOpacity>
            </View>
        </View>
    );
};
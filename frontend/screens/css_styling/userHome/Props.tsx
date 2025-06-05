import {StyleSheet} from "react-native";

export const styles = StyleSheet.create({

    container: {
        flex: 1,
        justifyContent: 'space-between',
    },
    welcomeContent: {
        flex: 1,
        justifyContent: 'center',
        alignItems: 'center',
    },
    centerMiddleText: {
        fontSize: 24,
        fontWeight: 'bold',
    },
    logoutContainer: {
        alignItems: 'flex-start',
        padding: 16,
    },
    logoutButton: {
        position: 'relative',
        backgroundColor: '#671b22',
        paddingVertical: 12,
        paddingHorizontal: 24,
        borderRadius: 10,
        marginVertical: 6,
        alignItems: 'center',
        width: 200,
        zIndex: 2,
    },
    logoutButtonText: {
        color: 'white',
        fontSize: 16,
        fontWeight: 'bold',
    },

})
import {StyleSheet} from "react-native";


export const navBarStyles = StyleSheet.create({
    navBar: {
        flexDirection: 'row',
        justifyContent: 'flex-start',
        alignItems: 'center',
        padding: 15,
        position: 'absolute',
        top: 0,
        left: 0,
        right: 0,
        zIndex: 10,
    },
    navButton: {
        paddingHorizontal: 15,
        paddingVertical: 8,
        marginRight: 10,
        borderRadius: 5,
        backgroundColor: '#e9e9e9',
    },
    navButtonText: {
        fontSize: 16,
        color: '#333333',
    },
    iconContainer: {
        backgroundColor: '#651c24',
        width: 60,
        height: 60,
        borderRadius: 30,
        justifyContent: 'center',
        alignItems: 'center',
        marginRight: 10,
    },
    navContainer: {
        flex: 1
    },
})
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
    menuButton: {
        position: 'absolute',
        top: 15,
        left: 15,
        zIndex: 100,
        backgroundColor: '#651c24',
        width: 50,
        height: 50,
        borderRadius: 25,
        justifyContent: 'center',
        alignItems: 'center',
    },
    drawerBackdrop: {
        ...StyleSheet.absoluteFillObject,
        zIndex: 99,
        backgroundColor: 'black',
    },
    drawerBackdropTouchable: {
        flex: 1,
    },
    drawerContainer: {
        position: 'absolute',
        top: 0,
        left: 0,
        bottom: 0,
        width: 280,
        backgroundColor: '#f8f0e6',
        paddingTop: 60,
        paddingHorizontal: 20,
        zIndex: 101,
    },
    drawerHeader: {
        paddingBottom: 20,
        borderBottomWidth: 1,
        borderBottomColor: '#ddd',
        marginBottom: 20,
    },
    drawerTitle: {
        fontSize: 24,
        fontWeight: 'bold',
        color: '#651c24',
    },
    drawerItem: {
        flexDirection: 'row',
        alignItems: 'center',
        paddingVertical: 15,
        borderBottomWidth: 1,
        borderBottomColor: '#eee',
    },
    drawerIcon: {
        marginRight: 15,
        width: 24,
    },
    drawerItemText: {
        fontSize: 18,
        color: '#651c24',
    },
})
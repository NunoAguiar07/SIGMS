import {StyleSheet} from "react-native";


export const welcomeStyles = StyleSheet.create({
    welcomeContainer: {
        flex: 1,
        backgroundColor: '#f5f5f5',
    },
    welcomeContent: {
        flex: 1,
        justifyContent: 'center',
        alignItems: 'center',
        padding: 20,
    },
    slogan: {
        color: '#671b22',
        fontSize: 42,
        fontFamily: 'Roboto Condensed',
        textAlign: 'center',
        marginBottom: 20,
    },
    microsoftButtonContainer: {
        marginTop: 10,
        marginBottom: 30,
    },
    footerText: {
        color: 'rgba(184, 178, 171, 1)',
        fontSize: 40,
        fontFamily: 'Roboto Condensed',
        fontWeight: '400',
        textTransform: 'none',
        textAlign: 'left',
    },
    microsoftButton: {
        flexDirection: 'row',
        alignItems: 'center',
        justifyContent: 'center',
        paddingVertical: 12,
        paddingHorizontal: 24,
        borderRadius: 10,
        borderWidth: 1,
        borderColor: '#671b22',
        backgroundColor: 'transparent',
        marginVertical: 6,
        width: 200,
    },

    microsoftButtonText: {
        color: '#671b22',
        fontSize: 18,
        fontFamily: 'Roboto Condensed',
        fontWeight: '400',
        marginLeft: 10,
    },
})
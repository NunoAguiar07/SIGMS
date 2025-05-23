import {StyleSheet} from 'react-native';

export const styles = StyleSheet.create({
    container: {
        flex: 1,
        justifyContent: 'center',
        alignItems: 'center',
        padding: 64, // 4rem ≈ 64px
        backgroundColor: '#fef6ef',
    },
    card: {
        backgroundColor: '#bb9996',
        borderRadius: 32, // 2rem ≈ 32px
        padding: 64,
        width: 600,
        borderWidth: 8,
        borderColor: '#651920',
        position: 'relative',
        alignItems: 'center',
    },
    imageWrapper: {
        position: 'relative',
        width: 300,
        height: 300,
        marginBottom: 32,
        justifyContent: 'center',
        alignItems: 'center',
    },
    image: {
        width: '100%',
        height: '100%',
        borderRadius: 150, // Half of width/height for circle
        zIndex: 4,
    },
    name: {
        fontWeight: 'bold',
        fontSize: 36,
        marginBottom: 16,
        color: '#f4ece5',
    },
    info: {
        marginVertical: 6,
        fontSize: 24,
        color: '#f4ece5',
    },
    editButton: {
        marginTop: 32,
        paddingVertical: 16,
        paddingHorizontal: 48,
        backgroundColor: '#651920',
        color: 'white', // Only applies in text components
        borderRadius: 32,
    },
    welcomeContainer: {
        flex: 1,
        backgroundColor: '#f5f5f5',
    },
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
    welcomeContent: {
        flex: 1,
        justifyContent: 'center',
        alignItems: 'center',
        padding: 20,
    },
    centerMiddleText: {
        color: '#671b22',
        fontFamily: '"Roboto Condensed"',
        fontWeight: 400,
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
});

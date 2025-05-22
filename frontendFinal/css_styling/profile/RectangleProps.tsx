import { StyleSheet } from 'react-native';

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
});

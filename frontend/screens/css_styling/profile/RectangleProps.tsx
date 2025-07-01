import {StyleSheet} from 'react-native';
import {isMobile} from "../../../utils/DeviceType";

export const profileStyles = StyleSheet.create({
    imageWrapper: {
        position: 'relative',
        width: 200,
        height: 200,
        marginBottom: 32,
        justifyContent: 'center',
        alignItems: 'center',
    },
    image: {
        width: '100%',
        height: '100%',
        borderRadius: 150,
        zIndex: 4,
    },
    name: {
        fontWeight: 'bold',
        fontSize: !isMobile ? 32 : 24,
        marginBottom: 16,
        color: '#f4ece5',
    },
    info: {
        marginVertical: 6,
        fontSize: !isMobile ? 32 : 24,
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
    background: {
        flex: 1,
        backgroundColor: '#fff',
        justifyContent: 'center',
        alignItems: 'center',
    },

    approvalCard: {
        backgroundColor: '#fff',
        borderRadius: 8,
        padding: 16,
        marginBottom: 12,
        shadowColor: '#000',
        shadowOffset: { width: 0, height: 2 },
        shadowOpacity: 0.1,
        shadowRadius: 4,
        elevation: 2,
    },
    infoContainer: {
        alignSelf: 'stretch',
        alignItems: 'center',
    },
});

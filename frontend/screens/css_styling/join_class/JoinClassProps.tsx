import {StyleSheet} from "react-native";


export const joinClassStyles = StyleSheet.create({
    joinClassButton: {
        marginTop: 16,
        paddingVertical: 8,
        paddingHorizontal: 24,
        backgroundColor: '#651920',
        color: 'white', // Only applies in text components
        borderRadius: 16,
        justifyContent: 'center',
        alignItems: 'center',
    },
    classItem: {
        flexDirection: 'row',
        justifyContent: 'space-between',
        alignItems: 'center',
        paddingVertical: 12,
        borderBottomWidth: 1,
        backgroundColor: '#fef6ef',
        borderBottomColor: '#651c24',
    },
    classesSection: {
        marginTop: '10%',
        borderTopWidth: 1,
        borderTopColor: '#fef6ef',
        paddingTop: 20,
    },
    joinButtonText: {
        color: 'white',
        fontSize: 14,  // Optional: smaller text if needed
        fontWeight: '500',
    },
    joinClassContainer: {
        flex: 1,
        flexDirection: 'row', // Horizontal layout
        backgroundColor: 'transparent',
    },
})
import { StyleSheet } from "react-native";

export const scheduleLectureStyles = StyleSheet.create({
    container: {
        flex: 1,
        flexDirection: 'row',
        backgroundColor: '#fef6ef',

    },
    lectureItem: {
        flexDirection: 'row',
        justifyContent: 'space-between',
        alignItems: 'center',
        paddingVertical: 12,
        paddingHorizontal: 8,
        borderBottomWidth: 1,
        borderBottomColor: '#651c24',
        backgroundColor: '#fef6ef',
    },
    scheduleForm: {
        flex: 1,
        padding: 24,
        justifyContent: 'flex-start',
        gap: 12,
    },
    inputField: {
        borderWidth: 1,
        borderColor: '#651c24',
        borderRadius: 12,
        paddingHorizontal: 12,
        paddingVertical: 8,
        backgroundColor: 'white',
        color: '#000',
    },
    saveButton: {
        marginTop: 16,
        paddingVertical: 10,
        paddingHorizontal: 24,
        backgroundColor: '#651920',
        borderRadius: 16,
        justifyContent: 'center',
        alignItems: 'center',
    },
    saveButtonText: {
        color: 'white',
        fontSize: 14,
        fontWeight: '500',
    }
});

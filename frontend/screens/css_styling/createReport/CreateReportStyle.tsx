import {StyleSheet} from "react-native";


export const createReportStyle = StyleSheet.create({
    joinClassButton: {
        minWidth: 150,
        marginTop: 16,
        paddingVertical: 8,
        paddingHorizontal: 24,
        backgroundColor: '#651920',
        color: 'white', // Only applies in text components
        borderRadius: 8,
        justifyContent: 'center',
        alignItems: 'center',
        alignSelf : 'center',
    },
    classItem: {
        flexDirection: 'row',
        justifyContent: 'space-between',
        alignItems: 'center',
        paddingVertical: 12,
        borderBottomWidth: 1,
        backgroundColor: '#f8f0e6FF',
        borderBottomColor: '#651c24',
    },
    issuesListContainer: {
        marginTop: 0,
        padding: 10,
        width: '60%',
        backgroundColor: '#fff',
        borderRadius: 8,
        borderLeftWidth: 6,
        borderLeftColor: '#3e1f1f',
        shadowColor: '#000',
        shadowOffset: { width: 1, height: 1 },
        shadowOpacity: 0.1,
        shadowRadius: 4,
        elevation: 3,
    },

    issueItem: {
        paddingVertical: 4,
        borderBottomWidth: 1,
        borderBottomColor: '#ddd',
    },

    issueText: {
        fontSize: 14,
        color: '#444',
    },
    classesSection: {
        marginTop: '10%',
        width: '60%',
        borderTopWidth: 1,
        borderTopColor: '#f8f0e6FF',
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
    reportInput: {
        height: 120,
        borderColor: '#651c24',
        borderWidth: 1,
        borderRadius: 12,
        padding: 10,
        backgroundColor: 'white',
        marginTop: 12,
        marginBottom: 16,
        textAlignVertical: 'top',
    },
    fullScreenContainer: {
        padding: 20,
        backgroundColor: "#fff",
        flexGrow: 1,
    },

})



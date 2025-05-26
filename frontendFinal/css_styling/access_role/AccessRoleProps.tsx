import {StyleSheet} from "react-native";


export const accessRoleStyles = StyleSheet.create({
    userName: {
        fontSize: 18,
        fontWeight: 'bold',
        marginBottom: 4,
    },
    userEmail: {
        color: '#666',
        marginBottom: 8,
    },
    roleText: {
        fontWeight: '500',
        marginBottom: 4,
    },
    dateText: {
        color: '#666',
        marginBottom: 12,
    },
    // Column Widths
    nameColumn: {
        minWidth: 150,
        flex: 4,
        paddingHorizontal: 32,
    },
    emailColumn: {
        flex: 4,
        minWidth: 250,
        paddingHorizontal: 32,
    },
    roleColumn: {
        flex: 4,
        minWidth: 250,
        paddingHorizontal: 32,
    },
    dateColumn: {
        flex: 4,
        minWidth: 200,
        paddingHorizontal: 32,
    },
    actionsColumn: {
        minWidth: 100,
        flex: 4,
    },
    // Action Buttons
    actionsCell: {
        flexDirection: 'row',
    },
    actionButton: {
        width: 32,
        height: 32,
        borderRadius: 16,
        justifyContent: 'center',
        alignItems: 'center',
        marginHorizontal: 2,
    },
    approveButton: {
        backgroundColor: '#4CAF50',
    },
    rejectButton: {
        backgroundColor: '#F44336',
    },
});
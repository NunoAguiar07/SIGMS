import {StyleSheet} from "react-native";

export const mobileStyles = StyleSheet.create({
    container: {
        flex: 1,
        justifyContent: 'center',
        alignItems: 'center',
        padding: 16,
    },
    card: {
        width: '90%',
        backgroundColor: '#bb9996',
        borderRadius: 12,
        padding: 20,
        borderWidth: 2,
        borderColor: '#651920',
        shadowColor: '#000',
        shadowOffset: { width: 0, height: 2 },
        shadowOpacity: 0.2,
        shadowRadius: 4,
        elevation: 5,
    },
    cardContent: {
        alignItems: 'center',
    },
    name: {
        fontSize: 20,
        fontWeight: 'bold',
        color: '#651920',
        marginBottom: 16,
    },
    detailRow: {
        flexDirection: 'row',
        justifyContent: 'space-between',
        width: '100%',
        marginBottom: 12,
    },
    detailLabel: {
        fontWeight: 'bold',
        color: '#651920',
    },
    detailValue: {
        color: '#333',
    },
    actionButtons: {
        flexDirection: 'row',
        justifyContent: 'space-around',
        width: '100%',
        marginTop: 20,
    },
    actionButton: {
        flexDirection: 'row',
        alignItems: 'center',
        paddingVertical: 10,
        paddingHorizontal: 20,
        borderRadius: 20,
    },
    approveButton: {
        backgroundColor: '#4CAF50',
    },
    rejectButton: {
        backgroundColor: '#F44336',
    },
    buttonText: {
        color: 'white',
        marginLeft: 8,
        fontWeight: 'bold',
    },
    paginationDots: {
        flexDirection: 'row',
        justifyContent: 'center',
        marginTop: 20,
    },
    dot: {
        width: 8,
        height: 8,
        borderRadius: 4,
        backgroundColor: '#ccc',
        marginHorizontal: 4,
    },
    activeDot: {
        backgroundColor: '#651920',
    },
    detailSection: {
        width: '100%',
        marginBottom: 12,
    },
    roomName: {
        fontSize: 20,
        fontWeight: 'bold',
        color: '#651920',
        marginBottom: 16,
        textAlign: 'center',
    },
    assignButton: {
        flexDirection: 'row',
        alignItems: 'center',
        backgroundColor: '#4CAF50',
        paddingVertical: 12,
        paddingHorizontal: 24,
        borderRadius: 20,
        marginTop: 20,
    },
    emptyText: {
        fontSize: 18,
        color: '#666',
    },
    descriptionInput: {
        width: '100%',
        borderWidth: 1,
        borderColor: '#651920',
        borderRadius: 8,
        padding: 10,
        backgroundColor: '#f8f0e6',
        minHeight: 100,
        textAlignVertical: 'top',
    },
    actions: {
        flexDirection: 'row',
        justifyContent: 'space-between',
        width: '100%',
        marginTop: 20,
    },
    editActions: {
        flexDirection: 'row',
        justifyContent: 'center',
        width: '100%',
        marginTop: 20,
        gap: 10,
    },
    editButton: {
        backgroundColor: '#FFC107',
    },
    fixButton: {
        backgroundColor: '#4CAF50',
    },
    unassignButton: {
        backgroundColor: '#F44336',
    },
    saveButton: {
        backgroundColor: '#4CAF50',
    },
    cancelButton: {
        backgroundColor: '#F44336',
    },
});
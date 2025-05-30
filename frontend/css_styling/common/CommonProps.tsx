import {StyleSheet} from "react-native";


export const commonStyles = StyleSheet.create({
    container: {
        flex: 1,
        justifyContent: 'center',
        alignItems: 'center',
        padding: 64, // 4rem ≈ 64px
        backgroundColor: '#f8f0e6FF',
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
        zIndex:3,
    },
    searchInput: {
        width: '100%',
        height: 50,
        borderWidth: 1,
        borderColor: '#651c24',
        borderRadius: 8,
        paddingHorizontal: 12,
        marginBottom: 16,
        marginTop: '10%',
    },
    itemSearch: {
        padding: 16,
        borderBottomWidth: 1,
        borderBottomColor: '#651c24',
    },
    itemText: {
        fontSize: 16,
    },
    emptyText: {
        textAlign: 'center',
        marginTop: 20,
        color: '#666',
    },
    searchContainer: {
        flex: 1,
        padding: 16,
        justifyContent: 'center',
        alignItems: 'center',
        backgroundColor: '#fef6ef',
    },
    searchSubjectInput: {
        height: 50,
        borderWidth: 1,
        borderColor: '#651c24',
        borderRadius: 8,
        paddingHorizontal: 12,
        marginBottom: 16,
        marginTop: '10%',
    },
    centerMiddleText: {
        color: '#671b22',
        fontFamily: '"Roboto Condensed"',
        fontWeight: 400,
    },
    sectionTitle: {
        fontSize: 18,
        fontWeight: 'bold',
        marginBottom: 15,
    },
    leftColumn: {
        flex: 1, // Takes 50% of width
        padding: 16,
        borderRightWidth: 1,
        borderRightColor: '#651c24',
        alignItems: 'center',
    },
    rightColumn: {
        flex: 1, // Takes 50% of width
        padding: 16,
        alignItems: 'center',
    },
    centerContainer: {
        alignItems: 'center',
        justifyContent: 'center',
        flex: 1,
    },
    logo: {
        width: 600,
        height: 300,
        resizeMode: 'contain',
        marginBottom: 20,
    },
    loginRegisterButton: {
        position: 'relative',
        backgroundColor: '#671b22',
        paddingVertical: 12,
        paddingHorizontal: 24,
        borderRadius: 10,
        marginVertical: 6,
        alignItems: 'center',
        width: 200,
        zIndex: 2,
    },
    loginRegisterButtonText: {
        color: '#fff',
        fontSize: 18,
        fontFamily: 'Roboto Condensed',
        fontWeight: '400',
    },
    title: {
        fontSize: 24,
        fontWeight: 'bold',
        textAlign: 'center',
        marginBottom: 20,
    },
    pickerContainer: {
        marginTop: 20,
        width: '100%',
        marginBottom: 20,
    },
    picker: {
        width: '100%',
        height: 50,
        color: '#000',
        backgroundColor: '#f8f0e6',
        borderRadius: 8,
    },
    inputRow: {
        flexDirection: 'row',
        justifyContent: 'space-between',
        width: '100%',
        marginBottom: 20,
    },
    inputRowItem: {
        width: '30%',
        marginTop: '5%',
    },
    buttonsContainer: {
        flexDirection: 'row',
        justifyContent: 'space-between',
        width: '100%',
        marginTop: 20,
    },
    message: {
        fontSize: 18,
        textAlign: 'center',
        marginBottom: 16,
        color: '#f4ece5',
    },
    loginButton: {
        backgroundColor: '#671b22',
        paddingVertical: 12,
        paddingHorizontal: 24,
        borderRadius: 10,
        marginTop: 20,
        alignItems: 'center',
        width: 200,
    },
    buttonText: {
        color: '#fff',
        fontSize: 18,
        fontFamily: 'Roboto Condensed',
        fontWeight: '400',
    },
    tableHeader: {
        flexDirection: 'row',
        paddingVertical: 12,
        alignItems: 'center',

        backgroundColor: '#651c24',
    },
    tableRow: {
        flexDirection: 'row',
        borderBottomWidth: 4,
        borderLeftWidth: 4,
        borderRightWidth: 4,
        borderColor: '#651c24',
        paddingVertical: 12,
        alignItems: 'center',
        backgroundColor: '#bb9996',
    },
    emptyRow: {
        padding: 20,
        alignItems: 'center',
    },
    headerText: {
        fontSize: 20,
        fontWeight: 'bold',
        color: 'white',
    },
    cellText: {
        fontSize: 16,
        color: 'white',
    },
    // Pagination
    pagination: {
        flexDirection: 'row',
        justifyContent: 'space-between',
        alignItems: 'center',
        marginTop: 16,
        paddingTop: 16,
        borderTopWidth: 1,
        borderColor: '#ddd',
    },
    paginationButton: {
        padding: 10,
        backgroundColor: '#e0e0e0',
        borderRadius: 4,
    },
    disabledButton: {
        opacity: 0.5,
    },
    pageNumber: {
        fontWeight: 'bold',
    },
    // New table container style
    tableContainer: {
        flex: 1,
        borderRadius: 8,
        overflow: 'hidden',
    },
    footerContainer: {
        position: 'absolute',
        bottom: 20,
        left: 20,
        flexDirection: 'row',
        gap: 20,
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
})
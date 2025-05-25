import {StyleSheet} from 'react-native';

export const styles = StyleSheet.create({
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
    searchSubjectInput: {
        height: 50,
        borderWidth: 1,
        borderColor: '#651c24',
        borderRadius: 8,
        paddingHorizontal: 12,
        marginBottom: 16,
        marginTop: '10%',
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
    classItem: {
        flexDirection: 'row',
        justifyContent: 'space-between',
        alignItems: 'center',
        paddingVertical: 12,
        borderBottomWidth: 1,
        backgroundColor: '#fef6ef',
        borderBottomColor: '#651c24',
    },
    sectionTitle: {
        fontSize: 18,
        fontWeight: 'bold',
        marginBottom: 15,
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
    joinClassContainer: {
        flex: 1,
        flexDirection: 'row', // Horizontal layout
        backgroundColor: '#fef6ef',
    },
    background: {
        flex: 1,
        backgroundColor: '#fff',
        justifyContent: 'center',
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
    footerContainer: {
        position: 'absolute',
        bottom: 20,
        left: 20,
        flexDirection: 'row',
        gap: 20,
    },
    footerText: {
        color: 'rgba(184, 178, 171, 1)',
        fontSize: 40,
        fontFamily: 'Roboto Condensed',
        fontWeight: '400',
        textTransform: 'none',
        textAlign: 'left',
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
        width: '30%', // Each input takes about 1/3 of the space
        marginTop: '5%',
    },
    universitySearchContainer: {
        width: '100%',
        justifyContent:'center',
        alignItems: 'center',
        position: 'relative',
        marginBottom: 20,
    },
    universityResultsContainer: {
        position: 'absolute',
        top: '60%', // Position below the search input
        left: 0,
        right: 0,
        backgroundColor: '#fff',
        maxHeight: 200,
        borderWidth: 1,
        borderColor: '#651c24',
        borderRadius: 8,
        zIndex: 4,
        elevation: 3, // For Android shadow
        shadowColor: '#000', // For iOS shadow
        shadowOffset: { width: 0, height: 2 },
        shadowOpacity: 0.2,
        shadowRadius: 4,
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
});

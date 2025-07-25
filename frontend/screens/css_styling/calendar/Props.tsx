import {StyleSheet} from 'react-native';

export const styles = StyleSheet.create({
    container: {
        flex: 1,
        justifyContent: 'center',
        alignItems: 'center',
        width: '95%',
        backgroundColor: 'transparent',
    },
    homeContainer: {
        flex: 1,
        justifyContent: 'center',
        alignItems: 'center',
        padding: 20,
        backgroundColor: '#ead6bd',
    },

    scrollbar: {
        position: 'absolute',
        right: 4,
        top: 0,
        bottom: 0,
        width: 6,
        backgroundColor: '#6C2E2E',
        borderRadius: 4,
    },

    content: {
        paddingRight: 10,
        height: '100%'
    },

    daySection: {
        width: '100%',
        backgroundColor: '#caa8a0',
        borderRadius: 12,
        padding: 12,
        marginBottom: 24,
        shadowColor: '#000',
        shadowOpacity: 0.1,
        shadowRadius: 6,
        elevation: 4,
    },


    dayTitle: {
        fontSize: 20,
        fontWeight: 'bold',
        color: '#2e0e0e',
    },

    daySubtitle: {
        color: '#6c4c4c',
        marginBottom: 12,
        fontSize: 14,
    },

    eventBlock: {
        backgroundColor: '#f9f1e7',
        borderRadius: 12,
        padding: 12,
    },

    eventItem: {
        flexDirection: 'column',
        alignItems: 'flex-start',
        marginBottom: 16,
        paddingLeft: 10,
        borderLeftWidth: 4,
        borderLeftColor: '#651c24',
    },


    eventTime: {
        fontWeight: 'bold',
        width: 110,
        color: '#6c2e2e',
        fontSize: 14,
    },

    eventTitle: {
        color: '#3b1f1f',
        fontSize: 15,
        flexShrink: 1,
    },
    cardContainer: {
        alignContent: 'space-evenly',
        width: '95%',
        maxWidth: 1000,
        height: '100%', // adjust this value to fit ~3 day sections
        borderRadius: 20,
        backgroundColor: '#b88d89',
        padding: 16,
        borderWidth: 4,
        borderColor: '#651c24',
        shadowColor: '#000',
        shadowOpacity: 0.1,
        shadowOffset: { width: 0, height: 4 },
        shadowRadius: 8,
        elevation: 5,
        overflow: 'hidden', // important for clean scroll boundaries
    },
    teacherButtonRow: {
        flexDirection: 'row',
        flexWrap: 'wrap',
        justifyContent: 'flex-end',
        width: '100%',
        marginTop: 8,
    },

    teacherCard: {
        backgroundColor: '#eee',
        paddingVertical: 8,
        paddingHorizontal: 16,
        borderRadius: 12,
        marginRight: 8,
        shadowColor: '#000',
        shadowOffset: { width: 0, height: 1 },
        shadowOpacity: 0.2,
        shadowRadius: 2,
        elevation: 2,
    },

    teacherCardText: {
        fontWeight: 'bold',
        fontSize: 14,
        color: '#651920',
    },
    teacherNames: {
        fontSize: 14,
        fontWeight: '500',
        marginTop: 4,
        marginBottom: 4,
        color: '#333', // adjust color if needed
    },


});

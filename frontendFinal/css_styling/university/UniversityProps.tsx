import {StyleSheet} from "react-native";


export const universityStyles = StyleSheet.create({
    universitySearchContainer: {
        width: '100%',
        justifyContent:'center',
        alignItems: 'center',
        position: 'relative',
        marginBottom: 20,
    },
    universityResultsContainer: {
        position: 'absolute',
        top: '55%', // Position below the search input
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
});

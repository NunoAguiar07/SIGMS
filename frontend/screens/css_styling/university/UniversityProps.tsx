import {StyleSheet} from "react-native";


export const universityStyles = StyleSheet.create({
    universitySearchContainer: {
        alignSelf: "stretch",
        justifyContent:'center',
        alignItems: 'center',
        position: 'relative',
        marginBottom: 20,
    },
    universityResultsContainer: {
        position: 'absolute',
        top: '100%',
        alignSelf: 'stretch',
        backgroundColor: '#fff',
        maxHeight: 200,
        borderWidth: 1,
        borderColor: '#651c24',
        borderRadius: 8,
        zIndex: 4, // Ensure it appears above other elements
        elevation: 3, // For Android shadow
        shadowColor: '#000', // For iOS shadow
        shadowOffset: { width: 0, height: 2 },
        shadowOpacity: 0.2,
        shadowRadius: 4,
    },
});

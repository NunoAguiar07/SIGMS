import {StyleSheet} from "react-native";


export const generateEntitiesStyles = StyleSheet.create({
    selectButton: {
        marginTop: 16,
        width: 175,
        height: 50,
        backgroundColor: '#651920',
        color: 'white',
        borderRadius: 16,
        justifyContent: 'center',
        alignItems: 'center',
    },
    createInput: {
        width: '100%',
        height: 50,
        borderWidth: 1,
        borderColor: '#651c24',
        borderRadius: 8,
        paddingHorizontal: 12,
        marginBottom: 16,
        marginTop: '10%',
    },
    formSection: {
        flex: 1,
        justifyContent: 'center',
        alignItems: 'center',
    },
    activeButton: {
        backgroundColor: '#651920',
    },
    list: {
        maxHeight: 200,
        borderWidth: 1,
        borderColor: '#ddd',
        borderRadius: 5,
        marginBottom: 15,
    },
    listItem: {
        padding: 12,
        borderBottomWidth: 1,
        borderBottomColor: '#eee',
    },
    selectedItem: {
        backgroundColor: '#e3f2fd',
    },
    listItemText: {
        fontSize: 16,
    },
    pickerContainer: {
        marginTop: 10,
    },
    picker: {
        backgroundColor: '#f9f9f9',
        borderRadius: 5,
        borderWidth: 1,
        borderColor: '#ddd',
    },
    loader: {
        marginVertical: 20,
    },
    emptyText: {
        textAlign: 'center',
        padding: 20,
        color: '#888',
    },
    placeholder: {
        flex: 1,
        justifyContent: 'center',
        alignItems: 'center',
    },
    placeholderText: {
        fontSize: 18,
        color: '#888',
    },
    footer: {
        marginTop: 'auto',
        paddingTop: 20,
    },
});
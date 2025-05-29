import {StyleSheet} from "react-native";


export const unassignedIssuesStyle = StyleSheet.create({
    roomColumn: {
        paddingHorizontal: 32,
        minWidth: 150,
        flex: 8,
    },
    descriptionColumn: {
        paddingHorizontal: 32,
        minWidth: 400,
        flex: 4,

    },
    actionsColumn: {
        paddingHorizontal: 32,
        flex: 2,
        justifyContent: 'center',
        alignItems: 'center'
    },
});
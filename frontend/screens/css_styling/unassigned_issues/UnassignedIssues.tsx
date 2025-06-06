import {StyleSheet} from "react-native";


export const unassignedIssuesStyle = StyleSheet.create({
    roomColumn: {
        paddingHorizontal: 32,
        minWidth: 200,
        flex: 8,
        padding: 8,
    },
    descriptionColumn: {
        paddingHorizontal: 32,
        minWidth: 600,
        flex: 4,
        padding: 8,
    },
    actionsColumn: {
        minWidth: 100,
        paddingHorizontal: 32,
        flex: 2,
        justifyContent: 'center',
        alignItems: 'center',
        padding: 8,
    },
});
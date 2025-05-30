import {StyleSheet} from "react-native";

export const assignedIssuesStyle = StyleSheet.create({
    roomColumn: {
        paddingHorizontal: 32,
        minWidth: 200,
        flex: 4,
        padding: 8,
    },
    descriptionColumn: {
        paddingHorizontal: 32,
        minWidth: 600,
        flex: 4,
        padding: 8,
    },
    actionsColumn: {paddingHorizontal: 32,

        flex: 4,
        minWidth: 100,
        flexDirection: 'row',
        justifyContent: 'space-around',
        padding: 8,
    },
});
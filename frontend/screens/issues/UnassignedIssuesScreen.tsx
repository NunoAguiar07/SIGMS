import {ActivityIndicator, FlatList, Text, TouchableOpacity, View} from "react-native";
import {commonStyles} from "../../css_styling/common/CommonProps";
import {unassignedIssuesStyle} from "../../css_styling/unassigned_issues/UnassignedIssues";
import {Ionicons} from "@expo/vector-icons";


// @ts-ignore
export const TechnicianUnassignedIssuesScreen = ({issues, onAssign, currentPage, onNext, onPrevious, isLoading, hasNext}) => {
    if (isLoading) {
        return (
            <View style={commonStyles.container}>
                <ActivityIndicator size="large" />
            </View>
        );
    }

    return (
        <View style={commonStyles.container}>
            <Text style={commonStyles.title}>Unassigned Room Issues</Text>

            <View style={commonStyles.tableContainer}>
                <View style={commonStyles.tableHeader}>
                    <View style={unassignedIssuesStyle.roomColumn}><Text style={commonStyles.headerText}>Room</Text></View>
                    <View style={unassignedIssuesStyle.descriptionColumn}><Text style={commonStyles.headerText}>Description</Text></View>
                    <View style={unassignedIssuesStyle.actionsColumn}><Text style={commonStyles.headerText}>Assign</Text></View>
                </View>

                <FlatList
                    data={issues}
                    keyExtractor={(item) => item.id.toString()}
                    renderItem={({ item }) => (
                        <View style={commonStyles.tableRow}>
                            <View style={unassignedIssuesStyle.roomColumn}>
                                <Text style={commonStyles.cellText} numberOfLines={1}>{item.room.name}</Text>
                            </View>
                            <View style={unassignedIssuesStyle.descriptionColumn}>
                                <Text style={commonStyles.cellText}>{item.description}</Text>
                            </View>
                            <View style={unassignedIssuesStyle.actionsColumn}>
                                <TouchableOpacity
                                    style={[commonStyles.actionButton, commonStyles.approveButton]}
                                    onPress={() => onAssign(item.id)}
                                >
                                    <Ionicons name="checkmark" size={16} color="white" />
                                </TouchableOpacity>
                            </View>
                        </View>
                    )}
                />
            </View>

            <View style={commonStyles.pagination}>
                <TouchableOpacity
                    style={[commonStyles.paginationButton, !currentPage && commonStyles.disabledButton]}
                    onPress={onPrevious}
                    disabled={!currentPage}
                >
                    <Text>Previous</Text>
                </TouchableOpacity>
                <Text style={commonStyles.pageNumber}>Page {currentPage + 1}</Text>
                <TouchableOpacity
                    style={[commonStyles.paginationButton, !hasNext && commonStyles.disabledButton]}
                    onPress={onNext}
                    disabled={!hasNext}
                >
                    <Text>Next</Text>
                </TouchableOpacity>
            </View>
        </View>
    );
};

export default TechnicianUnassignedIssuesScreen;
import {commonStyles} from "../../css_styling/common/CommonProps";
import {assignedIssuesStyle} from "../../css_styling/assigned_issues/AssignedIssuesProps";
import {View, Text, FlatList, TouchableOpacity, TextInput} from "react-native";
import {Ionicons} from "@expo/vector-icons";
import {AssignedIssuesScreenType} from "../types/AssignedIssuesScreenType";

export const AssignedIssuesScreen = ({
    issues,
    currentPage,
    onNext,
    onPrevious,
    onFix,
    onEdit,
    onUpdate,
    onUnassigned,
    onCancelEdit,
    editingId,
    editedDescription,
    setEditedDescription,
    hasNext
}: AssignedIssuesScreenType) => {    return (
        <View style={commonStyles.container}>
            <Text style={commonStyles.title}>Your Assigned Issues</Text>

            <View style={commonStyles.tableContainer}>
                <View style={commonStyles.tableHeader}>
                    <View style={assignedIssuesStyle.roomColumn}>
                        <Text style={commonStyles.headerText}>Room</Text>
                    </View>
                    <View style={assignedIssuesStyle.descriptionColumn}>
                        <Text style={commonStyles.headerText}>Description</Text>
                    </View>
                    <View style={assignedIssuesStyle.actionsColumn}>
                        <Text style={commonStyles.headerText}>Actions</Text>
                    </View>
                </View>

                <FlatList
                    data={issues}
                    keyExtractor={(item) => item.id.toString()}
                    renderItem={({ item }) => (
                        <View style={commonStyles.tableRow}>
                            <View style={assignedIssuesStyle.roomColumn}>
                                <Text style={commonStyles.cellText} numberOfLines={1}>
                                    {item.room.name}
                                </Text>
                            </View>
                            <View style={assignedIssuesStyle.descriptionColumn}>
                                {editingId === item.id ? (
                                    <TextInput
                                        style={[commonStyles.editableCell]}
                                        value={editedDescription}
                                        onChangeText={setEditedDescription}
                                        multiline
                                        autoFocus
                                        placeholder="Enter issue description"
                                        placeholderTextColor="#999"
                                    />
                                ) : (
                                    <Text style={commonStyles.cellText}>{item.description}</Text>
                                )}
                            </View>
                            <View style={assignedIssuesStyle.actionsColumn}>
                                {editingId === item.id ? (
                                    <>
                                        <TouchableOpacity
                                            style={[commonStyles.actionButton, commonStyles.saveButton]}
                                            onPress={() => onUpdate(item.id)}
                                        >
                                            <Ionicons name="save" size={16} color="white" />
                                        </TouchableOpacity>
                                        <TouchableOpacity
                                            style={[commonStyles.actionButton, commonStyles.cancelButton]}
                                            onPress={onCancelEdit}
                                        >
                                            <Ionicons name="close" size={16} color="white" />
                                        </TouchableOpacity>
                                    </>
                                ) : (
                                    <>
                                        <TouchableOpacity
                                            style={[commonStyles.actionButton, commonStyles.editButton]}
                                            onPress={() => onEdit(item)}
                                        >
                                            <Ionicons name="create-outline" size={16} color="white" />
                                        </TouchableOpacity>
                                        <TouchableOpacity
                                            style={[commonStyles.actionButton, commonStyles.approveButton]}
                                            onPress={() => onFix(item.id)}
                                        >
                                            <Ionicons name="checkmark-done" size={16} color="white" />
                                        </TouchableOpacity>
                                        <TouchableOpacity
                                            style={[commonStyles.actionButton, commonStyles.rejectButton]}
                                            onPress={() => onUnassigned(item.id)}
                                        >
                                            <Ionicons name="remove-circle-outline" size={16} color="white" />
                                        </TouchableOpacity>
                                    </>
                                )}
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
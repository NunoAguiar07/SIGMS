import {commonStyles} from "../css_styling/common/CommonProps";
import {assignedIssuesStyle} from "../css_styling/assigned_issues/AssignedIssuesProps";
import {View, Text, FlatList, TouchableOpacity, TextInput} from "react-native";
import {Ionicons} from "@expo/vector-icons";
import {AssignedIssuesScreenType} from "../types/AssignedIssuesScreenType";
import {IssueReportInterface} from "../../types/IssueReportInterface";
import {PaginationControls} from "./UnassignedIssuesScreen";

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
}: AssignedIssuesScreenType) => {
    return (
        <View style={commonStyles.container}>
            <Text style={commonStyles.title}>Your Assigned Issues</Text>

            <View style={commonStyles.tableContainer}>
                <AssignedIssuesTableHeader />

                <FlatList
                    data={issues}
                    keyExtractor={(item) => item.id.toString()}
                    renderItem={({ item }) => (
                        <AssignedIssueRow
                            item={item}
                            isEditing={editingId === item.id}
                            editedDescription={editedDescription}
                            setEditedDescription={setEditedDescription}
                            onEdit={onEdit}
                            onUpdate={onUpdate}
                            onCancelEdit={onCancelEdit}
                            onFix={onFix}
                            onUnassigned={onUnassigned}
                        />
                    )}
                />
            </View>

            <PaginationControls
                currentPage={currentPage}
                onNext={onNext}
                onPrevious={onPrevious}
                hasNext={hasNext}
            />
        </View>
    );
};

export const AssignedIssuesTableHeader = () => (
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
);

interface AssignedIssueRowType {
    item: IssueReportInterface;
    isEditing: boolean;
    editedDescription: string;
    setEditedDescription: (text: string) => void;
    onEdit: (issue: IssueReportInterface) => void;
    onUpdate: (id: number) => void;
    onCancelEdit: () => void;
    onFix: (id: number) => void;
    onUnassigned: (id: number) => void;
}

export const AssignedIssueRow = ({
    item,
    isEditing,
    editedDescription,
    setEditedDescription,
    onEdit,
    onUpdate,
    onCancelEdit,
    onFix,
    onUnassigned
}: AssignedIssueRowType) => (
    <View style={commonStyles.tableRow}>
        <View style={assignedIssuesStyle.roomColumn}>
            <Text style={commonStyles.cellText} numberOfLines={1}>{item.room.name}</Text>
        </View>

        <View style={assignedIssuesStyle.descriptionColumn}>
            {isEditing ? (
                <TextInput
                    style={commonStyles.editableCell}
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
            {isEditing ? (
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
);
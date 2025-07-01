import {commonStyles} from "../css_styling/common/CommonProps";
import {assignedIssuesStyle} from "../css_styling/assigned_issues/AssignedIssuesProps";
import {
    View,
    Text,
    FlatList,
    TouchableOpacity,
    TextInput,
    Animated,
} from "react-native";
import {Ionicons} from "@expo/vector-icons";
import {AssignedIssuesScreenType} from "../types/AssignedIssuesScreenType";
import {IssueReportInterface} from "../../types/IssueReportInterface";
import {PaginationControls} from "./UnassignedIssuesScreen";
import {mobileStyles} from "../css_styling/common/MobileProps";
import {isMobile} from "../../utils/DeviceType";
import {useSwipeAnimation} from "../../hooks/mobile/useSwipeAnimation";

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

            {isMobile ? (
                <MobileAssignedIssuesView
                    issues={issues}
                    currentPage={currentPage}
                    onNext={onNext}
                    onPrevious={onPrevious}
                    onFix={onFix}
                    onEdit={onEdit}
                    onUpdate={onUpdate}
                    onUnassigned={onUnassigned}
                    onCancelEdit={onCancelEdit}
                    editingId={editingId}
                    editedDescription={editedDescription}
                    setEditedDescription={setEditedDescription}
                    hasNext={hasNext}
                />
            ) : (
                <>
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
                </>
            )}
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

const MobileAssignedIssuesView = ({
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
    const { currentIndex, pan, panResponder } = useSwipeAnimation(
        issues.length,
        currentPage,
        hasNext,
        onNext,
        onPrevious
    );

    const currentIssue = issues[currentIndex];
    const isEditing = editingId === currentIssue?.id;

    if (!issues.length) {
        return (
            <View style={mobileStyles.container}>
                <Text style={mobileStyles.emptyText}>No assigned issues</Text>
            </View>
        );
    }

    return (
        <View style={mobileStyles.container}>
            <Animated.View
                style={[
                    mobileStyles.card,
                    {
                        transform: [{ translateX: pan.x }]
                    }
                ]}
                {...panResponder.panHandlers}
            >
                <View style={mobileStyles.cardContent}>
                    <Text style={mobileStyles.roomName}>{currentIssue.room.name}</Text>

                    <View style={mobileStyles.detailSection}>
                        <Text style={mobileStyles.detailLabel}>Description:</Text>
                        {isEditing ? (
                            <TextInput
                                style={mobileStyles.descriptionInput}
                                value={editedDescription}
                                onChangeText={setEditedDescription}
                                multiline
                                autoFocus
                                placeholder="Enter issue description"
                                placeholderTextColor="#999"
                            />
                        ) : (
                            <Text style={mobileStyles.detailValue}>{currentIssue.description}</Text>
                        )}
                    </View>

                    <View style={mobileStyles.detailSection}>
                        <Text style={mobileStyles.detailLabel}>Status:</Text>
                        <Text style={mobileStyles.detailValue}>Assigned</Text>
                    </View>

                    {isEditing ? (
                        <View style={mobileStyles.editActions}>
                            <TouchableOpacity
                                style={[mobileStyles.actionButton, mobileStyles.saveButton]}
                                onPress={() => onUpdate(currentIssue.id)}
                            >
                                <Ionicons name="save" size={20} color="white" />
                                <Text style={mobileStyles.buttonText}>Save</Text>
                            </TouchableOpacity>
                            <TouchableOpacity
                                style={[mobileStyles.actionButton, mobileStyles.cancelButton]}
                                onPress={onCancelEdit}
                            >
                                <Ionicons name="close" size={20} color="white" />
                                <Text style={mobileStyles.buttonText}>Cancel</Text>
                            </TouchableOpacity>
                        </View>
                    ) : (
                        <View style={mobileStyles.actions}>
                            <TouchableOpacity
                                style={[mobileStyles.actionButton, mobileStyles.editButton]}
                                onPress={() => onEdit(currentIssue)}
                            >
                                <Ionicons name="create-outline" size={20} color="white" />
                                <Text style={mobileStyles.buttonText}>Edit</Text>
                            </TouchableOpacity>
                            <TouchableOpacity
                                style={[mobileStyles.actionButton, mobileStyles.fixButton]}
                                onPress={() => onFix(currentIssue.id)}
                            >
                                <Ionicons name="checkmark-done" size={20} color="white" />
                                <Text style={mobileStyles.buttonText}>Mark Fixed</Text>
                            </TouchableOpacity>
                            <TouchableOpacity
                                style={[mobileStyles.actionButton, mobileStyles.unassignButton]}
                                onPress={() => onUnassigned(currentIssue.id)}
                            >
                                <Ionicons name="remove-circle-outline" size={20} color="white" />
                                <Text style={mobileStyles.buttonText}>Unassign</Text>
                            </TouchableOpacity>
                        </View>
                    )}
                </View>
            </Animated.View>

            <View style={mobileStyles.paginationDots}>
                {issues.map((_, index) => (
                    <View
                        key={index}
                        style={[
                            mobileStyles.dot,
                            index === currentIndex && mobileStyles.activeDot
                        ]}
                    />
                ))}
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
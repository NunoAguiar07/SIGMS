import {
    TextInput,
    Animated, ScrollView,
} from "react-native";
import {Ionicons} from "@expo/vector-icons";
import {AssignedIssuesScreenType} from "../types/AssignedIssuesScreenType";
import {IssueReportInterface} from "../../types/IssueReportInterface";
import {PaginationControls} from "./UnassignedIssuesScreen";
import {mobileStyles} from "../css_styling/common/MobileProps";
import {isMobile} from "../../utils/DeviceType";
import {useSwipeAnimation} from "../../hooks/mobile/useSwipeAnimation";
import {Card, CenteredContainer, RowContainer} from "../css_styling/common/NewContainers";
import {BodyText, Title} from "../css_styling/common/Typography";
import {
    CellText,
    EditableCell,
    HeaderText,
    TableColumn,
    TableContainer,
    TableHeader,
    TableRow
} from "../css_styling/common/Tables";
import {ActionButton} from "../css_styling/common/Buttons";

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
        <CenteredContainer flex={1} padding="md">
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
                    <AssignedIssuesTable
                        issues={issues}
                        editingId={editingId}
                        editedDescription={editedDescription}
                        setEditedDescription={setEditedDescription}
                        onEdit={onEdit}
                        onUpdate={onUpdate}
                        onCancelEdit={onCancelEdit}
                        onFix={onFix}
                        onUnassigned={onUnassigned}
                    />
                )}

                {!isMobile && (
                    <PaginationControls
                        currentPage={currentPage}
                        onNext={onNext}
                        onPrevious={onPrevious}
                        hasNext={hasNext}
                    />
                )}
        </CenteredContainer>
    );
};
interface AssignedIssuesTableType {
    issues: IssueReportInterface[];
    editingId: number | null;
    editedDescription: string;
    setEditedDescription: (text: string) => void;
    onEdit: (issue: IssueReportInterface) => void;
    onUpdate: (id: number) => void;
    onCancelEdit: () => void;
    onFix: (id: number) => void;
    onUnassigned: (id: number) => void;
}

export const AssignedIssuesTable = ({
    issues,
    editingId,
    editedDescription,
    setEditedDescription,
    onEdit,
    onUpdate,
    onCancelEdit,
    onFix,
    onUnassigned
}: AssignedIssuesTableType) => {
    return (
        <Card shadow="medium" alignItems={"center"} gap="md" width={"50%"} height={"80%"}>
            <Title>Your Assigned Issues</Title>
            <TableContainer>
                <AssignedIssuesTableHeader />
                <ScrollView showsVerticalScrollIndicator={false}>
                    {issues.map((item) => (
                        <AssignedIssueRow
                            key={item.id}
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
                    ))}
                </ScrollView>
            </TableContainer>
        </Card>
    );
};
export const AssignedIssuesTableHeader = () => (
    <TableHeader>
        <TableColumn width={4} first align="left">
            <HeaderText>Room</HeaderText>
        </TableColumn>
        <TableColumn width={6} align="left">
            <HeaderText>Description</HeaderText>
        </TableColumn>
        <TableColumn width={5} last align="center">
            <HeaderText>Actions</HeaderText>
        </TableColumn>
    </TableHeader>
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
    <TableRow>
        <TableColumn width={4} first align="left">
            <CellText numberOfLines={1}>{item.room.name}</CellText>
        </TableColumn>

        <TableColumn width={6} align="left">
            {isEditing ? (
                <EditableCell
                    value={editedDescription}
                    onChangeText={setEditedDescription}
                    multiline
                    autoFocus
                    placeholder="Enter issue description"
                    placeholderTextColor="#999"
                />
            ) : (
                <CellText>{item.description}</CellText>
            )}
        </TableColumn>

        <TableColumn width={5} last align="center">
                {isEditing ? (
                    <RowContainer>
                        <ActionButton onPress={() => onUpdate(item.id)}>
                            <Ionicons name="save" size={16} color="white" />
                        </ActionButton>
                        <ActionButton onPress={onCancelEdit}>
                            <Ionicons name="close" size={16} color="white" />
                        </ActionButton>
                    </RowContainer>
                ) : (
                    <RowContainer>
                        <ActionButton onPress={() => onEdit(item)}>
                            <Ionicons name="create-outline" size={16} color="white" />
                        </ActionButton>
                        <ActionButton onPress={() => onFix(item.id)}>
                            <Ionicons name="checkmark-done" size={16} color="white" />
                        </ActionButton>
                        <ActionButton onPress={() => onUnassigned(item.id)}>
                            <Ionicons name="remove-circle-outline" size={16} color="white" />
                        </ActionButton>
                    </RowContainer>
                )}
        </TableColumn>
    </TableRow>
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
            <CenteredContainer flex={1} justifyContent="center" alignItems="center">
                <BodyText>No assigned issues</BodyText>
            </CenteredContainer>
            // <View style={mobileStyles.container}>
            //     <Text style={mobileStyles.emptyText}>No assigned issues</Text>
            // </View>
        );
    }

    return (
        <CenteredContainer flex={1} padding="md">
            <Animated.View
                style={[
                    mobileStyles.card,
                    {
                        transform: [{ translateX: pan.x }]
                    }
                ]}
                {...panResponder.panHandlers}
            >
                <RowContainer>
                    <BodyText>Name:</BodyText>
                    <BodyText>{currentIssue.room.name}</BodyText>
                </RowContainer>
                <RowContainer>
                    <BodyText>Description:</BodyText>
                    {isEditing ? (
                        <TextInput
                            value={editedDescription}
                            onChangeText={setEditedDescription}
                            multiline
                            autoFocus
                            placeholder="Enter issue description"
                            placeholderTextColor="#999"
                        />
                    ) : (
                        <BodyText>{currentIssue.description}</BodyText>
                    )}
                </RowContainer>
                {isEditing ? (
                    <RowContainer justifyContent={"center"}>
                        <ActionButton onPress={() => onUpdate(currentIssue.id)}>
                            <Ionicons name="save" size={20} color="white" />
                        </ActionButton>
                        <ActionButton onPress={onCancelEdit}>
                            <Ionicons name="close" size={20} color="white" />
                        </ActionButton>
                    </RowContainer>
                ) : (
                    <RowContainer justifyContent={"center"}>
                        <ActionButton onPress={() => onEdit(currentIssue)}>
                            <Ionicons name="create-outline" size={20} color="white" />
                        </ActionButton>
                        <ActionButton onPress={() => onFix(currentIssue.id)}>
                            <Ionicons name="checkmark-done" size={20} color="white" />
                        </ActionButton>
                        <ActionButton onPress={() => onUnassigned(currentIssue.id)}>
                            <Ionicons name="remove-circle-outline" size={20} color="white" />
                        </ActionButton>
                    </RowContainer>
                )}
            </Animated.View>
        </CenteredContainer>
        // <View style={mobileStyles.container}>
        //     <Animated.View
        //         style={[
        //             mobileStyles.card,
        //             {
        //                 transform: [{ translateX: pan.x }]
        //             }
        //         ]}
        //         {...panResponder.panHandlers}
        //     >
        //         <View style={mobileStyles.cardContent}>
        //             <Text style={mobileStyles.roomName}>{currentIssue.room.name}</Text>
        //
        //             <View style={mobileStyles.detailSection}>
        //                 <Text style={mobileStyles.detailLabel}>Description:</Text>
        //                 {isEditing ? (
        //                     <TextInput
        //                         style={mobileStyles.descriptionInput}
        //                         value={editedDescription}
        //                         onChangeText={setEditedDescription}
        //                         multiline
        //                         autoFocus
        //                         placeholder="Enter issue description"
        //                         placeholderTextColor="#999"
        //                     />
        //                 ) : (
        //                     <Text style={mobileStyles.detailValue}>{currentIssue.description}</Text>
        //                 )}
        //             </View>
        //
        //             <View style={mobileStyles.detailSection}>
        //                 <Text style={mobileStyles.detailLabel}>Status:</Text>
        //                 <Text style={mobileStyles.detailValue}>Assigned</Text>
        //             </View>
        //
        //             {isEditing ? (
        //                 <View style={mobileStyles.editActions}>
        //                     <TouchableOpacity
        //                         style={[mobileStyles.actionButton, mobileStyles.saveButton]}
        //                         onPress={() => onUpdate(currentIssue.id)}
        //                     >
        //                         <Ionicons name="save" size={20} color="white" />
        //                         <Text style={mobileStyles.buttonText}>Save</Text>
        //                     </TouchableOpacity>
        //                     <TouchableOpacity
        //                         style={[mobileStyles.actionButton, mobileStyles.cancelButton]}
        //                         onPress={onCancelEdit}
        //                     >
        //                         <Ionicons name="close" size={20} color="white" />
        //                         <Text style={mobileStyles.buttonText}>Cancel</Text>
        //                     </TouchableOpacity>
        //                 </View>
        //             ) : (
        //                 <View style={mobileStyles.actions}>
        //                     <TouchableOpacity
        //                         style={[mobileStyles.actionButton, mobileStyles.editButton]}
        //                         onPress={() => onEdit(currentIssue)}
        //                     >
        //                         <Ionicons name="create-outline" size={20} color="white" />
        //                         <Text style={mobileStyles.buttonText}>Edit</Text>
        //                     </TouchableOpacity>
        //                     <TouchableOpacity
        //                         style={[mobileStyles.actionButton, mobileStyles.fixButton]}
        //                         onPress={() => onFix(currentIssue.id)}
        //                     >
        //                         <Ionicons name="checkmark-done" size={20} color="white" />
        //                         <Text style={mobileStyles.buttonText}>Mark Fixed</Text>
        //                     </TouchableOpacity>
        //                     <TouchableOpacity
        //                         style={[mobileStyles.actionButton, mobileStyles.unassignButton]}
        //                         onPress={() => onUnassigned(currentIssue.id)}
        //                     >
        //                         <Ionicons name="remove-circle-outline" size={20} color="white" />
        //                         <Text style={mobileStyles.buttonText}>Unassign</Text>
        //                     </TouchableOpacity>
        //                 </View>
        //             )}
        //         </View>
        //     </Animated.View>
        //
        //     <View style={mobileStyles.paginationDots}>
        //         {issues.map((_, index) => (
        //             <View
        //                 key={index}
        //                 style={[
        //                     mobileStyles.dot,
        //                     index === currentIndex && mobileStyles.activeDot
        //                 ]}
        //             />
        //         ))}
        //     </View>
        //
        //     <PaginationControls
        //         currentPage={currentPage}
        //         onNext={onNext}
        //         onPrevious={onPrevious}
        //         hasNext={hasNext}
        //     />
        // </View>
    );
};
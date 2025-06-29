import {Animated, FlatList, Text, TouchableOpacity, View} from "react-native";
import {commonStyles} from "../css_styling/common/CommonProps";
import {unassignedIssuesStyle} from "../css_styling/unassigned_issues/UnassignedIssues";
import {Ionicons} from "@expo/vector-icons";
import {UnassignedIssuesScreenType} from "../types/UnassignedIssuesScreenType";
import {IssueReportInterface} from "../../types/IssueReportInterface";
import {mobileStyles} from "../css_styling/common/MobileProps";
import {isMobile} from "../../utils/DeviceType";
import {useSwipeAnimation} from "../../hooks/mobile/useSwipeAnimation";

export const TechnicianUnassignedIssuesScreen = ({
    issues,
    onAssign,
    currentPage,
    onNext,
    onPrevious,
    hasNext
}: UnassignedIssuesScreenType) => {

    return (
        <View style={commonStyles.container}>
            <Text style={commonStyles.title}>Unassigned Room Issues</Text>

            {isMobile ? (
                <MobileIssueView
                    issues={issues}
                    onAssign={onAssign}
                    currentPage={currentPage}
                    onNext={onNext}
                    onPrevious={onPrevious}
                    hasNext={hasNext}
                />
            ) : (
                <>
                    <View style={commonStyles.tableContainer}>
                        <TableHeader />
                        <FlatList
                            data={issues}
                            keyExtractor={(item) => item.id.toString()}
                            renderItem={({ item }) => (
                                <IssueRow item={item} onAssign={onAssign} />
                            )}
                        />
                    </View>
                    <PaginationControls
                        currentPage={currentPage}
                        hasNext={hasNext}
                        onNext={onNext}
                        onPrevious={onPrevious}
                    />
                </>
            )}
        </View>
    );
};

export default TechnicianUnassignedIssuesScreen;

export const TableHeader = () => (
    <View style={commonStyles.tableHeader}>
        <View style={unassignedIssuesStyle.roomColumn}>
            <Text style={commonStyles.headerText}>Room</Text>
        </View>
        <View style={unassignedIssuesStyle.descriptionColumn}>
            <Text style={commonStyles.headerText}>Description</Text>
        </View>
        <View style={unassignedIssuesStyle.actionsColumn}>
            <Text style={commonStyles.headerText}>Assign</Text>
        </View>
    </View>
);

interface IssueRowType {
    item: IssueReportInterface;
    onAssign: (issueId: number) => void;
}

export const IssueRow = ({ item, onAssign }: IssueRowType) => (
    <View style={commonStyles.tableRow}>
        <View style={unassignedIssuesStyle.roomColumn}>
            <Text style={commonStyles.cellText} numberOfLines={1}>
                {item.room.name}
            </Text>
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
);

interface PaginationControlsType {
    currentPage: number;
    hasNext: boolean;
    onNext: () => void;
    onPrevious: () => void;
}

export const PaginationControls = ({
    currentPage,
    hasNext,
    onNext,
    onPrevious
}: PaginationControlsType) => (
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
);

const MobileIssueView = ({
    issues,
    onAssign,
    currentPage,
    onNext,
    onPrevious,
    hasNext
}: UnassignedIssuesScreenType) => {
    const { currentIndex, pan, panResponder } = useSwipeAnimation(
        issues.length,
        currentPage,
        hasNext,
        onNext,
        onPrevious
    );

    const currentIssue = issues[currentIndex];

    if (!issues.length) {
        return (
            <View style={mobileStyles.container}>
                <Text style={mobileStyles.emptyText}>No unassigned issues</Text>
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
                        <Text style={mobileStyles.detailValue}>{currentIssue.description}</Text>
                    </View>

                    <TouchableOpacity
                        style={mobileStyles.assignButton}
                        onPress={() => onAssign(currentIssue.id)}
                    >
                        <Ionicons name="checkmark" size={20} color="white" />
                        <Text style={mobileStyles.buttonText}>Assign to Me</Text>
                    </TouchableOpacity>
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
                hasNext={hasNext}
                onNext={onNext}
                onPrevious={onPrevious}
            />
        </View>
    );
};
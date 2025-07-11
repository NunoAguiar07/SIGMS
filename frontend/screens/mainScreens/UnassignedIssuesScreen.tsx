import {Animated, ScrollView, Text, View} from "react-native";
import {commonStyles} from "../css_styling/common/CommonProps";
import {Ionicons} from "@expo/vector-icons";
import {UnassignedIssuesScreenType} from "../types/UnassignedIssuesScreenType";
import {IssueReportInterface} from "../../types/IssueReportInterface";
import {mobileStyles} from "../css_styling/common/MobileProps";
import {isMobile} from "../../utils/DeviceType";
import {useSwipeAnimation} from "../../hooks/mobile/useSwipeAnimation";
import {Card, CenteredContainer, PaginationContainer, RowContainer} from "../css_styling/common/NewContainers";
import {ActionButton, Button, ButtonText} from "../css_styling/common/Buttons";
import {BodyText, Title} from "../css_styling/common/Typography";
import {CellText, HeaderText, TableColumn, TableContainer, TableHeader, TableRow} from "../css_styling/common/Tables";

export const TechnicianUnassignedIssuesScreen = ({
    issues,
    onAssign,
    currentPage,
    onNext,
    onPrevious,
    hasNext
}: UnassignedIssuesScreenType) => {

    return (
        <CenteredContainer flex={1} padding="md">

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
                    <UnassignedIssuesTable
                        issues={issues}
                        onAssign={onAssign}
                    />
                )}

                {!isMobile && (
                    <PaginationControls
                        currentPage={currentPage}
                        hasNext={hasNext}
                        onNext={onNext}
                        onPrevious={onPrevious}
                    />
                )}
        </CenteredContainer>
    );

        // <View style={commonStyles.container}>
        //     <Text style={commonStyles.title}>Unassigned Room Issues</Text>
        //
        //     {isMobile ? (
        //         <MobileIssueView
        //             issues={issues}
        //             onAssign={onAssign}
        //             currentPage={currentPage}
        //             onNext={onNext}
        //             onPrevious={onPrevious}
        //             hasNext={hasNext}
        //         />
        //     ) : (
        //         <>
        //             <View style={commonStyles.tableContainer}>
        //                 <TableHeader />
        //                 <FlatList
        //                     data={issues}
        //                     keyExtractor={(item) => item.id.toString()}
        //                     renderItem={({ item }) => (
        //                         <IssueRow item={item} onAssign={onAssign} />
        //                     )}
        //                 />
        //             </View>
        //             <PaginationControls
        //                 currentPage={currentPage}
        //                 hasNext={hasNext}
        //                 onNext={onNext}
        //                 onPrevious={onPrevious}
        //             />
        //         </>
        //     )}
        // </View>
};

interface UnassignedIssuesTableType {
    issues: IssueReportInterface[];
    onAssign: (issueId: number) => void;
}

export const UnassignedIssuesTable = ({ issues, onAssign }: UnassignedIssuesTableType) => {
    return (
        <Card shadow="medium" alignItems={"center"} gap="md" width={"50%"} height={"80%"}>
            <Title>Unassigned Room Issues</Title>
            <TableContainer>
                <UnassignedIssuesTableHeader />
                <ScrollView showsVerticalScrollIndicator={false}>
                    {issues.map((item) => (
                        <UnassignedIssueRow
                            key={item.id}
                            item={item}
                            onAssign={onAssign}
                        />
                    ))}
                </ScrollView>
            </TableContainer>
        </Card>
    );
};

export default TechnicianUnassignedIssuesScreen;
export const UnassignedIssuesTableHeader = () => (
    <TableHeader>
        <TableColumn width={4} first align="left">
            <HeaderText>Room</HeaderText>
        </TableColumn>
        <TableColumn width={10} align="left">
            <HeaderText>Description</HeaderText>
        </TableColumn>
        <TableColumn width={2} last align="center">
            <HeaderText>Assign</HeaderText>
        </TableColumn>
    </TableHeader>
);

interface IssueRowType {
    item: IssueReportInterface;
    onAssign: (issueId: number) => void;
}

export const UnassignedIssueRow = ({ item, onAssign }: IssueRowType) => (
    <TableRow>
        <TableColumn width={4} first align="left">
            <CellText numberOfLines={1}>{item.room.name}</CellText>
        </TableColumn>
        <TableColumn width={10} align="left">
            <CellText>{item.description}</CellText>
        </TableColumn>
        <TableColumn width={2} last align="center">
            <ActionButton onPress={() => onAssign(item.id)}>
                <Ionicons name="checkmark" size={16} color="white" />
            </ActionButton>
        </TableColumn>
    </TableRow>
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
    <PaginationContainer>
        <Button
            onPress={onPrevious}
            disabled={!currentPage}
            size={"small"}
            style={[commonStyles.paginationButton, !currentPage && commonStyles.disabledButton]}
        >
            <ButtonText>Previous</ButtonText>
        </Button>
        <BodyText>Page {currentPage + 1}</BodyText>
        <Button
            onPress={onNext}
            disabled={!hasNext}
            size={"small"}
            style={[commonStyles.paginationButton, !hasNext && commonStyles.disabledButton]}
        >
            <ButtonText>Next</ButtonText>
        </Button>
    </PaginationContainer>
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
                    <BodyText>{currentIssue.description}</BodyText>
                </RowContainer>
                <RowContainer>
                    <ActionButton
                        onPress={() => onAssign(currentIssue.id)}
                    >
                        <Ionicons name="checkmark" size={20} color="white" />
                    </ActionButton>
                </RowContainer>
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
        //                 <Text style={mobileStyles.detailValue}>{currentIssue.description}</Text>
        //             </View>
        //
        //             <TouchableOpacity
        //                 style={mobileStyles.assignButton}
        //                 onPress={() => onAssign(currentIssue.id)}
        //             >
        //                 <Ionicons name="checkmark" size={20} color="white" />
        //                 <Text style={mobileStyles.buttonText}>Assign to Me</Text>
        //             </TouchableOpacity>
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
        //         hasNext={hasNext}
        //         onNext={onNext}
        //         onPrevious={onPrevious}
        //     />
        // </View>
    );
};
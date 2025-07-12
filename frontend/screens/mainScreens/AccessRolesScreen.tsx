import {Animated, ScrollView} from "react-native";
import {Ionicons} from "@expo/vector-icons";
import {mobileStyles} from "../css_styling/common/MobileProps";
import {AccessRoleScreenType} from "../types/AccessRoleScreenType";
import {PaginationControls} from "./UnassignedIssuesScreen";
import {AccessRoleInterface} from "../../types/AccessRoleInterface";
import {isMobile} from "../../utils/DeviceType";
import {useSwipeAnimation} from "../../hooks/mobile/useSwipeAnimation";
import {Card, CenteredContainer, RowContainer} from "../css_styling/common/NewContainers";
import {BodyText, Title} from "../css_styling/common/Typography";
import {CellText, HeaderText, TableColumn, TableContainer, TableHeader, TableRow} from "../css_styling/common/Tables";
import {ActionButton} from "../css_styling/common/Buttons";

export const AccessRolesScreen = ({
    approvals,
    onApprove,
    onReject,
    onNext,
    onPrevious,
    currentPage,
    hasNext
}: AccessRoleScreenType) => {

    return (
        <CenteredContainer flex={1} padding="md">
            {isMobile ? (
                <AccessRoleMobileView
                    approvals={approvals}
                    onApprove={onApprove}
                    onReject={onReject}
                    currentPage={currentPage}
                    hasNext={hasNext}
                    onNext={onNext}
                    onPrevious={onPrevious}
                />
            ) : (
                <AccessRoleTable
                    approvals={approvals}
                    onApprove={onApprove}
                    onReject={onReject}
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
};

interface AccessRoleTableType {
    approvals: AccessRoleInterface[];
    onApprove: (id: number) => void;
    onReject: (id: number) => void;
}

export const AccessRoleTable = ({ approvals, onApprove, onReject }: AccessRoleTableType) => {
    return (
        <Card shadow="medium" alignItems={"center"} gap="md" width={"90%"} height={"80%"}>
            <Title>Pending Approvals</Title>
            <TableContainer>
                <AccessRoleTableHeader />
                <ScrollView showsVerticalScrollIndicator={false}>
                    {approvals.map((item) => (
                        <AccessRoleRow
                            key={item.id}
                            item={item}
                            onApprove={onApprove}
                            onReject={onReject}
                        />
                    ))}
                </ScrollView>
            </TableContainer>
        </Card>
    );
};

export const AccessRoleTableHeader = () => (
    <TableHeader>
        <TableColumn width={4} first align="left">
            <HeaderText>Name</HeaderText>
        </TableColumn>
        <TableColumn width={5} align="left">
            <HeaderText>Email</HeaderText>
        </TableColumn>
        <TableColumn width={3} align="center">
            <HeaderText>Role</HeaderText>
        </TableColumn>
        <TableColumn width={3} align="center">
            <HeaderText>Date</HeaderText>
        </TableColumn>
        <TableColumn width={3} last align="right">
            <HeaderText>Actions</HeaderText>
        </TableColumn>
    </TableHeader>
);

interface AccessRoleRowType {
    item: AccessRoleInterface;
    onApprove: (id: number) => void;
    onReject: (id: number) => void;
}

export const AccessRoleRow = ({ item, onApprove, onReject }: AccessRoleRowType) => (
    <TableRow>
        <TableColumn width={4} first align="left">
            <CellText numberOfLines={1}>{item.user.username}</CellText>
        </TableColumn>
        <TableColumn width={5} align="left">
            <CellText numberOfLines={1}>{item.user.email}</CellText>
        </TableColumn>
        <TableColumn width={3} align="center">
            <CellText>{item.requestedRole}</CellText>
        </TableColumn>
        <TableColumn width={3} align="center">
            <CellText>{new Date(item.createdAt).toLocaleDateString()}</CellText>
        </TableColumn>
        <TableColumn width={3} last align="right">
            <RowContainer>
                <ActionButton onPress={() => onApprove(item.id)}>
                    <Ionicons name="checkmark" size={16} color="white" />
                </ActionButton>
                <ActionButton onPress={() => onReject(item.id)}>
                    <Ionicons name="close" size={16} color="white" />
                </ActionButton>
        </RowContainer>
        </TableColumn>
    </TableRow>
);

export const AccessRoleMobileView = ({
    approvals,
    onApprove,
    onReject,
    currentPage,
    hasNext,
    onNext,
    onPrevious
}: AccessRoleScreenType) => {

    const { currentIndex, pan, panResponder } = useSwipeAnimation(
        approvals.length,
        currentPage,
        hasNext,
        onNext,
        onPrevious
    );

    const currentUser = approvals[currentIndex];

    if (!currentUser) {
        return (
            <CenteredContainer flex={1} padding="md">
                <BodyText>No pending approvals</BodyText>
            </CenteredContainer>
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
                    <BodyText>{currentUser.user.username}</BodyText>
                </RowContainer>
                <RowContainer>
                    <BodyText>Email:</BodyText>
                    <BodyText>{currentUser.user.email}</BodyText>
                </RowContainer>
                <RowContainer>
                    <BodyText>Requested Role:</BodyText>
                    <BodyText>{currentUser.requestedRole}</BodyText>
                </RowContainer>
                <RowContainer>
                    <BodyText>Request Date:</BodyText>
                    <BodyText>{new Date(currentUser.createdAt).toLocaleDateString()}</BodyText>
                </RowContainer>
                <RowContainer justifyContent={"center"}>
                    <ActionButton
                        onPress={() => onApprove(currentUser.id)}
                        variant="success"
                    >
                        <Ionicons name="checkmark" size={20} color="white" />
                    </ActionButton>
                    <ActionButton
                        onPress={() => onReject(currentUser.id)}
                        variant="error"
                    >
                        <Ionicons name="close" size={20} color="white" />
                    </ActionButton>
                </RowContainer>
            </Animated.View>
        </CenteredContainer>
    );
};
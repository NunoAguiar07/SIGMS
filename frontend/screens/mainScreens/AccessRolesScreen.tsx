import {Animated, FlatList, Text, TouchableOpacity, View} from "react-native";
import {Ionicons} from "@expo/vector-icons";
import {commonStyles} from "../css_styling/common/CommonProps";
import {accessRoleStyles} from "../css_styling/access_role/AccessRoleProps";
import {mobileStyles} from "../css_styling/common/MobileProps";
import {AccessRoleScreenType} from "../types/AccessRoleScreenType";
import {PaginationControls} from "./UnassignedIssuesScreen";
import {AccessRoleInterface} from "../../types/AccessRoleInterface";
import {isMobile} from "../../utils/DeviceType";
import {useSwipeAnimation} from "../../hooks/mobile/useSwipeAnimation";

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
        <View style={[
            commonStyles.container,
            isMobile && { padding: 0 }
        ]}>
            <Text style={[commonStyles.title, isMobile && { marginBottom: 16 }]}>
                Pending Approvals
            </Text>

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
        </View>
    );
};

interface AccessRoleTableType {
    approvals: AccessRoleInterface[];
    onApprove: (id: number) => void;
    onReject: (id: number) => void;
}

export const AccessRoleTable = ({ approvals, onApprove, onReject }: AccessRoleTableType) => {
    return (
        <View style={commonStyles.tableContainer}>
            <AccessRoleTableHeader />
            <FlatList
                data={approvals}
                keyExtractor={(item) => item.id.toString()}
                renderItem={({ item }) => (
                    <AccessRoleRow item={item} onApprove={onApprove} onReject={onReject} />
                )}
            />
        </View>
    );
};

export const AccessRoleTableHeader = () => (
    <View style={commonStyles.tableHeader}>
        <View style={accessRoleStyles.nameColumn}>
            <Text style={commonStyles.headerText}>Name</Text>
        </View>
        <View style={accessRoleStyles.emailColumn}>
            <Text style={commonStyles.headerText}>Email</Text>
        </View>
        <View style={accessRoleStyles.roleColumn}>
            <Text style={commonStyles.headerText}>Role</Text>
        </View>
        <View style={accessRoleStyles.dateColumn}>
            <Text style={commonStyles.headerText}>Date</Text>
        </View>
        <View style={accessRoleStyles.actionsColumn}>
            <Text style={commonStyles.headerText}>Actions</Text>
        </View>
    </View>
);

interface AccessRoleRowType {
    item: AccessRoleInterface;
    onApprove: (id: number) => void;
    onReject: (id: number) => void;
}

export const AccessRoleRow = ({ item, onApprove, onReject }: AccessRoleRowType) => (
    <View style={commonStyles.tableRow}>
        <View style={accessRoleStyles.nameColumn}>
            <Text style={commonStyles.cellText} numberOfLines={1}>{item.user.name}</Text>
        </View>
        <View style={accessRoleStyles.emailColumn}>
            <Text style={commonStyles.cellText} numberOfLines={1}>{item.user.email}</Text>
        </View>
        <View style={accessRoleStyles.roleColumn}>
            <Text style={commonStyles.cellText}>{item.requestedRole}</Text>
        </View>
        <View style={accessRoleStyles.dateColumn}>
            <Text style={commonStyles.cellText}>
                {new Date(item.createdAt).toLocaleDateString()}
            </Text>
        </View>
        <View style={accessRoleStyles.actionsColumn}>
            <View style={[accessRoleStyles.actionsCell, accessRoleStyles.actionsColumn]}>
                <TouchableOpacity
                    style={[commonStyles.actionButton, commonStyles.approveButton]}
                    onPress={() => onApprove(item.id)}
                >
                    <Ionicons name="checkmark" size={16} color="white" />
                </TouchableOpacity>
                <TouchableOpacity
                    style={[commonStyles.actionButton, commonStyles.rejectButton]}
                    onPress={() => onReject(item.id)}
                >
                    <Ionicons name="close" size={16} color="white" />
                </TouchableOpacity>
            </View>
        </View>
    </View>
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
            <View style={mobileStyles.container}>
                <Text style={mobileStyles.emptyText}>No pending approvals</Text>
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
                    <Text style={mobileStyles.name}>{currentUser.user.name}</Text>

                    <View style={mobileStyles.detailRow}>
                        <Text style={mobileStyles.detailLabel}>Email:</Text>
                        <Text style={mobileStyles.detailValue}>{currentUser.user.email}</Text>
                    </View>

                    <View style={mobileStyles.detailRow}>
                        <Text style={mobileStyles.detailLabel}>Requested Role:</Text>
                        <Text style={mobileStyles.detailValue}>{currentUser.requestedRole}</Text>
                    </View>

                    <View style={mobileStyles.detailRow}>
                        <Text style={mobileStyles.detailLabel}>Request Date:</Text>
                        <Text style={mobileStyles.detailValue}>
                            {new Date(currentUser.createdAt).toLocaleDateString()}
                        </Text>
                    </View>

                    <View style={mobileStyles.actionButtons}>
                        <TouchableOpacity
                            style={[mobileStyles.actionButton, mobileStyles.approveButton]}
                            onPress={() => onApprove(currentUser.id)}
                        >
                            <Ionicons name="checkmark" size={20} color="white" />
                            <Text style={mobileStyles.buttonText}>Approve</Text>
                        </TouchableOpacity>
                        <TouchableOpacity
                            style={[mobileStyles.actionButton, mobileStyles.rejectButton]}
                            onPress={() => onReject(currentUser.id)}
                        >
                            <Ionicons name="close" size={20} color="white" />
                            <Text style={mobileStyles.buttonText}>Reject</Text>
                        </TouchableOpacity>
                    </View>
                </View>
            </Animated.View>

            <View style={mobileStyles.paginationDots}>
                {approvals.map((_, index) => (
                    <View
                        key={index}
                        style={[
                            mobileStyles.dot,
                            index === currentIndex && mobileStyles.activeDot
                        ]}
                    />
                ))}
            </View>
        </View>
    );
};
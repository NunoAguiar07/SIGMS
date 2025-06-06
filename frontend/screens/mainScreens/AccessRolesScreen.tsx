import {FlatList, Text, TouchableOpacity, View} from "react-native";
import {Ionicons} from "@expo/vector-icons";
import {commonStyles} from "../css_styling/common/CommonProps";
import {accessRoleStyles} from "../css_styling/access_role/AccessRoleProps";
import {AccessRoleScreenType} from "../types/AccessRoleScreenType";
import {PaginationControls} from "./UnassignedIssuesScreen";
import {AccessRoleInterface} from "../../types/AccessRoleInterface";


export const AccessRolesScreen = ({
    approvals,
    onApprove,
    onReject,
    onNext,
    onPrevious,
    currentPage,
    hasNext
} : AccessRoleScreenType) => {
    return (
        <View style={commonStyles.container}>
            <Text style={commonStyles.title}>Pending Approvals</Text>
            <AccessRoleTable approvals={approvals} onApprove={onApprove} onReject={onReject} />
            <PaginationControls
                currentPage={currentPage}
                hasNext={hasNext}
                onNext={onNext}
                onPrevious={onPrevious}
            />
        </View>
    );
}

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
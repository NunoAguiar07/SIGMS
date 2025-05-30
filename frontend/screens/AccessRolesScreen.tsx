import {ActivityIndicator, FlatList, Text, TouchableOpacity, View} from "react-native";
import {Ionicons} from "@expo/vector-icons";
import {commonStyles} from "../css_styling/common/CommonProps";
import {accessRoleStyles} from "../css_styling/access_role/AccessRoleProps";

// @ts-ignore
export const AccessRolesScreen = ({approvals, onApprove, onReject, onNext, onPrevious, currentPage, hasNext, isLoading}) => {
    if (isLoading) {
        return (
            <View style={commonStyles.container}>
                <ActivityIndicator size="large" />
            </View>
        );
    }
    return (
        <View style={commonStyles.container}>

            <Text style={commonStyles.title}>Pending Approvals</Text>

            {/* Table Container */}
            <View style={commonStyles.tableContainer}>
                {/* Table Header */}
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
                {/* Table Body */}
                <FlatList
                    data={approvals}
                    keyExtractor={(item) => item.id.toString()}
                    renderItem={({ item }) => (
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
                                <Text style={commonStyles.cellText}>{new Date(item.createdAt).toLocaleDateString()}</Text>
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
                    )}
                />
            </View>

            {/* Pagination */}
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
}
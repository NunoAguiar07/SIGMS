import {
    View,
    Text,
    TextInput,
    FlatList,
    TouchableOpacity
} from 'react-native';
import {commonStyles} from "../../css_styling/common/CommonProps";
import {createReportStyle} from "../../css_styling/createReport/CreateReportStyle";


export const CreateReportScreen = ({
                                       rooms,
                                       selectedRoom,
                                       reportText,
                                       searchQuery,
                                       onSearchChange,
                                       onRoomSelect,
                                       onReportTextChange,
                                       onSubmitReport
}) => {
    return (
        <View style={createReportStyle.joinClassContainer}>
            <View style={commonStyles.leftColumn}>
                <TextInput
                    style={commonStyles.searchSubjectInput}
                    placeholder="Search rooms..."
                    value={searchQuery}
                    onChangeText={onSearchChange}
                />
                <FlatList
                    data={rooms}
                    keyExtractor={(item) => item.id.toString()}
                    renderItem={({ item }) => (
                        <TouchableOpacity
                            style={commonStyles.itemSearch}
                            onPress={() => onRoomSelect(item)}
                        >
                            <Text style={commonStyles.itemText}>{item.name}</Text>
                        </TouchableOpacity>
                    )}
                    ListEmptyComponent={
                        <Text style={commonStyles.emptyText}>No rooms found</Text>
                    }
                />
            </View>

            <View style={commonStyles.rightColumn}>
                {selectedRoom && (
                    <View style={createReportStyle.classesSection}>
                        <Text style={commonStyles.sectionTitle}>
                            Report for {selectedRoom.name}
                        </Text>
                        <TextInput
                            style={createReportStyle.reportInput}
                            placeholder="Describe the issue or note here..."
                            multiline
                            value={reportText}
                            onChangeText={onReportTextChange}
                        />
                        <TouchableOpacity
                            style={createReportStyle.joinClassButton}
                            onPress={() => onSubmitReport(selectedRoom.id, reportText)}
                        >
                            <Text style={createReportStyle.joinButtonText}>Submit Report</Text>
                        </TouchableOpacity>
                    </View>
                )}
            </View>
        </View>
    );
};

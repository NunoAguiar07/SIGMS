import {
    View,
    Text,
    TextInput,
    FlatList,
    TouchableOpacity
} from 'react-native';
import {commonStyles} from "../css_styling/common/CommonProps";
import {createReportStyle} from "../css_styling/createReport/CreateReportStyle";
import {CreateReportScreenType} from "../types/CreateReportScreenType";
import {RoomInterface} from "../../types/RoomInterface";
import {isMobile} from "../../utils/DeviceType";


export const CreateReportScreen = ({
    rooms,
    issues,
    selectedRoom,
    reportText,
    searchQuery,
    onSearchChange,
    onRoomSelect,
    onReportTextChange,
    onSubmitReport,
} : CreateReportScreenType) => {
    if (!isMobile) {
        return (
            <View style={createReportStyle.joinClassContainer}>
                <View style={commonStyles.leftColumn}>
                    <RoomSearchList
                        rooms={rooms}
                        searchQuery={searchQuery}
                        onSearchChange={onSearchChange}
                        onRoomSelect={onRoomSelect}
                    />
                </View>

            <View style={commonStyles.rightColumn}>
                {selectedRoom && (
                    <>
                        {/* Issues List for Selected Room */}
                        <View style={createReportStyle.issuesListContainer}>
                            <Text style={commonStyles.sectionTitle}>
                                Previous Reports for {selectedRoom.name}
                            </Text>

                            {issues.length > 0 ? (
                                issues.map((issue, index) => (
                                    <View key={issue.id || index} style={createReportStyle.issueItem}>
                                        <Text style={createReportStyle.issueText}>- {issue.description}</Text>
                                    </View>
                                ))
                            ) : (
                                <Text style={commonStyles.emptyText}>
                                    No issues reported for this room.
                                </Text>
                            )}
                        </View>

                        {/* Report Form */}
                        <RoomReportForm
                            selectedRoom={selectedRoom}
                            reportText={reportText}
                            onReportTextChange={onReportTextChange}
                            onSubmitReport={onSubmitReport}
                        />
                    </>
                )}
            </View>
        </View>
    );
    } else {
        return (
            <View style={createReportStyle.joinClassContainer}>
                {!selectedRoom ? (
                    <View style={commonStyles.leftColumn}>
                        <RoomSearchList
                            rooms={rooms}
                            searchQuery={searchQuery}
                            onSearchChange={onSearchChange}
                            onRoomSelect={onRoomSelect}
                        />
                    </View>
                ) : (
                    <View style={commonStyles.rightColumn}>
                        <RoomReportForm
                            selectedRoom={selectedRoom}
                            reportText={reportText}
                            onReportTextChange={onReportTextChange}
                            onSubmitReport={onSubmitReport}
                        />
                    </View>
                )}
            </View>
        );
    }
};

interface RoomSearchListType {
    rooms: RoomInterface[];
    searchQuery: string;
    onSearchChange: (text: string) => void;
    onRoomSelect: (room: RoomInterface) => void;
}

export const RoomSearchList = ({
    rooms,
    searchQuery,
    onSearchChange,
    onRoomSelect
}: RoomSearchListType) => (
    <View>
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
            ListEmptyComponent={<Text style={commonStyles.emptyText}>No rooms found</Text>}
        />
    </View>
);


interface RoomReportFormType {
    selectedRoom: RoomInterface;
    reportText: string;
    onReportTextChange: (text: string) => void;
    onSubmitReport: (roomId: number, description: string) => void;
}

export const RoomReportForm = ({
    selectedRoom,
    reportText,
    onReportTextChange,
    onSubmitReport
}: RoomReportFormType) => (
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
);
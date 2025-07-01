import {RoomReportsScreenType} from "../types/RoomReportScreenType";
import {ScrollView, View, Text} from "react-native";
import {createReportStyle} from "../css_styling/createReport/CreateReportStyle";
import {commonStyles} from "../css_styling/common/CommonProps";


export const RoomReportsScreen = ({ reports, room }: RoomReportsScreenType) => {
    return (
        <View style={createReportStyle.joinClassContainer}>
            <View style={commonStyles.rightColumn}>
                <View style={createReportStyle.issuesListContainer}>
                    <Text style={[commonStyles.sectionTitle, { fontSize: 24, marginBottom: 16 }]}>
                        Previous Reports for {room?.name}
                    </Text>

                    {reports.length > 0 ? (
                        <ScrollView>
                            {reports.map((issue, index) => (
                                <View key={issue.id || index} style={createReportStyle.issueItem}>
                                    <Text style={createReportStyle.issueText}>- {issue.description}</Text>
                                </View>
                            ))}
                        </ScrollView>
                    ) : (
                        <Text style={commonStyles.emptyText}>
                            No issues reported for this room.
                        </Text>
                    )}
                </View>
            </View>
        </View>
    );
};
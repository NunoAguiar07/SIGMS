import {
    View,
    FlatList,
} from 'react-native';
import {commonStyles} from "../css_styling/common/CommonProps";
import {createReportStyle} from "../css_styling/createReport/CreateReportStyle";
import {CreateReportScreenType} from "../types/CreateReportScreenType";
import {RoomInterface} from "../../types/RoomInterface";
import {isMobile} from "../../utils/DeviceType";
import {
    Card,
    CenteredContainer,
    Container, GridColumn, GridRow,
    IssuesListContainer,
} from "../css_styling/common/NewContainers";
import {BodyText, Subtitle} from "../css_styling/common/Typography";
import {Input, SearchInput} from "../css_styling/common/Inputs";
import {FlatListContainer, FlatListItem} from "../css_styling/common/FlatList";
import {Button, ButtonText} from "../css_styling/common/Buttons";
import {theme} from "../css_styling/common/Theme";


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
            <Container flex={1} justifyContent="center" padding="md">
                <GridRow>
                    <GridColumn widthPercent={50}>
                        <RoomSearchList
                            rooms={rooms}
                            searchQuery={searchQuery}
                            onSearchChange={onSearchChange}
                            onRoomSelect={onRoomSelect}
                        />
                    </GridColumn>
                    {selectedRoom && (
                        <GridColumn widthPercent={50}>
                            <GridRow heightPercent={50}>
                                <IssuesListContainer>
                                    <Subtitle color={theme.colors.text.black}>Previous Reports for {selectedRoom.name}</Subtitle>
                                    {issues.length > 0 ? (
                                        issues.map(issue => (
                                            <Container padding={"xs"} borderRadius={"small"}>
                                                <BodyText>- {issue.description}</BodyText>
                                            </Container>
                                        ))
                                    ) : (
                                        <BodyText>No issues reported for this room.</BodyText>
                                    )}
                                </IssuesListContainer>
                            </GridRow>
                            <GridRow heightPercent={50}>
                                <RoomReportForm
                                        selectedRoom={selectedRoom}
                                    reportText={reportText}
                                    onReportTextChange={onReportTextChange}
                                    onSubmitReport={onSubmitReport}
                                />
                            </GridRow>
                        </GridColumn>
                    )}
                </GridRow>
            </Container>
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
    <CenteredContainer style={{ position: 'relative' , zIndex: 10}}>
        <SearchInput
            placeholder="Search rooms..."
            value={searchQuery}
            onChangeText={onSearchChange}
        />
        {rooms.length > 0 && (
            <FlatListContainer>
                <FlatList
                    data={rooms}
                    keyExtractor={(item) => item.id.toString()}
                    renderItem={({ item }) => (
                        <FlatListItem
                            onPress={() => onRoomSelect(item)}
                        >
                            <BodyText>{item.name}</BodyText>
                        </FlatListItem>
                    )}
                />
            </FlatListContainer>
        )}
    </CenteredContainer>
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
    <CenteredContainer>
        <Card shadow="medium" alignItems={"center"} gap="md">
            <Subtitle color={theme.colors.text.white}>
                Report for {selectedRoom.name}
            </Subtitle>
            <Input
                placeholder="Describe the issue or note here..."
                multiline
                value={reportText}
                onChangeText={onReportTextChange}
            />
            <Button
                variant="primary"
                onPress={() => onSubmitReport(selectedRoom.id, reportText)}
            >
                <ButtonText>Submit Report</ButtonText>
            </Button>
        </Card>
    </CenteredContainer>
);
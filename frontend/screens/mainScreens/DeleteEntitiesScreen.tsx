import {SubjectInterface} from "../../types/SubjectInterface";
import {SchoolClassInterface} from "../../types/SchoolClassInterface";
import {RoomInterface} from "../../types/RoomInterface";
import {Lecture} from "../../types/calendar/Lecture";
import {
    Card,
    CenteredContainer, ColumnContainer,
    Container,
    GridColumn,
    GridRow,
    RowContainer
} from "../css_styling/common/NewContainers";
import {FlatListContainer, FlatListItem} from "../css_styling/common/FlatList";
import {FlatList} from "react-native";
import {BodyText, Subtitle} from "../css_styling/common/Typography";
import {Button, ButtonText} from "../css_styling/common/Buttons";
import {SearchInput} from "../css_styling/common/Inputs";
import {EntityType} from "../types/EntityCreationScreenType";
import {isMobile} from "../../utils/DeviceType";


export const AdminEntityDeletionScreen = ({
                                              selectedEntity,
                                              onEntitySelect,
                                              // Subject/Class props
                                              subjects,
                                              subjectClasses,
                                              selectedSubject,
                                              selectedClass,
                                              searchQuerySubjects,
                                              onSearchQuerySubjectsChange,
                                              onSubjectSelect,
                                              onClassSelect,
                                              // Room props
                                              rooms,
                                              selectedRoom,
                                              searchQueryRooms,
                                              onSearchQueryRoomsChange,
                                              onRoomSelect,
                                              // Lecture props
                                              lectures,
                                              selectedLecture,
                                              lectureFilter,
                                              onLectureFilterChange,
                                              onLectureSelect,
                                              // Delete action
                                              onDelete
                                          }: AdminEntityDeletionScreenType) => {
    const renderEntitySelection = () => {
        switch (selectedEntity) {
            case 'Subject':
                return (
                    <SubjectSearchList
                        subjects={subjects}
                        searchQuery={searchQuerySubjects}
                        onSearchChange={onSearchQuerySubjectsChange}
                        onSubjectSelect={onSubjectSelect}
                    />
                );
            case 'Class':
                return (
                    <ClassSelection
                        subjects={subjects}
                        subjectClasses={subjectClasses}
                        selectedSubject={selectedSubject}
                        searchQuery={searchQuerySubjects}
                        onSearchChange={onSearchQuerySubjectsChange}
                        onSubjectSelect={onSubjectSelect}
                        onClassSelect={onClassSelect}
                    />
                );
            case 'Room':
                return (
                    <RoomSearchList
                        rooms={rooms}
                        searchQuery={searchQueryRooms}
                        onSearchChange={onSearchQueryRoomsChange}
                        onRoomSelect={onRoomSelect}
                    />
                );
            case 'Lecture':
                return (
                    <LectureSelection
                        lectures={lectures}
                        lectureFilter={lectureFilter}
                        onLectureFilterChange={onLectureFilterChange}
                        onLectureSelect={onLectureSelect}
                        subjects={subjects}
                        subjectClasses={subjectClasses}
                        selectedSubject={selectedSubject}
                        searchQuerySubjects={searchQuerySubjects}
                        onSearchQuerySubjectsChange={onSearchQuerySubjectsChange}
                        onSubjectSelect={onSubjectSelect}
                        onClassSelect={onClassSelect}
                        rooms={rooms}
                        searchQueryRooms={searchQueryRooms}
                        onSearchQueryRoomsChange={onSearchQueryRoomsChange}
                        onRoomSelect={onRoomSelect}
                    />
                );
            default:
                return <Subtitle>Select an entity type to delete</Subtitle>;
        }
    };

    if (!isMobile) {
        return (
            <Container flex={1} justifyContent="center" padding="md">
                <GridRow>
                    <GridColumn widthPercent={50} gap={"md"}>
                        {['Subject', 'Class', 'Room', 'Lecture'].map((entity) => (
                            <Button
                                variant="primary"
                                key={entity}
                                size={"large"}
                                onPress={() => onEntitySelect(entity as EntityType)}
                            >
                                <ButtonText>{entity}</ButtonText>
                            </Button>
                        ))}
                    </GridColumn>
                    <GridColumn widthPercent={50}>
                        <Card shadow="medium" alignItems={"center"} gap="md">
                            {renderEntitySelection()}
                            {selectedEntity && (
                                <RowContainer gap={"md"}>
                                    <Button
                                        variant="primary"
                                        onPress={onDelete}
                                        disabled={
                                            (selectedEntity === 'Subject' && !selectedSubject) ||
                                            (selectedEntity === 'Class' && (!selectedSubject || !selectedClass)) ||
                                            (selectedEntity === 'Room' && !selectedRoom) ||
                                            (selectedEntity === 'Lecture' && !selectedLecture)
                                        }
                                    >
                                        <ButtonText>Delete {selectedEntity}</ButtonText>
                                    </Button>
                                    <Button
                                        variant="primary"
                                        onPress={() => onEntitySelect(null)}
                                    >
                                        <ButtonText>Back</ButtonText>
                                    </Button>
                                </RowContainer>
                            )}
                        </Card>
                    </GridColumn>
                </GridRow>
            </Container>
        );
    } else {
        return (
            !selectedEntity ? (
                <CenteredContainer flex={1} padding="md" gap="md">
                    <ColumnContainer gap="md">
                        {['Subject', 'Class', 'Room', 'Lecture'].map((entity) => (
                            <Button
                                key={entity}
                                variant="primary"
                                onPress={() => onEntitySelect(entity as EntityType)}
                            >
                                <ButtonText>{entity}</ButtonText>
                            </Button>
                        ))}
                    </ColumnContainer>
                </CenteredContainer>
            ) : (
                <CenteredContainer flex={1} padding="md" gap="md">
                    <Card shadow="medium" alignItems={"center"} gap="md">
                        {renderEntitySelection()}
                        {selectedEntity && (
                            <ColumnContainer gap={"md"}>
                                <Button
                                    variant="primary"
                                    onPress={onDelete}
                                    disabled={
                                        (selectedEntity === 'Subject' && !selectedSubject) ||
                                        (selectedEntity === 'Class' && (!selectedSubject || !selectedClass)) ||
                                        (selectedEntity === 'Room' && !selectedRoom) ||
                                        (selectedEntity === 'Lecture' && !selectedLecture)
                                    }
                                >
                                    <ButtonText>Delete {selectedEntity}</ButtonText>
                                </Button>
                                <Button
                                    variant="primary"
                                    onPress={() => onEntitySelect(null)}
                                >
                                    <ButtonText>Back</ButtonText>
                                </Button>
                            </ColumnContainer>
                        )}
                    </Card>
                </CenteredContainer>
            )
        );
    }
};

const SubjectSearchList = ({
                               subjects,
                               searchQuery,
                               onSearchChange,
                               onSubjectSelect
                           }: {
    subjects: SubjectInterface[];
    searchQuery: string;
    onSearchChange: (query: string) => void;
    onSubjectSelect: (subject: SubjectInterface) => void;
}) => (
    <CenteredContainer style={{ position: 'relative', zIndex: 10 }}>
        <Subtitle>Select Subject</Subtitle>
        <SearchInput
            placeholder="Search subjects..."
            value={searchQuery}
            onChangeText={onSearchChange}
        />
        {subjects.length > 0 && (
            <FlatListContainer position={"absolute"}>
                <FlatList
                    data={subjects}
                    keyExtractor={(item) => item.id.toString()}
                    renderItem={({ item }) => (
                        <FlatListItem
                            onPress={() => onSubjectSelect(item)}
                        >
                            <BodyText>{item.name}</BodyText>
                        </FlatListItem>
                    )}
                />
            </FlatListContainer>
        )}
    </CenteredContainer>
);

const ClassSelection = ({
                            subjects,
                            subjectClasses,
                            selectedSubject,
                            searchQuery,
                            onSearchChange,
                            onSubjectSelect,
                            onClassSelect
                        }: {
    subjects: SubjectInterface[];
    subjectClasses: SchoolClassInterface[];
    selectedSubject: SubjectInterface | null;
    searchQuery: string;
    onSearchChange: (query: string) => void;
    onSubjectSelect: (subject: SubjectInterface) => void;
    onClassSelect: (schoolClass: SchoolClassInterface) => void;
}) => (
    <CenteredContainer gap={"md"} style={{ position: 'relative', zIndex: 10 }}>
        <Subtitle>Class to Delete</Subtitle>
        <SubjectSearchList
            subjects={subjects}
            searchQuery={searchQuery}
            onSearchChange={onSearchChange}
            onSubjectSelect={onSubjectSelect}
        />
        {selectedSubject && (
            <CenteredContainer>
                <Subtitle>Classes for {selectedSubject.name}</Subtitle>
                {subjectClasses.length > 0 ? (
                    <FlatListContainer position={"static"}>
                        <FlatList
                            data={subjectClasses}
                            keyExtractor={(item) => item.id.toString()}
                            renderItem={({ item }) => (
                                <FlatListItem
                                    onPress={() => onClassSelect(item)}
                                >
                                    <BodyText>{item.name}</BodyText>
                                </FlatListItem>
                            )}
                        />
                    </FlatListContainer>
                ) : (
                    <BodyText>No classes available for this subject</BodyText>
                )}
            </CenteredContainer>
        )}
    </CenteredContainer>
);

const RoomSearchList = ({
                            rooms,
                            searchQuery,
                            onSearchChange,
                            onRoomSelect
                        }: {
    rooms: RoomInterface[];
    searchQuery: string;
    onSearchChange: (query: string) => void;
    onRoomSelect: (room: RoomInterface) => void;
}) => (
    <CenteredContainer style={{ position: 'relative', zIndex: 10 }}>
        <Subtitle>Select Room to Delete</Subtitle>
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
                            <BodyText>{item.name} (Capacity: {item.capacity})</BodyText>
                        </FlatListItem>
                    )}
                />
            </FlatListContainer>
        )}
    </CenteredContainer>
);

const LectureSelection = ({
                              lectures,
                              lectureFilter,
                              onLectureFilterChange,
                              onLectureSelect,
                              // Subject/Class props
                              subjects,
                              subjectClasses,
                              selectedSubject,
                              searchQuerySubjects,
                              onSearchQuerySubjectsChange,
                              onSubjectSelect,
                              onClassSelect,
                              // Room props
                              rooms,
                              searchQueryRooms,
                              onSearchQueryRoomsChange,
                              onRoomSelect
                          }: {
    lectures: Lecture[];
    lectureFilter: 'all' | 'class' | 'room';
    onLectureFilterChange: (filter: 'all' | 'class' | 'room') => void;
    onLectureSelect: (lecture: Lecture) => void;
    // Subject/Class props
    subjects: SubjectInterface[];
    subjectClasses: SchoolClassInterface[];
    selectedSubject: SubjectInterface | null;
    searchQuerySubjects: string;
    onSearchQuerySubjectsChange: (query: string) => void;
    onSubjectSelect: (subject: SubjectInterface) => void;
    onClassSelect: (schoolClass: SchoolClassInterface) => void;
    // Room props
    rooms: RoomInterface[];
    searchQueryRooms: string;
    onSearchQueryRoomsChange: (query: string) => void;
    onRoomSelect: (room: RoomInterface) => void;
}) => {
    return (
        <CenteredContainer gap={"md"}>
            <Subtitle>Select Lecture to Delete</Subtitle>

            <Container gap={"md"} justifyContent={"space-between"} flexDirection={isMobile ? "column" : "row"}>
                <Button
                    variant='primary'
                    onPress={() => onLectureFilterChange('all')}
                >
                    <ButtonText>All Lectures</ButtonText>
                </Button>
                <Button
                    variant='primary'
                    onPress={() => onLectureFilterChange('class')}
                >
                    <ButtonText>By Class</ButtonText>
                </Button>
                <Button
                    variant='primary'
                    onPress={() => onLectureFilterChange('room')}
                >
                    <ButtonText>By Room</ButtonText>
                </Button>
            </Container>
            <CenteredContainer gap={"md"} style={{ position: 'relative', zIndex: 10 }}>
                {lectureFilter === 'class' && (
                    <ClassSelection
                        subjects={subjects}
                        subjectClasses={subjectClasses}
                        selectedSubject={selectedSubject}
                        searchQuery={searchQuerySubjects}
                        onSearchChange={onSearchQuerySubjectsChange}
                        onSubjectSelect={onSubjectSelect}
                        onClassSelect={onClassSelect}
                    />
                )}

                {lectureFilter === 'room' && (
                    <RoomSearchList
                        rooms={rooms}
                        searchQuery={searchQueryRooms}
                        onSearchChange={onSearchQueryRoomsChange}
                        onRoomSelect={onRoomSelect}
                    />
                )}
            </CenteredContainer>

            {lectures.length > 0 ? (
                <FlatListContainer position={"static"} style={{ zIndex: 6}}>
                    <FlatList
                        data={lectures}
                        keyExtractor={(item) => item.id.toString()}
                        renderItem={({ item }) => (
                            <FlatListItem
                                onPress={() => onLectureSelect(item)}
                            >
                                <BodyText>
                                    {item.schoolClass.subject.name} - {item.schoolClass.name} |
                                    Room: {item.room.name} |
                                    {item.startTime} - {item.endTime}
                                </BodyText>
                            </FlatListItem>
                        )}
                    />
                </FlatListContainer>
            ) : (
                <BodyText>No lectures found</BodyText>
            )}
        </CenteredContainer>
    );
};

type AdminEntityDeletionScreenType = {
    // Entity selection
    selectedEntity: EntityType;
    onEntitySelect: (entity: EntityType) => void;

    // Subject/Class props
    subjects: SubjectInterface[];
    subjectClasses: SchoolClassInterface[];
    selectedSubject: SubjectInterface | null;
    selectedClass: SchoolClassInterface | null;
    searchQuerySubjects: string;
    onSearchQuerySubjectsChange: (query: string) => void;
    onSubjectSelect: (subject: SubjectInterface) => void;
    onClassSelect: (schoolClass: SchoolClassInterface) => void;

    // Room props
    rooms: RoomInterface[];
    selectedRoom: RoomInterface | null;
    searchQueryRooms: string;
    onSearchQueryRoomsChange: (query: string) => void;
    onRoomSelect: (room: RoomInterface) => void;

    // Lecture props
    lectures: Lecture[];
    selectedLecture: Lecture | null;
    lectureFilter: 'all' | 'class' | 'room';
    onLectureFilterChange: (filter: 'all' | 'class' | 'room') => void;
    onLectureSelect: (lecture: Lecture) => void;

    // Delete action
    onDelete: () => void;
};
import {FlatList, ScrollView} from "react-native";
import {EntityCreationScreenType, EntityType} from "../types/EntityCreationScreenType";
import {Ionicons} from "@expo/vector-icons";
import {FormCreateEntityValues, LectureType, RoomType} from "../../types/welcome/FormCreateEntityValues";
import {SubjectInterface} from "../../types/SubjectInterface";
import {SchoolClassInterface} from "../../types/SchoolClassInterface";
import {RoomInterface} from "../../types/RoomInterface";
import {isMobile} from "../../utils/DeviceType";
import {
    Card,
    CenteredContainer, ColumnContainer,
    Container,
    GridColumn,
    GridRow, RowContainer
} from "../css_styling/common/NewContainers";
import {ActionButton, Button, ButtonText} from "../css_styling/common/Buttons";
import {BodyText, Subtitle} from "../css_styling/common/Typography";
import {SearchInput} from "../css_styling/common/Inputs";
import {FlatListContainer, FlatListItem} from "../css_styling/common/FlatList";
import {PickerContainer, StyledPicker, StyledPickerItem} from "../css_styling/common/Picker";
import {TimePicker} from "./UpdateLectureScreen";

export const AdminEntityCreationScreen = ({
    selectedEntity,
    onEntitySelect,
    handleEntitySelect,
    formValues,
    setFormValues,
    onSubmit,
    subjects,
    subjectClasses,
    rooms,
    searchQuerySubjects,
    setSearchQuerySubjects,
    searchQueryRooms,
    setSearchQueryRooms,
    onItemSelect
}: EntityCreationScreenType) => {
    const renderForm = () => {
        switch (selectedEntity) {
            case 'Subject':
                return <SubjectForm formValues={formValues} setFormValues={setFormValues} />;
            case 'Class':
                return (
                    <ClassForm
                        formValues={formValues}
                        setFormValues={setFormValues}
                        searchQuery={searchQuerySubjects}
                        setSearchQuery={setSearchQuerySubjects}
                        subjects={subjects}
                        onItemSelect={onItemSelect}
                    />
                );
            case 'Room':
                return <RoomForm formValues={formValues} setFormValues={setFormValues} />;
            case 'Lecture':
                return (
                    <LectureForm
                        formValues={formValues}
                        setFormValues={setFormValues}
                        handleEntitySelect={handleEntitySelect}
                        subjects={subjects}
                        subjectClasses={subjectClasses}
                        rooms={rooms}
                        searchQuerySubjects={searchQuerySubjects}
                        setSearchQuerySubjects={setSearchQuerySubjects}
                        searchQueryRooms={searchQueryRooms}
                        setSearchQueryRooms={setSearchQueryRooms}
                        onItemSelect={onItemSelect}
                    />
                );
            default:
                return (
                    <Subtitle>Select an entity type to add</Subtitle>
                );
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
                        <Card shadow="medium" alignItems={"center"} gap="md" flex={selectedEntity === 'Lecture' && formValues.subjectId ? 1 : undefined}>
                            {renderForm()}
                            {selectedEntity && (selectedEntity != 'Lecture' || formValues.subjectId) &&(
                                <Button variant="primary" onPress={onSubmit}>
                                    <ButtonText>Create {selectedEntity}</ButtonText>
                                </Button>
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
                        {renderForm()}
                        {selectedEntity && (selectedEntity != 'Lecture' || formValues.subjectId) &&(
                            <Button variant="primary" onPress={onSubmit}>
                                <ButtonText>Create {selectedEntity}</ButtonText>
                            </Button>
                        )}
                    </Card>
                </CenteredContainer>
            )
        );
    }
};

type SubjectFromType = {
    formValues: FormCreateEntityValues;
    setFormValues: (values: FormCreateEntityValues) => void;
};

export const SubjectForm = ({ formValues, setFormValues }: SubjectFromType) => (
    <CenteredContainer gap={"md"}>
        <Subtitle>Create New Subject</Subtitle>
        <SearchInput
            placeholder="Subject Name"
            value={formValues.name || ''}
            onChangeText={(text) => setFormValues({ ...formValues, name: text })}
        />
    </CenteredContainer>
);

type ClassFormType = {
    formValues: FormCreateEntityValues;
    setFormValues: (values: FormCreateEntityValues) => void;
    searchQuery: string;
    setSearchQuery: (query: string) => void;
    subjects: SubjectInterface[];
    onItemSelect: (item: SubjectInterface) => void;
};

export const ClassForm = ({ formValues, setFormValues, searchQuery, setSearchQuery, subjects, onItemSelect }: ClassFormType) => {
    return (
        <CenteredContainer gap={"md"} style={{ position: 'relative' , zIndex: 10}}>
            <Subtitle>Create New Class</Subtitle>
            <SearchInput
                placeholder="Class Name"
                value={formValues.name || ''}
                onChangeText={(text) => setFormValues({ ...formValues, name: text })}
            />
            <Subtitle>Select Subject:</Subtitle>
            <SearchInput
                placeholder="Search subjects..."
                value={searchQuery}
                onChangeText={setSearchQuery}
            />
            {subjects.length > 0 && (
                <FlatListContainer>
                    <FlatList
                        data={subjects}
                        keyExtractor={(item) => item.id.toString()}
                        renderItem={({ item }) => (
                            <FlatListItem
                                onPress={() => onItemSelect(item)}
                            >
                                <BodyText>{item.name}</BodyText>
                            </FlatListItem>
                        )}
                    />
                </FlatListContainer>
            )}
        </CenteredContainer>
    );
}

type RoomFormType = {
    formValues: FormCreateEntityValues;
    setFormValues: (values: FormCreateEntityValues) => void;
};

export const RoomForm = ({ formValues, setFormValues }: RoomFormType) => (
    <CenteredContainer gap={"md"}>
        <Subtitle>Create New Room</Subtitle>
        <SearchInput
            placeholder="Room Name"
            value={formValues.name || ''}
            onChangeText={(text) => setFormValues({ ...formValues, name: text })}
        />
        <SearchInput
            placeholder="Capacity"
            keyboardType="numeric"
            value={formValues.capacity?.toString() || ''}
            onChangeText={(text) => setFormValues({
                ...formValues,
                capacity: text ? parseInt(text) : null,
            })}
        />
        <PickerContainer width={"200px"}>
            <StyledPicker
                selectedValue={formValues.roomType}
                onValueChange={(value) => setFormValues({ ...formValues, roomType: value as RoomType })}
            >
                <StyledPickerItem label="Classroom" value="CLASS" />
                <StyledPickerItem label="Study Room" value="STUDY" />
                <StyledPickerItem label="Office Room" value="OFFICE" />
            </StyledPicker>
        </PickerContainer>
    </CenteredContainer>
);


type LectureFormType = {
    formValues: FormCreateEntityValues;
    setFormValues: (values: FormCreateEntityValues) => void;
    handleEntitySelect: (entity: EntityType) => void;
    subjects: SubjectInterface[];
    subjectClasses: SchoolClassInterface[];
    rooms: RoomInterface[];
    searchQuerySubjects: string;
    setSearchQuerySubjects: (query: string) => void;
    searchQueryRooms: string;
    setSearchQueryRooms: (query: string) => void;
    onItemSelect: (item: any) => void;
};

export const LectureForm = ({
    formValues,
    setFormValues,
    handleEntitySelect,
    subjects,
    subjectClasses,
    rooms,
    searchQuerySubjects,
    setSearchQuerySubjects,
    searchQueryRooms,
    setSearchQueryRooms,
    onItemSelect,
}: LectureFormType) => {
    return (
        <CenteredContainer gap={"md"} flex={formValues.subjectId ? 1 : undefined}>
            <Subtitle>Create New Lecture</Subtitle>
            {!formValues.subjectId ? (
                <LectureSubjectSelection
                    subjects={subjects}
                    searchQuery={searchQuerySubjects}
                    setSearchQuery={setSearchQuerySubjects}
                    onItemSelect={onItemSelect}
                />
            ) : (
                <ScrollView>
                    <LectureSelectedSubject
                        subjectName={searchQuerySubjects}
                        handleEntitySelect={handleEntitySelect}
                    />
                    <LectureClassSelection
                        subjectClasses={subjectClasses}
                        formValues={formValues}
                        setFormValues={setFormValues}
                    />
                    <LectureRoomSelection
                        rooms={rooms}
                        searchQuery={searchQueryRooms}
                        setSearchQuery={setSearchQueryRooms}
                        onItemSelect={onItemSelect}
                    />
                    <LectureDetailsSelection
                        formValues={formValues}
                        setFormValues={setFormValues}
                    />
                </ScrollView>
            )}
        </CenteredContainer>
    );
};

const LectureSubjectSelection = ({
    subjects,
    searchQuery,
    setSearchQuery,
    onItemSelect
}: {
    subjects: SubjectInterface[];
    searchQuery: string;
    setSearchQuery: (query: string) => void;
    onItemSelect: (item: SubjectInterface) => void;
}) => {
    return (
        <CenteredContainer gap={'md'}>
            <Subtitle>Select Subject:</Subtitle>
            <SearchInput
                placeholder="Search subjects..."
                value={searchQuery}
                onChangeText={setSearchQuery}
            />
            {subjects.length > 0 && (
                <FlatListContainer>
                    <FlatList
                        data={subjects}
                        keyExtractor={(item) => item.id.toString()}
                        renderItem={({item}) => (
                            <FlatListItem
                                onPress={() => onItemSelect(item)}
                            >
                                <BodyText>{item.name}</BodyText>
                            </FlatListItem>
                        )}
                    />
                </FlatListContainer>
            )}
        </CenteredContainer>
    );
}

const LectureSelectedSubject = ({
    subjectName,
    handleEntitySelect
}: {
    subjectName: string;
    handleEntitySelect: (entity: EntityType) => void;
}) => (
    <CenteredContainer margin={"md"}>
        <Subtitle>Selected Subject:</Subtitle>
        <RowContainer gap={"md"}>
            <BodyText>{subjectName}</BodyText>
            <ActionButton
                variant="primary"
                onPress={() => handleEntitySelect('Lecture')}
            >
                <Ionicons name="arrow-undo-circle-outline" size={16} color="white" />
            </ActionButton>
        </RowContainer>
    </CenteredContainer>
);

const LectureClassSelection = ({
    subjectClasses,
    formValues,
    setFormValues,
}: {
    subjectClasses: SchoolClassInterface[];
    formValues: FormCreateEntityValues;
    setFormValues: (values: FormCreateEntityValues) => void;
}) => (
    <CenteredContainer gap={"md"} margin={"md"}>
        <Subtitle>Select Class:</Subtitle>
        {subjectClasses.length > 0 ? (
            <FlatListContainer position={"static"}>
                <FlatList
                    data={subjectClasses}
                    keyExtractor={(item) => item.id.toString()}
                    renderItem={({ item }) => (
                        <FlatListItem
                            onPress={() => setFormValues({ ...formValues, schoolClassId: item.id })}
                            style={{
                                backgroundColor: item.id === formValues.schoolClassId
                                    ? '#e3f2fd'
                                    : 'transparent'
                            }}
                        >
                            <BodyText>{item.name}</BodyText>
                        </FlatListItem>
                    )}
                />
            </FlatListContainer>
        ) : (
            <BodyText>No classes available</BodyText>
        )}
    </CenteredContainer>
);

const LectureRoomSelection = ({
    rooms,
    searchQuery,
    setSearchQuery,
    onItemSelect
}: {
    rooms: RoomInterface[];
    searchQuery: string;
    setSearchQuery: (query: string) => void;
    onItemSelect: (item: RoomInterface) => void;
}) => {
    return (
        <CenteredContainer style={{position: 'relative', zIndex: 10}} gap={"md"}>
            <Subtitle>Select Room:</Subtitle>
            <SearchInput
                placeholder="Search rooms..."
                value={searchQuery}
                onChangeText={setSearchQuery}
            />
            {rooms.length > 0 && (
                <FlatListContainer position={"absolute"}>
                    <FlatList
                        data={rooms}
                        keyExtractor={(item) => item.id.toString()}
                        renderItem={({item}) => (
                            <FlatListItem
                                onPress={() => onItemSelect(item)}
                            >
                                <BodyText>{item.name}</BodyText>
                            </FlatListItem>
                        )}
                    />
                </FlatListContainer>
            )}
        </CenteredContainer>
    );
}

export const LectureDetailsSelection = ({
    formValues,
    setFormValues
}: {
    formValues: FormCreateEntityValues;
    setFormValues: (values: FormCreateEntityValues) => void;
}) => (
    <CenteredContainer gap={'md'}>
        <Subtitle>Lecture Type</Subtitle>
        <PickerContainer width="100px">
            <StyledPicker
                selectedValue={formValues.lectureType}
                onValueChange={(value) => setFormValues({ ...formValues, lectureType: value as LectureType})}
            >
                <StyledPickerItem label="Theoretical" value="THEORETICAL" />
                <StyledPickerItem label="Practical" value="PRACTICAL" />
                <StyledPickerItem label="Mix" value="THEORETICAL_PRACTICAL" />
            </StyledPicker>
        </PickerContainer>
        <Subtitle>Weekday</Subtitle>
        <PickerContainer width="200px">
            <StyledPicker
                selectedValue={formValues.weekDay}
                onValueChange={(value) => setFormValues({ ...formValues, weekDay: value as number})}
            >
                <StyledPickerItem label="Monday" value={1} />
                <StyledPickerItem label="Tuesday" value={2} />
                <StyledPickerItem label="Wednesday" value={3} />
                <StyledPickerItem label="Thursday" value={4} />
                <StyledPickerItem label="Friday" value={5} />
                <StyledPickerItem label="Saturday" value={6} />
                <StyledPickerItem label="Sunday" value={7} />
            </StyledPicker>
        </PickerContainer>
        <TimePicker
            label="Start Time"
            placeholder="HH:MM"
            selectedValue={formValues.startTime || ''}
            onValueChange={(text) => setFormValues({ ...formValues, startTime: text })}

        />
        <TimePicker
            label="End Time"
            placeholder="HH:MM"
            selectedValue={formValues.endTime || ''}
            onValueChange={(text) => setFormValues({ ...formValues, endTime: text })}
        />
    </CenteredContainer>
);
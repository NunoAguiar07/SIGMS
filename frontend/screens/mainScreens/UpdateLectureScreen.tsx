import {FlatList, ScrollView, View} from "react-native";
import {scheduleLectureStyles} from "../css_styling/lecture/scheduleLectureStyles";
import {commonStyles} from "../css_styling/common/CommonProps";
import {RoomInterface} from "../../types/RoomInterface";
import {Lecture} from "../../types/calendar/Lecture";
import {isMobile} from "../../utils/DeviceType";
import {UpdateLectureProps} from "../css_styling/update_lecture/UpdateLectureProps";
import {
    Card,
    CenteredContainer,
    Container,
    GridColumn,
    GridRow
} from "../css_styling/common/NewContainers";
import {BodyText, Subtitle} from "../css_styling/common/Typography";
import {Button, ButtonText} from "../css_styling/common/Buttons";
import {SubjectInterface} from "../../types/SubjectInterface";
import {SchoolClassInterface} from "../../types/SchoolClassInterface";
import {FlatListContainer, FlatListItem} from "../css_styling/common/FlatList";
import {Input, SearchInput} from "../css_styling/common/Inputs";
import {PaginationControls} from "./UnassignedIssuesScreen";
import {PickerContainer, StyledPicker, StyledPickerItem} from "../css_styling/common/Picker";
import {LectureType} from "../../types/welcome/FormCreateEntityValues";


export const UpdateLectureScreen = ({
    lectures,
    selectedLecture,
    onLectureSelect,
    onScheduleChange,
    onSaveSchedule,
    isSaving,
    rooms,
    searchQueryRoom,
    setSearchQueryRoom,
    handleRoomSelect,
    setEffectiveFromText,
    setEffectiveUntilText,
    effectiveFromText,
    effectiveUntilText,
    searchQuerySubjects,
    setSearchQuerySubjects,
    subjects,
    onGetAllLectures,
    handleOnSubjectSelect,
    handleClassSelect,
    lectureFilter,
    classes,
    setLectureFilter,
    handleNext,
    handlePrevious,
    page
}: UpdateLectureProps) => {
    if (!isMobile) {
        return (
            <Container flex={1} padding="md">
                <GridRow flex={1}>
                    <GridColumn widthPercent={50} gap={"md"}>
                        <LectureList
                            lectures={lectures}
                            onLectureSelect={onLectureSelect}
                            searchQueryRoom={searchQueryRoom}
                            setSearchQueryRoom={setSearchQueryRoom}
                            rooms={rooms}
                            handleRoomSelect={handleRoomSelect}
                            searchQuerySubjects={searchQuerySubjects}
                            setSearchQuerySubjects={setSearchQuerySubjects}
                            subjects={subjects}
                            handleClassSelect={handleClassSelect}
                            handleOnSubjectSelect={handleOnSubjectSelect}
                            onGetAllLectures={onGetAllLectures}
                            classes={classes}
                            lectureFilter={lectureFilter}
                            setLectureFilter={setLectureFilter}
                            handleNext={handleNext}
                            handlePrevious={handlePrevious}
                            page={page}
                        />
                    </GridColumn>
                    <GridColumn widthPercent={50}>
                        <CenteredContainer flex={1} gap="md" padding="md" >
                            <Card shadow="medium" alignItems={"center"} gap="md" flex={selectedLecture ? 1: undefined}>
                                {selectedLecture ? (
                                    <UpdateLectureForm
                                        selectedLecture={selectedLecture}
                                        onScheduleChange={onScheduleChange}
                                        rooms={rooms}
                                        searchQuery={searchQueryRoom}
                                        setSearchQuery={setSearchQueryRoom}
                                        handleRoomSelect={handleRoomSelect}
                                        effectiveFromText={effectiveFromText}
                                        effectiveUntilText={effectiveUntilText}
                                        setEffectiveFromText={setEffectiveFromText}
                                        setEffectiveUntilText={setEffectiveUntilText}
                                        onSaveSchedule={onSaveSchedule}
                                        isSaving={isSaving}
                                    />
                                ) : (
                                    <Subtitle>Select a lecture to update schedule</Subtitle>
                                )}
                            </Card>
                        </CenteredContainer>
                    </GridColumn>
                </GridRow>
            </Container>
        )
    } else {
        return(
            <View style={scheduleLectureStyles.container}>
                {!selectedLecture ? (
                    <View style={commonStyles.leftColumn}>
                        <LectureList
                            lectures={lectures}
                            onLectureSelect={onLectureSelect}
                            searchQueryRoom={searchQueryRoom}
                            setSearchQueryRoom={setSearchQueryRoom}
                            rooms={rooms}
                            handleRoomSelect={handleRoomSelect}
                            searchQuerySubjects={searchQuerySubjects}
                            setSearchQuerySubjects={setSearchQuerySubjects}
                            subjects={subjects}
                            handleClassSelect={handleClassSelect}
                            handleOnSubjectSelect={handleOnSubjectSelect}
                            onGetAllLectures={onGetAllLectures}
                            classes={classes}
                            lectureFilter={lectureFilter}
                            setLectureFilter={setLectureFilter}
                            handleNext={handleNext}
                            handlePrevious={handlePrevious}
                            page={page}
                        />
                    </View>
                ) : (
                    <View style={commonStyles.rightColumn}>
                        <FlatList
                            data={[1]} // Single item to wrap our content
                            renderItem={() => <UpdateLectureForm
                            selectedLecture={selectedLecture}
                            onScheduleChange={onScheduleChange}
                            rooms={rooms}
                            searchQuery={searchQueryRoom}
                            setSearchQuery={setSearchQueryRoom}
                            handleRoomSelect={handleRoomSelect}
                            effectiveFromText={effectiveFromText}
                            effectiveUntilText={effectiveUntilText}
                            setEffectiveFromText={setEffectiveFromText}
                            setEffectiveUntilText={setEffectiveUntilText}
                            onSaveSchedule={onSaveSchedule}
                            isSaving={isSaving}
                            />}
                            keyExtractor={() => 'update-lecture-form'}
                        />
                    </View>
                ) }
            </View>
        )
    }
};

interface LectureListProps {
    lectures: Lecture[];
    onLectureSelect: (lecture: Lecture) => void;
    searchQueryRoom: string;
    setSearchQueryRoom: (query: string) => void;
    rooms: RoomInterface[];
    handleRoomSelect: (room: RoomInterface) => void;
    searchQuerySubjects: string;
    setSearchQuerySubjects: (query: string) => void;
    subjects: SubjectInterface[];
    handleClassSelect: (schoolClass: SchoolClassInterface) => void;
    handleOnSubjectSelect: (subject: SubjectInterface) => void;
    onGetAllLectures: () => void;
    classes: SchoolClassInterface[];
    lectureFilter: 'all' | 'class' | 'room';
    setLectureFilter: (filter: 'all' | 'class' | 'room') => void;
    handleNext: () => void;
    handlePrevious: () => void;
    page: number;
}
interface WeekdayPickerProps {
    selectedValue: number;
    onValueChange: (value: number) => void;
}

interface LectureTypePickerProps {
    selectedValue: string;
    onValueChange: (value: string) => void;
}
interface TimePickerProps {
    selectedValue: string;
    onValueChange: (value: string) => void;
    label: string;
    placeholder: string
}

interface UpdateLectureFormProps {
    selectedLecture: Lecture;
    onScheduleChange: (changes: Partial<Lecture>) => void;
    rooms: RoomInterface[];
    searchQuery: string;
    setSearchQuery: (query: string) => void;
    handleRoomSelect: (room: RoomInterface) => void;
    effectiveFromText: string;
    effectiveUntilText: string;
    setEffectiveFromText: (text: string) => void;
    setEffectiveUntilText: (text: string) => void;
    onSaveSchedule: () => void;
    isSaving: boolean;
}

interface FilterButtonsProps {
    onGetAllLectures: () => void;
    setLectureFilter: (filter: 'all' | 'class' | 'room') => void;
    widthPercent: number;
}

export const FilterButtons = ({
                                  onGetAllLectures,
                                  setLectureFilter,
                                  widthPercent
                              }: FilterButtonsProps) => (
    <GridColumn widthPercent={widthPercent} gap={"md"}>
        <Button variant={'primary'} onPress={onGetAllLectures}>
            <ButtonText>All Lectures</ButtonText>
        </Button>
        <Button variant={'primary'} onPress={() => setLectureFilter('class')}>
            <ButtonText>By Class</ButtonText>
        </Button>
        <Button variant={'primary'} onPress={() => setLectureFilter('room')}>
            <ButtonText>By Room</ButtonText>
        </Button>
    </GridColumn>
);

// RoomSearchSection.tsx
interface RoomSearchSectionProps {
    searchQueryRoom: string;
    setSearchQueryRoom: (query: string) => void;
    rooms: RoomInterface[];
    handleRoomSelect: (room: RoomInterface) => void;
}

export const RoomSearchSection = ({
                                      searchQueryRoom,
                                      setSearchQueryRoom,
                                      rooms,
                                      handleRoomSelect
                                  }: RoomSearchSectionProps) => (
        <CenteredContainer style={{ position: 'relative', zIndex: 10 }}>
            <SearchInput
                placeholder="Search rooms..."
                value={searchQueryRoom}
                onChangeText={setSearchQueryRoom}
            />
            {rooms.length > 0 && (
                <FlatListContainer>
                    <FlatList
                        data={rooms}
                        keyExtractor={(item) => item.id.toString()}
                        renderItem={({ item }) => (
                            <FlatListItem onPress={() => handleRoomSelect(item)}>
                                <BodyText>{item.name}</BodyText>
                            </FlatListItem>
                        )}
                    />
                </FlatListContainer>
            )}
        </CenteredContainer>
);

// SubjectSearchSection.tsx
interface SubjectSearchSectionProps {
    searchQuerySubjects: string;
    setSearchQuerySubjects: (query: string) => void;
    subjects: SubjectInterface[];
    handleOnSubjectSelect: (subject: SubjectInterface) => void;
}

export const SubjectSearchSection = ({
                                         searchQuerySubjects,
                                         setSearchQuerySubjects,
                                         subjects,
                                         handleOnSubjectSelect
                                     }: SubjectSearchSectionProps) => (
        <CenteredContainer style={{ position: 'relative', zIndex: 10 }} gap="md">
            <SearchInput
                placeholder="Search subjects..."
                value={searchQuerySubjects}
                onChangeText={setSearchQuerySubjects}
            />
            {subjects.length > 0 && (
                <FlatListContainer>
                    <FlatList
                        data={subjects}
                        keyExtractor={(item) => item.id.toString()}
                        renderItem={({ item }) => (
                            <FlatListItem onPress={() => handleOnSubjectSelect(item)}>
                                <BodyText>{item.name}</BodyText>
                            </FlatListItem>
                        )}
                    />
                </FlatListContainer>
            )}
        </CenteredContainer>
);

// ClassListSection.tsx
interface ClassListSectionProps {
    classes: SchoolClassInterface[];
    handleClassSelect: (schoolClass: SchoolClassInterface) => void;
}

export const ClassListSection = ({
    classes,
    handleClassSelect
}: ClassListSectionProps) => {
    if (classes.length === 0) {
        return (
            <CenteredContainer gap="md">
                <Subtitle>No classes available</Subtitle>
            </CenteredContainer>
        );
    }

    return (
        <CenteredContainer gap="md">
            <Subtitle>Select Class:</Subtitle>
            <FlatListContainer>
                <FlatList
                    data={classes}
                    keyExtractor={(item) => item.id.toString()}
                    renderItem={({item}) => (
                        <FlatListItem
                            onPress={() => handleClassSelect(item)}
                            style={{
                                paddingVertical: 8,
                                borderBottomWidth: 1,
                                borderBottomColor: '#f0f0f0'
                            }}
                        >
                            <BodyText>{item.name}</BodyText>
                        </FlatListItem>
                    )}
                />
            </FlatListContainer>
        </CenteredContainer>
    );
}

// LecturesListSection.tsx
interface LecturesListSectionProps {
    lectures: Lecture[];
    onLectureSelect: (lecture: Lecture) => void;
    handleNext: () => void;
    handlePrevious: () => void;
    page: number;
    hasNext: boolean;
}

export const LecturesListSection = ({
                                        lectures,
                                        onLectureSelect,
                                        handleNext,
                                        handlePrevious,
                                        page,
                                        hasNext
                                    }: LecturesListSectionProps) => (
    <Card shadow="medium" alignItems="center" gap="md">
        <Subtitle>Lectures</Subtitle>
        {lectures.length > 0 ? (
            <FlatListContainer position={"static"}>
                <FlatList
                    data={lectures}
                    keyExtractor={(item) => item.id.toString()}
                    renderItem={({ item }) => (
                        <FlatListItem onPress={() => onLectureSelect(item)}>
                            <BodyText>
                                {item.schoolClass.subject.name} ({item.type}),
                                {item.schoolClass.name}: {item.room.name} |
                                {item.startTime} ➜ {item.endTime}
                            </BodyText>
                        </FlatListItem>
                    )}
                />
            </FlatListContainer>
        ) : (
            <BodyText>No lectures found</BodyText>
        )}
        <PaginationControls
            currentPage={page}
            hasNext={hasNext}
            onNext={handleNext}
            onPrevious={handlePrevious}
        />
    </Card>
);

// Updated LectureList.tsx
export const LectureList = ({
                                lectures,
                                onLectureSelect,
                                searchQueryRoom,
                                setSearchQueryRoom,
                                rooms,
                                handleRoomSelect,
                                searchQuerySubjects,
                                setSearchQuerySubjects,
                                subjects,
                                handleClassSelect,
                                handleOnSubjectSelect,
                                onGetAllLectures,
                                classes,
                                lectureFilter,
                                setLectureFilter,
                                handleNext,
                                handlePrevious,
                                page
                            }: LectureListProps) => {
    const getWidthPercent = () => {
        switch(lectureFilter) {
            case 'all': return 100;
            case 'class': return 33;
            case 'room': return 50;
            default: return 100;
        }
    };

    return (
        <Container flex={1} gap="md" width={'100%'}>
            <GridRow flex={1} heightPercent={50}>
                <FilterButtons
                    onGetAllLectures={onGetAllLectures}
                    setLectureFilter={setLectureFilter}
                    widthPercent={getWidthPercent()}
                />

                {lectureFilter === 'room' && (
                    <GridColumn widthPercent={50} gap={"md"}>
                        <RoomSearchSection
                            searchQueryRoom={searchQueryRoom}
                            setSearchQueryRoom={setSearchQueryRoom}
                            rooms={rooms}
                            handleRoomSelect={handleRoomSelect}
                        />
                    </GridColumn>
                )}

                {lectureFilter === 'class' && (
                    <>
                    <GridColumn widthPercent={33} gap={"md"}>
                        <SubjectSearchSection
                            searchQuerySubjects={searchQuerySubjects}
                            setSearchQuerySubjects={setSearchQuerySubjects}
                            subjects={subjects}
                            handleOnSubjectSelect={handleOnSubjectSelect}
                        />
                    </GridColumn>
                    <GridColumn widthPercent={33} gap={"md"}>
                        <ClassListSection
                            classes={classes}
                            handleClassSelect={handleClassSelect}
                        />
                    </GridColumn>
                    </>
                )}
            </GridRow>

            <GridRow heightPercent={50} gap={"md"}>
                <LecturesListSection
                    lectures={lectures}
                    onLectureSelect={onLectureSelect}
                    handleNext={handleNext}
                    handlePrevious={handlePrevious}
                    page={page}
                    hasNext={lectures.length >= 5}
                />
            </GridRow>
        </Container>
    );
};

export const WeekdayPicker = ({ selectedValue, onValueChange }: WeekdayPickerProps) => (
    <CenteredContainer gap={'md'}>
    <Subtitle>Weekday</Subtitle>
        <PickerContainer width="200px">
            <StyledPicker
                selectedValue={selectedValue}
                onValueChange={(value) => onValueChange(value as number)}
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
    </CenteredContainer>
);

export const LectureTypePicker = ({ selectedValue, onValueChange }: LectureTypePickerProps) => (
    <CenteredContainer gap={'md'}>
        <Subtitle>Lecture Type</Subtitle>
        <PickerContainer width="200px">
            <StyledPicker
                selectedValue={selectedValue}
                onValueChange={(value) => onValueChange(value as LectureType)}
            >
                <StyledPickerItem label="Theoretical" value="THEORETICAL" />
                <StyledPickerItem label="Practical" value="PRACTICAL" />
                <StyledPickerItem label="Mix" value="THEORETICAL_PRACTICAL" />
            </StyledPicker>
        </PickerContainer>
    </CenteredContainer>
);

export const TimePicker = ({ selectedValue, onValueChange, label, placeholder }: TimePickerProps) => (
    <CenteredContainer gap={'md'}>
        <Subtitle>{label}</Subtitle>
        <Input
            placeholder= {placeholder}
            value={selectedValue}
            onChangeText={(text) => onValueChange(text)}
        />
    </CenteredContainer>
);

export const UpdateLectureForm = ({
    selectedLecture,
    onScheduleChange,
    rooms,
    searchQuery,
    setSearchQuery,
    handleRoomSelect,
    effectiveFromText,
    effectiveUntilText,
    setEffectiveFromText,
    setEffectiveUntilText,
    onSaveSchedule,
    isSaving
}: UpdateLectureFormProps) => (
    <CenteredContainer gap="md" flex={1} >
        <ScrollView contentContainerStyle={{ gap: 16 }}>
            <Subtitle>Update schedule:</Subtitle>
            <WeekdayPicker
                selectedValue={selectedLecture.weekDay}
                onValueChange={(value) => onScheduleChange({ weekDay: value })}
            />
            <LectureTypePicker
                selectedValue={selectedLecture.type}
                onValueChange={(itemValue) => onScheduleChange({ type: itemValue })}
            />
            <RoomSearchSection
                searchQueryRoom={searchQuery}
                setSearchQueryRoom={setSearchQuery}
                rooms={rooms}
                handleRoomSelect={handleRoomSelect}
            />
            <TimePicker
                label="Effective From"
                placeholder="YYYY-MM-DD"
                selectedValue={effectiveFromText}
                onValueChange={setEffectiveFromText}
            />
            <TimePicker
                label="Effective Until"
                placeholder="YYYY-MM-DD"
                selectedValue={effectiveUntilText}
                onValueChange={setEffectiveUntilText}
            />
            <TimePicker
                label="New Start Time"
                placeholder="HH:MM"
                selectedValue={selectedLecture.startTime}
                onValueChange={(itemValue) => onScheduleChange({ startTime: itemValue })}
            />
            <TimePicker
                label="New End Time"
                placeholder="HH:MM"
                selectedValue={selectedLecture.endTime}
                onValueChange={(itemValue) => onScheduleChange({ endTime: itemValue })}
            />
        </ScrollView>
        <Button
            variant="primary"
            onPress={onSaveSchedule}
            disabled={isSaving}
        >
            <ButtonText>{isSaving ? 'Saving...' : 'Save Schedule'}</ButtonText>
        </Button>
    </CenteredContainer>
);
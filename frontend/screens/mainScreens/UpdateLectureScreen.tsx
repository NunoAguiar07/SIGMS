import {FlatList, Text, TextInput, TouchableOpacity, View} from "react-native";
import {scheduleLectureStyles} from "../css_styling/lecture/scheduleLectureStyles";
import {commonStyles} from "../css_styling/common/CommonProps";
import {Picker} from "@react-native-picker/picker";
import {RoomInterface} from "../../types/RoomInterface";
import {Lecture} from "../../types/calendar/Lecture";
import {isMobile} from "../../utils/DeviceType";
import {UpdateLectureProps} from "../css_styling/update_lecture/UpdateLectureProps";
import {Card, Container, GridColumn, GridRow} from "../css_styling/common/NewContainers";
import {Subtitle} from "../css_styling/common/Typography";
import {Button, ButtonText} from "../css_styling/common/Buttons";


export const UpdateLectureScreen = ({
    lectures,
    selectedLecture,
    onLectureSelect,
    onScheduleChange,
    onSaveSchedule,
    isSaving,
    rooms,
    setSearchQuery,
    searchQuery,
    selectedRoom,
    handleRoomSelect,
    setEffectiveFromText,
    setEffectiveUntilText,
    effectiveFromText,
    effectiveUntilText
}: UpdateLectureProps) => {
    const timeOptions = Array.from({ length: 48 }, (_, index) => {
        const hours = String(Math.floor(index / 2)).padStart(2, '0');
        const minutes = index % 2 === 0 ? '00' : '30';
        return `${hours}:${minutes}`;
    });

    if (!isMobile) {
        return (
            <Container flex={1} justifyContent="center" padding="md">
                <GridRow>
                    <GridColumn widthPercent={50} gap={"md"}>
                        <LectureList
                            lectures={lectures}
                            onLectureSelect={onLectureSelect}
                        />
                    </GridColumn>
                    <GridColumn widthPercent={50}>
                        <Card shadow="medium" alignItems={"center"} gap="md">
                            {selectedLecture ? (
                                <UpdateLectureForm
                                    selectedLecture={selectedLecture}
                                    onScheduleChange={onScheduleChange}
                                    rooms={rooms}
                                    searchQuery={searchQuery}
                                    setSearchQuery={setSearchQuery}
                                    selectedRoom={selectedRoom}
                                    handleRoomSelect={handleRoomSelect}
                                    effectiveFromText={effectiveFromText}
                                    effectiveUntilText={effectiveUntilText}
                                    setEffectiveFromText={setEffectiveFromText}
                                    setEffectiveUntilText={setEffectiveUntilText}
                                    timeOptions={timeOptions}
                                    onSaveSchedule={onSaveSchedule}
                                    isSaving={isSaving}
                                />
                            ) : (
                                <Subtitle>Select a lecture to update schedule</Subtitle>
                            )}
                        </Card>
                    </GridColumn>
                </GridRow>
            </Container>
        )
        // return (
        //     <View style={scheduleLectureStyles.container}>
        //         {/* Left Column */}
        //         <View style={commonStyles.leftColumn}>
        //             <LectureList
        //                 lectures={lectures}
        //                 onLectureSelect={onLectureSelect}
        //             />
        //         </View>
        //
        //         {/* Right Column */}
        //         <View style={commonStyles.rightColumn}>
        //             {selectedLecture ? (
        //                 <UpdateLectureForm
        //                     selectedLecture={selectedLecture}
        //                     onScheduleChange={onScheduleChange}
        //                     rooms={rooms}
        //                     searchQuery={searchQuery}
        //                     setSearchQuery={setSearchQuery}
        //                     selectedRoom={selectedRoom}
        //                     handleRoomSelect={handleRoomSelect}
        //                     effectiveFromText={effectiveFromText}
        //                     effectiveUntilText={effectiveUntilText}
        //                     setEffectiveFromText={setEffectiveFromText}
        //                     setEffectiveUntilText={setEffectiveUntilText}
        //                     timeOptions={timeOptions}
        //                     onSaveSchedule={onSaveSchedule}
        //                     isSaving={isSaving}
        //                 />
        //             ) : (
        //                 <Text style={commonStyles.emptyText}>Select a lecture to update schedule</Text>
        //             )}
        //         </View>
        //     </View>
        // );
    } else {
        return(
            <View style={scheduleLectureStyles.container}>
                {!selectedLecture ? (
                    <View style={commonStyles.leftColumn}>
                        <LectureList
                            lectures={lectures}
                            onLectureSelect={onLectureSelect}
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
                            searchQuery={searchQuery}
                            setSearchQuery={setSearchQuery}
                            selectedRoom={selectedRoom}
                            handleRoomSelect={handleRoomSelect}
                            effectiveFromText={effectiveFromText}
                            effectiveUntilText={effectiveUntilText}
                            setEffectiveFromText={setEffectiveFromText}
                            setEffectiveUntilText={setEffectiveUntilText}
                            timeOptions={timeOptions}
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
    // onGetAllLectures: () => void;
    // onGetLecturesByClass: (subjectId: number, classId:number) => void;
    // onGetLecturesByRoom: (roomId: number) => void;
}

interface WeekdayPickerProps {
    selectedValue: number;
    onValueChange: (value: number) => void;
}

interface LectureTypePickerProps {
    selectedValue: string;
    onValueChange: (value: string) => void;
}

interface RoomSearchProps {
    rooms: RoomInterface[];
    searchQuery: string;
    setSearchQuery: (query: string) => void;
    selectedRoom: RoomInterface | null;
    handleRoomSelect: (room: RoomInterface) => void;
}

interface TimePickerProps {
    selectedValue: string;
    onValueChange: (value: string) => void;
    timeOptions: string[];
    label: string;
}

interface EffectiveDateInputProps {
    label: string;
    value: string;
    onChangeText: (text: string) => void;
    placeholder: string;
}

interface SaveButtonProps {
    onPress: () => void;
    isSaving: boolean;
}

interface UpdateLectureFormProps {
    selectedLecture: Lecture;
    onScheduleChange: (changes: Partial<Lecture>) => void;
    rooms: RoomInterface[];
    searchQuery: string;
    setSearchQuery: (query: string) => void;
    selectedRoom: RoomInterface | null;
    handleRoomSelect: (room: RoomInterface) => void;
    effectiveFromText: string;
    effectiveUntilText: string;
    setEffectiveFromText: (text: string) => void;
    setEffectiveUntilText: (text: string) => void;
    timeOptions: string[];
    onSaveSchedule: () => void;
    isSaving: boolean;
}

export const LectureList = ({ lectures, onLectureSelect }: LectureListProps) => (
    // <Card shadow="medium" alignItems={"center"} gap="md">
    //     <GridRow>
    //         <GridColumn widthPercent={33} gap="md">
    //             <Subtitle>All Existing Lectures:</Subtitle>
    //             <Button
    //                 variant="primary"
    //                 onPress={() => onGetAllLectures()}
    //                 style={{ marginBottom: 10 }}
    //             >
    //                 <ButtonText>Get</ButtonText>
    //             </Button>
    //         </GridColumn>
    //         <GridColumn widthPercent={33}>
    //             <Subtitle>Get Lectures By Class:</Subtitle>
    //             <Button
    //                 variant="primary"
    //                 onPress={() => onGetLecturesByClasss()}
    //                 style={{ marginBottom: 10 }}
    //             >
    //                 <ButtonText>Get</ButtonText>
    //             </Button>
    //         </GridColumn>
    //         <GridColumn widthPercent={34}>
    //             <Subtitle>Get Lectures By Room:</Subtitle>
    //             <Button
    //                 variant="primary"
    //                 onPress={() => onGetLecturesByRoom()}
    //                 style={{ marginBottom: 10 }}
    //             >
    //                 <ButtonText>Get</ButtonText>
    //             </Button>
    //         </GridColumn>
    //     </GridRow>
    // </Card>
    <>
        <Text style={commonStyles.sectionTitle}>Existing Lectures</Text>
        <FlatList
            data={lectures}
            keyExtractor={(item) => item.id.toString()}
            renderItem={({ item }) => (
                <TouchableOpacity
                    style={commonStyles.itemSearch}
                    onPress={() => onLectureSelect(item)}
                >
                    <Text style={commonStyles.itemText}>
                        {item.schoolClass.subject.name} ({item.type}), {item.schoolClass.name}: {item.room.name} | {item.startTime} ➜ {item.endTime}
                    </Text>
                </TouchableOpacity>
            )}
            ListEmptyComponent={<Text style={commonStyles.emptyText}>No lectures found</Text>}
        />
    </>
);

export const WeekdayPicker = ({ selectedValue, onValueChange }: WeekdayPickerProps) => (
    <>
        <Text style={commonStyles.inputLabel}>Weekday</Text>
        <View style={commonStyles.pickerContainer}>
            <Picker
                selectedValue={selectedValue}
                onValueChange={onValueChange}
                style={commonStyles.picker}
            >
                {[
                    { label: 'Monday', value: 1 },
                    { label: 'Tuesday', value: 2 },
                    { label: 'Wednesday', value: 3 },
                    { label: 'Thursday', value: 4 },
                    { label: 'Friday', value: 5 },
                    { label: 'Saturday', value: 6 },
                ].map(({ label, value }) => (
                    <Picker.Item key={value} label={label} value={value} />
                ))}
            </Picker>
        </View>
    </>
);

export const LectureTypePicker = ({ selectedValue, onValueChange }: LectureTypePickerProps) => (
    <>
        <Text style={commonStyles.inputLabel}>Lecture Type</Text>
        <View style={commonStyles.pickerContainer}>
            <Picker
                selectedValue={selectedValue}
                onValueChange={onValueChange}
                style={commonStyles.picker}
            >
                <Picker.Item label="Theoretical" value="THEORETICAL" />
                <Picker.Item label="Practical" value="PRACTICAL" />
                <Picker.Item label="Theoretical-Practical" value="THEORETICAL_PRACTICAL" />
            </Picker>
        </View>
    </>
);

export const RoomSearch = ({
    rooms,
    searchQuery,
    setSearchQuery,
    selectedRoom,
    handleRoomSelect
}: RoomSearchProps) => (
    <>
        <Text style={commonStyles.inputLabel}>Room</Text>
        <TextInput
            style={commonStyles.input}
            placeholder="Search rooms..."
            value={searchQuery}
            onChangeText={setSearchQuery}
        />
        <FlatList
            data={rooms}
            keyExtractor={(item) => item.id.toString()}
            renderItem={({ item }) => (
                <TouchableOpacity
                    style={[
                        commonStyles.itemSearch,
                        selectedRoom?.id === item.id && { backgroundColor: '#e5e5e5' }
                    ]}
                    onPress={() => handleRoomSelect(item)}
                >
                    <Text style={commonStyles.itemText}>{item.name}</Text>
                </TouchableOpacity>
            )}
            ListEmptyComponent={<Text style={commonStyles.emptyText}>No rooms found</Text>}
        />
    </>
);

export const TimePicker = ({ selectedValue, onValueChange, timeOptions, label }: TimePickerProps) => (
    <>
        <Text style={commonStyles.inputLabel}>{label}</Text>
        <View style={commonStyles.pickerContainer}>
            <Picker
                selectedValue={selectedValue}
                onValueChange={onValueChange}
                style={commonStyles.picker}
            >
                {timeOptions.map((time) => (
                    <Picker.Item key={time} label={time} value={time} />
                ))}
            </Picker>
        </View>
    </>
);

export const EffectiveDateInput = ({ label, value, onChangeText, placeholder }: EffectiveDateInputProps) => (
    <>
        <Text style={commonStyles.inputLabel}>{label}</Text>
        <TextInput
            style={commonStyles.input}
            placeholder={placeholder}
            value={value}
            onChangeText={onChangeText}
        />
    </>
);

export const SaveButton = ({ onPress, isSaving }: SaveButtonProps) => (
    <Button
        variant="primary"
        onPress={onPress}
        disabled={isSaving}
    >
        <ButtonText>Save</ButtonText>
    </Button>
);

export const UpdateLectureForm = ({
    selectedLecture,
    onScheduleChange,
    rooms,
    searchQuery,
    setSearchQuery,
    selectedRoom,
    handleRoomSelect,
    effectiveFromText,
    effectiveUntilText,
    setEffectiveFromText,
    setEffectiveUntilText,
    timeOptions,
    onSaveSchedule,
    isSaving
}: UpdateLectureFormProps) => (
    <View>
        <Text style={commonStyles.sectionTitle}>
            Update Schedule for {selectedLecture.schoolClass.subject.name}
        </Text>

        <WeekdayPicker
            selectedValue={selectedLecture.weekDay}
            onValueChange={(value) => onScheduleChange({ weekDay: value })}
        />

        <LectureTypePicker
            selectedValue={selectedLecture.type}
            onValueChange={(itemValue) => onScheduleChange({ type: itemValue })}
        />

        <RoomSearch
            rooms={rooms}
            searchQuery={searchQuery}
            setSearchQuery={setSearchQuery}
            selectedRoom={selectedRoom}
            handleRoomSelect={handleRoomSelect}
        />

        <EffectiveDateInput
            label="Effective From"
            value={effectiveFromText}
            onChangeText={setEffectiveFromText}
            placeholder="YYYY-MM-DD"
        />

        <EffectiveDateInput
            label="Effective Until"
            value={effectiveUntilText}
            onChangeText={setEffectiveUntilText}
            placeholder="YYYY-MM-DD"
        />

        <TimePicker
            label="New Start Time"
            selectedValue={selectedLecture.startTime}
            onValueChange={(itemValue) => onScheduleChange({ startTime: itemValue })}
            timeOptions={timeOptions}
        />

        <TimePicker
            label="New End Time"
            selectedValue={selectedLecture.endTime}
            onValueChange={(itemValue) => onScheduleChange({ endTime: itemValue })}
            timeOptions={timeOptions}
        />

        <SaveButton onPress={onSaveSchedule} isSaving={isSaving} />
    </View>
);
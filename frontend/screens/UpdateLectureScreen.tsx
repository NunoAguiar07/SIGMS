import {ActivityIndicator, FlatList, Text, TextInput, TouchableOpacity, View} from "react-native";
import {scheduleLectureStyles} from "../css_styling/lecture/scheduleLectureStyles";
import {commonStyles} from "./css_styling/common/CommonProps";
import {Picker} from "@react-native-picker/picker";
import {RoomInterface} from "../types/RoomInterface";
import {Lecture} from "../types/calendar/Lecture";


interface Props {
    lectures: Lecture[];
    selectedLecture: Lecture | null;
    newSchedule: Lecture | null;
    onLectureSelect: (lecture: Lecture) => void;
    onScheduleChange: (changes: Partial<Lecture>) => void;
    onSaveSchedule: () => void;
    isSaving: boolean;
    rooms: RoomInterface[];
    setSearchQuery: (query: string) => void;
    searchQuery: string;
    selectedRoom: RoomInterface | null;
    handleRoomSelect: (room: RoomInterface) => void;
    effectiveFrom: Date | null;
    setEffectiveFrom: (date: Date) => void;
    effectiveUntil: Date | null;
    setEffectiveUntil: (date: Date) => void;
    setEffectiveFromText: (text: string) => void;
    setEffectiveUntilText: (text: string) => void;
    effectiveFromText: string;
    effectiveUntilText: string;
}

export const UpdateLectureScreen = ({
                                        lectures,
                                        selectedLecture,
                                        newSchedule,
                                        onLectureSelect,
                                        onScheduleChange,
                                        onSaveSchedule,
                                        isSaving,
                                        rooms,
                                        setSearchQuery,
                                        searchQuery,
                                        selectedRoom,
                                        handleRoomSelect,
                                        effectiveFrom,
                                        setEffectiveFrom,
                                        effectiveUntil,
                                        setEffectiveUntil,
                                        setEffectiveFromText,
                                        setEffectiveUntilText,
                                        effectiveFromText,
                                        effectiveUntilText

                                    }: Props) => {
    const sortedLectures = [...lectures].sort((a, b) =>
        a.schoolClass.subject.name.localeCompare(b.schoolClass.subject.name)
    );

    const timeOptions = Array.from({ length: 48 }, (_, index) => {
        const hours = String(Math.floor(index / 2)).padStart(2, '0');
        const minutes = index % 2 === 0 ? '00' : '30';
        return `${hours}:${minutes}`;
    });



    return (
        <View style={scheduleLectureStyles.container}>
            {/* Left Column */}
            <View style={commonStyles.leftColumn}>
                <Text style={commonStyles.sectionTitle}>Existing Lectures</Text>
                <FlatList
                    data={sortedLectures}
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
            </View>

            {/* Right Column */}
            <View style={commonStyles.rightColumn}>
                {selectedLecture ? (
                    <View>
                        <Text style={commonStyles.sectionTitle}>
                            Update Schedule for {selectedLecture.schoolClass.subject.name}
                        </Text>

                        {/* Weekday */}
                        <Text style={commonStyles.inputLabel}>Weekday</Text>
                        <Picker
                            selectedValue={selectedLecture.weekDay}
                            onValueChange={(value) => onScheduleChange({ weekDay: value })}
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

                        {/* Lecture Type */}
                        <Text style={commonStyles.inputLabel}>Lecture Type</Text>
                        <Picker
                            selectedValue={selectedLecture.type}
                            onValueChange={(itemValue) => onScheduleChange({ type: itemValue })}
                            style={commonStyles.picker}
                        >
                            <Picker.Item label="Theoretical" value="THEORETICAL" />
                            <Picker.Item label="Practical" value="PRACTICAL" />
                            <Picker.Item label="Theoretical-Practical" value="THEORETICAL_PRACTICAL" />
                        </Picker>

                        {/* Room Search */}
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

                        {/* Effective Dates */}
                        <Text style={commonStyles.inputLabel}>Effective From</Text>
                        <TextInput
                            style={commonStyles.input}
                            placeholder="YYYY-MM-DD"
                            value={effectiveFromText}
                            onChangeText={setEffectiveFromText}
                        />

                        <Text style={commonStyles.inputLabel}>Effective Until</Text>
                        <TextInput
                            style={commonStyles.input}
                            placeholder="YYYY-MM-DD"
                            value={effectiveUntilText}
                            onChangeText={setEffectiveUntilText}
                        />

                        {/* Time Pickers */}
                        <Text style={commonStyles.inputLabel}>New Start Time</Text>
                        <Picker
                            selectedValue={selectedLecture.startTime}
                            onValueChange={(itemValue) => onScheduleChange({ startTime: itemValue })}
                            style={commonStyles.picker}
                        >
                            {timeOptions.map((time) => (
                                <Picker.Item key={time} label={time} value={time} />
                            ))}
                        </Picker>

                        <Text style={commonStyles.inputLabel}>New End Time</Text>
                        <Picker
                            selectedValue={selectedLecture.endTime}
                            onValueChange={(itemValue) => onScheduleChange({ endTime: itemValue })}
                            style={commonStyles.picker}
                        >
                            {timeOptions.map((time) => (
                                <Picker.Item key={time} label={time} value={time} />
                            ))}
                        </Picker>

                        {/* Save Button */}
                        <TouchableOpacity style={commonStyles.actionButton_2} onPress={onSaveSchedule}>
                            {isSaving ? (
                                <ActivityIndicator size="small" color="#fff" />
                            ) : (
                                <Text style={commonStyles.buttonText}>Save Schedule</Text>
                            )}
                        </TouchableOpacity>
                    </View>
                ) : (
                    <Text style={commonStyles.emptyText}>Select a lecture to update schedule</Text>
                )}
            </View>
        </View>
    );
};
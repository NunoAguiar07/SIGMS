import {TextInput, TouchableOpacity, View, Text, ActivityIndicator, FlatList} from "react-native";
import {commonStyles} from "../css_styling/common/CommonProps";
import {generateEntitiesStyles} from "../css_styling/generateEntities/GenerateEntitiesProps";
import {Picker} from "@react-native-picker/picker";
import {EntityCreationScreenType} from "../types/EntityCreationScreenType";
import {Ionicons} from "@expo/vector-icons";
import {FormCreateEntityValues} from "../../types/welcome/FormCreateEntityValues";
import {SubjectInterface} from "../../types/SubjectInterface";
import {SchoolClassInterface} from "../../types/SchoolClassInterface";
import {RoomInterface} from "../../types/RoomInterface";
import {isMobile} from "../../utils/DeviceType";

export const AdminEntityCreationScreen = ({
    selectedEntity,
    onEntitySelect,
    formValues,
    setFormValues,
    onSubmit,
    subjects,
    subjectClasses,
    rooms,
    searchQuery,
    setSearchQuery,
    isLoading,
}: EntityCreationScreenType) => {
    const renderForm = () => {
        switch (selectedEntity) {
            case 'subject':
                return <SubjectForm formValues={formValues} setFormValues={setFormValues} />;
            case 'class':
                return (
                    <ClassForm
                        formValues={formValues}
                        setFormValues={setFormValues}
                        searchQuery={searchQuery}
                        setSearchQuery={setSearchQuery}
                        isLoading={isLoading}
                        subjects={subjects}
                    />
                );
            case 'room':
                return <RoomForm formValues={formValues} setFormValues={setFormValues} />;
            case 'lecture':
                return (
                    <LectureForm
                        formValues={formValues}
                        setFormValues={setFormValues}
                        subjects={subjects}
                        subjectClasses={subjectClasses}
                        rooms={rooms}
                        searchQuery={searchQuery}
                        setSearchQuery={setSearchQuery}
                        isLoading={isLoading}
                    />
                );
            default:
                return (
                    <View style={generateEntitiesStyles.placeholder}>
                        <Text style={generateEntitiesStyles.placeholderText}>
                            Select an entity type to add
                        </Text>
                    </View>
                );
        }
    };

    if (!isMobile) {
        return (
            <View style={commonStyles.columnsContainer}>
                <View style={[commonStyles.leftColumn, commonStyles.centerContainer]}>
                    {['subject', 'class', 'room', 'lecture'].map((entity) => (
                        <TouchableOpacity
                            key={entity}
                            style={[
                                generateEntitiesStyles.selectButton,
                                selectedEntity === entity && generateEntitiesStyles.activeButton,
                            ]}
                            onPress={() => onEntitySelect(entity as any)}
                        >
                            <Text style={commonStyles.buttonText}>
                                {entity.charAt(0).toUpperCase() + entity.slice(1)}
                            </Text>
                        </TouchableOpacity>
                    ))}
                </View>

                <View style={commonStyles.rightColumn}>
                    {renderForm()}
                    <View style={generateEntitiesStyles.footer}>
                        {selectedEntity && (
                            <TouchableOpacity
                                style={generateEntitiesStyles.selectButton}
                                onPress={onSubmit}
                            >
                                <Text style={commonStyles.buttonText}>Create {selectedEntity}</Text>
                            </TouchableOpacity>
                        )}
                    </View>
                </View>
            </View>
        );
    } else {
        return (
            <View style={commonStyles.columnsContainer}>
                {!selectedEntity ? (
                    <View style={commonStyles.centerContainer}>
                        {['subject', 'class', 'room', 'lecture'].map((entity) => (
                            <TouchableOpacity
                                key={entity}
                                style={generateEntitiesStyles.selectButton}
                                onPress={() => onEntitySelect(entity as any)}
                            >
                                <Text style={commonStyles.buttonText}>
                                    {entity.charAt(0).toUpperCase() + entity.slice(1)}
                                </Text>
                            </TouchableOpacity>
                        ))}
                    </View>
                ) : (
                    <View style={commonStyles.rightColumn}>
                        {renderForm()}
                        <View style={generateEntitiesStyles.footer}>
                            <TouchableOpacity
                                style={generateEntitiesStyles.selectButton}
                                onPress={onSubmit}
                            >
                                <Text style={commonStyles.buttonText}>Create {selectedEntity}</Text>
                            </TouchableOpacity>
                        </View>
                    </View>
                )}
            </View>
        );
    }
};

type SubjectFromType = {
    formValues: FormCreateEntityValues;
    setFormValues: (values: FormCreateEntityValues) => void;
};

export const SubjectForm = ({ formValues, setFormValues }: SubjectFromType) => (
    <View style={generateEntitiesStyles.formSection}>
        <Text style={commonStyles.sectionTitle}>Create New Subject</Text>
        <TextInput
            placeholder="Subject Name"
            style={generateEntitiesStyles.createInput}
            value={formValues.name || ''}
            onChangeText={(text) => setFormValues({ ...formValues, name: text })}
        />
    </View>
);

type ClassFormType = {
    formValues: FormCreateEntityValues;
    setFormValues: (values: FormCreateEntityValues) => void;
    searchQuery: string;
    setSearchQuery: (query: string) => void;
    isLoading: boolean;
    subjects: SubjectInterface[];
};

export const ClassForm = ({ formValues, setFormValues, searchQuery, setSearchQuery, isLoading, subjects }: ClassFormType) => (
    <View style={generateEntitiesStyles.formSection}>
        <Text style={commonStyles.sectionTitle}>Create New Class</Text>
        <TextInput
            placeholder="Class Name"
            style={generateEntitiesStyles.createInput}
            value={formValues.name || ''}
            onChangeText={(text) => setFormValues({ ...formValues, name: text })}
        />

        <Text style={commonStyles.sectionTitle}>Select Subject:</Text>
        <TextInput
            placeholder="Search subjects..."
            style={commonStyles.searchInput}
            value={searchQuery}
            onChangeText={setSearchQuery}
        />

        {isLoading ? (
            <ActivityIndicator size="small" style={generateEntitiesStyles.loader} />
        ) : (
            <FlatList
                data={subjects}
                keyExtractor={(item) => item.id.toString()}
                style={generateEntitiesStyles.list}
                renderItem={({ item }) => (
                    <TouchableOpacity
                        style={[
                            generateEntitiesStyles.listItem,
                            formValues.subjectId === item.id && generateEntitiesStyles.selectedItem,
                        ]}
                        onPress={() => setFormValues({ ...formValues, subjectId: item.id })}
                    >
                        <Text style={generateEntitiesStyles.listItemText}>{item.name}</Text>
                    </TouchableOpacity>
                )}
                ListEmptyComponent={
                    <Text style={generateEntitiesStyles.emptyText}>
                        {searchQuery ? 'No subjects found' : 'No subjects available'}
                    </Text>
                }
            />
        )}
    </View>
);

type RoomFormType = {
    formValues: FormCreateEntityValues;
    setFormValues: (values: FormCreateEntityValues) => void;
};

export const RoomForm = ({ formValues, setFormValues }: RoomFormType) => (
    <View style={generateEntitiesStyles.formSection}>
        <Text style={commonStyles.sectionTitle}>Create New Room</Text>

        <TextInput
            placeholder="Room Name"
            style={generateEntitiesStyles.createInput}
            value={formValues.name || ''}
            onChangeText={(text) => setFormValues({ ...formValues, name: text })}
        />

        <TextInput
            placeholder="Capacity"
            keyboardType="numeric"
            style={generateEntitiesStyles.createInput}
            value={formValues.capacity?.toString() || ''}
            onChangeText={(text) => setFormValues({
                ...formValues,
                capacity: text ? parseInt(text) : null,
            })}
        />

        <View style={generateEntitiesStyles.pickerContainer}>
            <Text style={commonStyles.sectionTitle}>Room Type:</Text>
            <Picker
                selectedValue={formValues.roomType}
                onValueChange={(value) => setFormValues({ ...formValues, roomType: value })}
                style={generateEntitiesStyles.picker}
            >
                <Picker.Item label="Classroom" value="CLASS" />
                <Picker.Item label="Study Room" value="STUDY" />
                <Picker.Item label="Office Room" value="OFFICE" />
            </Picker>
        </View>
    </View>
);


type LectureFormType = {
    formValues: FormCreateEntityValues;
    setFormValues: (values: FormCreateEntityValues) => void;
    subjects: SubjectInterface[];
    subjectClasses: SchoolClassInterface[];
    rooms: RoomInterface[];
    searchQuery: string;
    setSearchQuery: (query: string) => void;
    isLoading: boolean;
};

export const LectureForm = ({
    formValues,
    setFormValues,
    subjects,
    subjectClasses,
    rooms,
    searchQuery,
    setSearchQuery,
    isLoading
}: LectureFormType) => {
    // Data for our main FlatList
    const renderFormSections = () => {
        if (!formValues.subjectId) {
            return (
                <LectureSubjectSelection
                    subjects={subjects}
                    searchQuery={searchQuery}
                    setSearchQuery={setSearchQuery}
                    isLoading={isLoading}
                    formValues={formValues}
                    setFormValues={setFormValues}
                />
            );
        }

        return (
            <>
                <LectureSelectedSubject
                    subjects={subjects}
                    formValues={formValues}
                    setFormValues={setFormValues}
                />

                <LectureClassSelection
                    subjectClasses={subjectClasses}
                    formValues={formValues}
                    setFormValues={setFormValues}
                />

                <LectureRoomSelection
                    rooms={rooms}
                    searchQuery={searchQuery}
                    setSearchQuery={setSearchQuery}
                    formValues={formValues}
                    setFormValues={setFormValues}
                />

                {isMobile ? (
                    <LectureDetailsSelection
                        formValues={formValues}
                        setFormValues={setFormValues}
                    />
                ) : (
                    <View style={commonStyles.rightColumn}>
                        <LectureDetailsSelection
                            formValues={formValues}
                            setFormValues={setFormValues}
                        />
                    </View>
                )}
            </>
        );
    };

    return (
        <View style={generateEntitiesStyles.formSection}>
            <Text style={commonStyles.sectionTitle}>Create New Lecture</Text>

            <FlatList
                data={[1]} // Single item to render our form
                keyExtractor={() => 'lecture-form'}
                renderItem={() => renderFormSections()}
                contentContainerStyle={generateEntitiesStyles.scrollContainer}
                ListHeaderComponent={<Text style={commonStyles.sectionTitle}>Create New Lecture</Text>}
                keyboardShouldPersistTaps="handled"
            />
        </View>
    );
};

const LectureSubjectSelection = ({
    subjects,
    searchQuery,
    setSearchQuery,
    isLoading,
    formValues,
    setFormValues,
}: {
    subjects: SubjectInterface[];
    searchQuery: string;
    setSearchQuery: (query: string) => void;
    isLoading: boolean;
    formValues: FormCreateEntityValues;
    setFormValues: (values: FormCreateEntityValues) => void;
}) => (
    <>
        <Text style={commonStyles.sectionTitle}>Select Subject:</Text>
        <TextInput
            placeholder="Search subjects..."
            style={generateEntitiesStyles.createInput}
            value={searchQuery}
            onChangeText={setSearchQuery}
        />

        {isLoading ? (
            <ActivityIndicator size="small" style={generateEntitiesStyles.loader} />
        ) : (
            <FlatList
                data={subjects}
                keyExtractor={(item) => item.id.toString()}
                style={generateEntitiesStyles.list}
                renderItem={({ item }) => (
                    <TouchableOpacity
                        style={[
                            generateEntitiesStyles.listItem,
                            formValues.subjectId === item.id && generateEntitiesStyles.selectedItem
                        ]}
                        onPress={() => {
                            setFormValues({ ...formValues, subjectId: item.id, schoolClassId: null });
                            setSearchQuery('');
                        }}
                    >
                        <Text style={generateEntitiesStyles.listItemText}>{item.name}</Text>
                    </TouchableOpacity>
                )}
                ListEmptyComponent={
                    <Text style={generateEntitiesStyles.emptyText}>
                        {searchQuery ? 'No subjects found' : 'Search for subjects'}
                    </Text>
                }
            />
        )}
    </>
);

const LectureSelectedSubject = ({
    subjects,
    formValues,
    setFormValues,
}: {
    subjects: SubjectInterface[];
    formValues: FormCreateEntityValues;
    setFormValues: (values: FormCreateEntityValues) => void;
}) => (
    <>
        <Text style={commonStyles.sectionTitle}>Selected Subject:</Text>
        <View style={commonStyles.containerRow}>
            <Text style={commonStyles.itemText}>
                {subjects.find((s) => s.id === formValues.subjectId)?.name}
            </Text>
            <TouchableOpacity
                style={[commonStyles.actionButton, commonStyles.editButton]}
                onPress={() => setFormValues({ ...formValues, subjectId: null, schoolClassId: null })}
            >
                <Ionicons name="arrow-undo-circle-outline" size={16} color="white" />
            </TouchableOpacity>
        </View>
    </>
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
    <>
        <Text style={commonStyles.sectionTitle}>Select Class:</Text>
        <FlatList
            data={subjectClasses}
            keyExtractor={(item) => item.id.toString()}
            style={generateEntitiesStyles.list}
            renderItem={({ item }) => (
                <TouchableOpacity
                    style={[
                        generateEntitiesStyles.listItem,
                        formValues.schoolClassId === item.id && generateEntitiesStyles.selectedItem
                    ]}
                    onPress={() => setFormValues({ ...formValues, schoolClassId: item.id })}
                >
                    <Text style={generateEntitiesStyles.listItemText}>{item.name}</Text>
                </TouchableOpacity>
            )}
            ListEmptyComponent={
                <Text style={generateEntitiesStyles.emptyText}>
                    {subjectClasses.length === 0 ? 'No classes found for this subject' : 'Select a class'}
                </Text>
            }
        />
    </>
);

const LectureRoomSelection = ({
    rooms,
    searchQuery,
    setSearchQuery,
    formValues,
    setFormValues
}: {
    rooms: RoomInterface[];
    searchQuery: string;
    setSearchQuery: (query: string) => void;
    formValues: FormCreateEntityValues;
    setFormValues: (values: FormCreateEntityValues) => void;
}) => (
    <>
        <Text style={commonStyles.sectionTitle}>Select Room:</Text>
        <TextInput
            placeholder="Search rooms..."
            style={commonStyles.searchInput}
            value={searchQuery}
            onChangeText={setSearchQuery}
        />
        <FlatList
            data={rooms}
            keyExtractor={(item) => item.id.toString()}
            style={generateEntitiesStyles.list}
            renderItem={({ item }) => (
                <TouchableOpacity
                    style={[
                        generateEntitiesStyles.listItem,
                        formValues.roomId === item.id && generateEntitiesStyles.selectedItem
                    ]}
                    onPress={() => setFormValues({ ...formValues, roomId: item.id })}
                >
                    <Text style={generateEntitiesStyles.listItemText}>{item.name}</Text>
                </TouchableOpacity>
            )}
            ListEmptyComponent={
                <Text style={generateEntitiesStyles.emptyText}>
                    {searchQuery ? 'No rooms found' : 'Search for rooms'}
                </Text>
            }
        />
    </>
);

const LectureDetailsSelection = ({
    formValues,
    setFormValues
}: {
    formValues: FormCreateEntityValues;
    setFormValues: (values: FormCreateEntityValues) => void;
}) => (
    <>
        <View style={generateEntitiesStyles.pickerContainer}>
            <Text style={commonStyles.sectionTitle}>Lecture Type:</Text>
            <Picker
                selectedValue={formValues.lectureType}
                onValueChange={(value) => setFormValues({ ...formValues, lectureType: value })}
                style={generateEntitiesStyles.picker}
            >
                <Picker.Item label="Theorical" value="THEORETICAL" />
                <Picker.Item label="Practical" value="PRACTICAL" />
                <Picker.Item label="Mix" value="THEORETICAL_PRACTICAL" />
            </Picker>
        </View>

        <View style={generateEntitiesStyles.pickerContainer}>
            <Text style={commonStyles.sectionTitle}>Weekday:</Text>
            <Picker
                selectedValue={formValues.weekDay}
                onValueChange={(value) => setFormValues({ ...formValues, weekDay: value })}
                style={generateEntitiesStyles.picker}
            >
                <Picker.Item label="Monday" value={1} />
                <Picker.Item label="Tuesday" value={2} />
                <Picker.Item label="Wednesday" value={3} />
                <Picker.Item label="Thursday" value={4} />
                <Picker.Item label="Friday" value={5} />
                <Picker.Item label="Saturday" value={6} />
                <Picker.Item label="Sunday" value={7} />
            </Picker>
        </View>

        <View style={generateEntitiesStyles.timeContainer}>
            <Text style={commonStyles.sectionTitle}>Start Time:</Text>
            <TextInput
                placeholder="HH:MM"
                style={generateEntitiesStyles.timeInput}
                value={formValues.startTime || ''}
                onChangeText={(text) => setFormValues({ ...formValues, startTime: text })}
            />

            <Text style={commonStyles.sectionTitle}>End Time:</Text>
            <TextInput
                placeholder="HH:MM"
                style={generateEntitiesStyles.timeInput}
                value={formValues.endTime || ''}
                onChangeText={(text) => setFormValues({ ...formValues, endTime: text })}
            />
        </View>
    </>
);
import {TextInput, TouchableOpacity, View, Text, ActivityIndicator, FlatList} from "react-native";
import {commonStyles} from "../../css_styling/common/CommonProps";
import {generateEntitiesStyles} from "../../css_styling/generateEntities/GenerateEntitiesProps";
import {Picker} from "@react-native-picker/picker";
import {EntityCreationScreenType, EntityType} from "../types/EntityCreationScreenType";
import {Ionicons} from "@expo/vector-icons";

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
                return (
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
            case 'class':
                return (
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
                                            formValues.subjectId === item.id && generateEntitiesStyles.selectedItem
                                        ]}
                                        onPress={() => setFormValues({
                                            ...formValues,
                                            subjectId: item.id
                                        })}
                                    >
                                        <Text style={generateEntitiesStyles.listItemText}>
                                            {item.name}
                                        </Text>
                                    </TouchableOpacity>
                                )}
                                ListEmptyComponent={
                                    <Text style={generateEntitiesStyles.emptyText}>
                                        {searchQuery ? "No subjects found" : "No subjects available"}
                                    </Text>
                                }
                            />
                        )}
                    </View>
                );
            case 'room':
                return (
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
                                capacity: text ? parseInt(text) : null
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
            case 'lecture':
                return (
                    <View style={generateEntitiesStyles.formSection}>
                        <Text style={commonStyles.sectionTitle}>Create New Lecture</Text>
                        {/* Step 1: Subject Selection (only if not selected yet) */}
                        {!formValues.subjectId ? (
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
                                                    setFormValues({
                                                        ...formValues,
                                                        subjectId: item.id,
                                                        schoolClassId: null // Reset class selection
                                                    });
                                                    setSearchQuery(''); // Clear search query
                                                }}
                                            >
                                                <Text style={generateEntitiesStyles.listItemText}>
                                                    {item.name}
                                                </Text>
                                            </TouchableOpacity>
                                        )}
                                        ListEmptyComponent={
                                            <Text style={generateEntitiesStyles.emptyText}>
                                                {searchQuery ? "No subjects found" : "Search for subjects"}
                                            </Text>
                                        }
                                    />
                                )}
                            </>
                        ) : (
                            <>
                                {/* Show selected subject with option to change */}
                                <Text style={commonStyles.sectionTitle}>Selected Subject:</Text>
                                <View style={commonStyles.containerRow}>
                                    <Text style={commonStyles.itemText}>
                                        {subjects.find(s => s.id === formValues.subjectId)?.name}
                                    </Text>
                                    <TouchableOpacity
                                        style={[commonStyles.actionButton, commonStyles.editButton]}
                                        onPress={() => {
                                            setFormValues({
                                                ...formValues,
                                                subjectId: null,
                                                schoolClassId: null
                                            });
                                        }}
                                    >
                                        <Ionicons name={'arrow-undo-circle-outline'} size={16} color="white"></Ionicons>
                                    </TouchableOpacity>
                                </View>

                                {/* Step 2: Class Selection */}
                                <View style={commonStyles.containerRow}>
                                    <View style={[commonStyles.leftColumn]}>
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
                                                        onPress={() => setFormValues({
                                                            ...formValues,
                                                            schoolClassId: item.id
                                                        })}
                                                    >
                                                        <Text style={generateEntitiesStyles.listItemText}>
                                                            {item.name}
                                                        </Text>
                                                    </TouchableOpacity>
                                                )}
                                                ListEmptyComponent={
                                                    <Text style={generateEntitiesStyles.emptyText}>
                                                        {subjectClasses.length === 0 ? "No classes found for this subject" : "Select a class"}
                                                    </Text>
                                                }
                                            />
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
                                                    onPress={() => setFormValues({
                                                        ...formValues,
                                                        roomId: item.id
                                                    })}
                                                >
                                                    <Text style={generateEntitiesStyles.listItemText}>
                                                        {item.name}
                                                    </Text>
                                                </TouchableOpacity>
                                            )}
                                            ListEmptyComponent={
                                                <Text style={generateEntitiesStyles.emptyText}>
                                                    {searchQuery ? "No rooms found" : "Search for rooms"}
                                                </Text>
                                            }
                                        />
                                    </View>
                                    <View style={commonStyles.rightColumn}>
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
                                    </View>
                                </View>
                            </>
                        )}
                    </View>
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


    return (
        <View style={commonStyles.columnsContainer}>
            {/* Left Column - Entity Selection */}
            <View style={[commonStyles.leftColumn, commonStyles.centerContainer]}>
                {['subject', 'class', 'room', 'lecture'].map((entity) => (
                    <TouchableOpacity
                        key={entity}
                        style={[
                            generateEntitiesStyles.selectButton,
                            selectedEntity === entity && generateEntitiesStyles.activeButton
                        ]}
                        onPress={() => onEntitySelect(entity as EntityType)}
                    >
                        <Text style={commonStyles.buttonText}>
                            {entity.charAt(0).toUpperCase() + entity.slice(1)}
                        </Text>
                    </TouchableOpacity>
                ))}
            </View>

            {/* Right Column - Form */}
            <View style={commonStyles.rightColumn}>
                {renderForm()}

                {/* Always visible submit button at bottom */}
                <View style={generateEntitiesStyles.footer}>
                    {selectedEntity && (
                        <TouchableOpacity
                            style={generateEntitiesStyles.selectButton}
                            onPress={onSubmit}
                        >
                            <Text style={commonStyles.buttonText}>
                                Create {selectedEntity}
                            </Text>
                        </TouchableOpacity>
                    )}
                </View>
            </View>
        </View>
    );
};
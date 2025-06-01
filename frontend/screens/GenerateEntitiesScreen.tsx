import {TextInput, TouchableOpacity, View, Text, ActivityIndicator, FlatList} from "react-native";
import {commonStyles} from "../css_styling/common/CommonProps";
import {generateEntitiesStyles} from "../css_styling/generateEntities/GenerateEntitiesProps";
import {Picker} from "@react-native-picker/picker";

// @ts-ignore
export const AdminEntityCreationScreen = ({selectedEntity, onEntitySelect, formValues, setFormValues, onSubmit, subjects, searchQuery, setSearchQuery, isLoading}) => {
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
                                selectedValue={formValues.type}
                                onValueChange={(value) => setFormValues({ ...formValues, type: value })}
                                style={generateEntitiesStyles.picker}
                            >
                                <Picker.Item label="Classroom" value="CLASS" />
                                <Picker.Item label="Study Room" value="STUDY" />
                                <Picker.Item label="Office Room" value="OFFICE" />
                            </Picker>
                        </View>
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
                {['subject', 'class', 'room'].map((entity) => (
                    <TouchableOpacity
                        key={entity}
                        style={[
                            generateEntitiesStyles.selectButton,
                            selectedEntity === entity && generateEntitiesStyles.activeButton
                        ]}
                        onPress={() => onEntitySelect(entity)}
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
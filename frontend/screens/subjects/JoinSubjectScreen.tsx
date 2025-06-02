import {ActivityIndicator, FlatList, Text, TextInput, TouchableOpacity, View} from "react-native";
import {commonStyles} from "../../css_styling/common/CommonProps";
import {joinClassStyles} from "../../css_styling/join_class/JoinClassProps";
import {JoinSubjectScreenType} from "../types/JoinSubjectScreenType";

export const JoinSubjectScreen = ({
                                      subjects,
                                      schoolClasses,
                                      selectedSubject,
                                      searchQuery,
                                      onSearchChange,
                                      onSubjectSelect,
                                      onJoinClass
}: JoinSubjectScreenType) => {
    return (
        <View style={joinClassStyles.joinClassContainer}>
            <View style={commonStyles.leftColumn}>
                <TextInput
                    style={commonStyles.searchSubjectInput}
                    placeholder="Search subjects..."
                    value={searchQuery}
                    onChangeText={onSearchChange}
                />
                <FlatList
                    data={subjects}
                    keyExtractor={(item) => item.id.toString()}
                    renderItem={({ item }) => (
                        <TouchableOpacity
                            style={commonStyles.itemSearch}
                            onPress={() => onSubjectSelect(item)}
                        >
                            <Text style={commonStyles.itemText}>{item.name}</Text>
                        </TouchableOpacity>
                    )}
                    ListEmptyComponent={
                        <Text style={commonStyles.emptyText}>No subjects found</Text>
                    }
                />
            </View>
            <View style={commonStyles.rightColumn}>
                {selectedSubject && (
                    <View style={joinClassStyles.classesSection}>
                        <Text style={commonStyles.sectionTitle}>
                            Classes for {selectedSubject.name}
                        </Text>
                        {schoolClasses.length <= 0 ? (
                            <ActivityIndicator size="small" />
                        ) : (
                            <FlatList
                                data={schoolClasses}
                                keyExtractor={(item) => item.id.toString()}
                                renderItem={({ item }) => (
                                    <View style={joinClassStyles.classItem}>
                                        <Text style={commonStyles.itemText}>{item.name}</Text>
                                        <TouchableOpacity
                                            style={joinClassStyles.joinClassButton}
                                            onPress={() => onJoinClass(selectedSubject.id, item.id)}
                                        >
                                            <Text style={joinClassStyles.joinButtonText}>Join Class</Text>
                                        </TouchableOpacity>
                                    </View>
                                )}
                                ListEmptyComponent={
                                    <Text style={commonStyles.emptyText}>No classes available</Text>
                                }
                            />
                        )}
                    </View>
                )}
            </View>
        </View>
    );
};
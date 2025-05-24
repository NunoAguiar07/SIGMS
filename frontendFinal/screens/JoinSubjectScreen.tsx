import {ActivityIndicator, FlatList, Text, TextInput, TouchableOpacity, View} from "react-native";
import {styles} from "../css_styling/profile/RectangleProps";


// @ts-ignore
export const JoinSubjectScreen = ({subjects,schoolClasses,selectedSubject,searchQuery,onSearchChange,onSubjectSelect,onJoinClass}) => {
    return (
        <View style={styles.joinClassContainer}>
            {/* Left Column - Search and Subjects */}
            <View style={styles.leftColumn}>
                <TextInput
                    style={styles.searchInput}
                    placeholder="Search subjects..."
                    value={searchQuery}
                    onChangeText={onSearchChange}
                />
                <FlatList
                    data={subjects}
                    keyExtractor={(item) => item.id.toString()}
                    renderItem={({ item }) => (
                        <TouchableOpacity
                            style={styles.itemSearch}
                            onPress={() => onSubjectSelect(item)}
                        >
                            <Text style={styles.itemText}>{item.name}</Text>
                        </TouchableOpacity>
                    )}
                    ListEmptyComponent={
                        <Text style={styles.emptyText}>No subjects found</Text>
                    }
                />
            </View>

            {/* Right Column - Classes */}
            <View style={styles.rightColumn}>
                {selectedSubject && (
                    <View style={styles.classesSection}>
                        <Text style={styles.sectionTitle}>
                            Classes for {selectedSubject.name}
                        </Text>
                        {schoolClasses.length <= 0 ? (
                            <ActivityIndicator size="small" />
                        ) : (
                            <FlatList
                                data={schoolClasses}
                                keyExtractor={(item) => item.id.toString()}
                                renderItem={({ item }) => (
                                    <View style={styles.classItem}>
                                        <Text style={styles.itemText}>{item.name}</Text>
                                        <TouchableOpacity
                                            style={styles.joinClassButton}
                                            onPress={() => onJoinClass(selectedSubject.id, item.id)}
                                        >
                                            <Text style={styles.joinButtonText}>Join Class</Text>
                                        </TouchableOpacity>
                                    </View>
                                )}
                                ListEmptyComponent={
                                    <Text style={styles.emptyText}>No classes available</Text>
                                }
                            />
                        )}
                    </View>
                )}
            </View>
        </View>
    );
};
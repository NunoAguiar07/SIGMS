import {ActivityIndicator, FlatList, Text, TextInput, TouchableOpacity, View} from "react-native";
import {commonStyles} from "../css_styling/common/CommonProps";
import {joinClassStyles} from "../css_styling/join_class/JoinClassProps";
import {JoinSubjectScreenType} from "../types/JoinSubjectScreenType";
import {SubjectInterface} from "../../types/SubjectInterface";
import {SchoolClassInterface} from "../../types/SchoolClassInterface";
import {isMobile} from "../../utils/DeviceType";

export const JoinSubjectScreen = ({
    subjects,
    schoolClasses,
    selectedSubject,
    searchQuery,
    onSearchChange,
    onSubjectSelect,
    onJoinClass,
    onLeaveClass,
    userClasses
}: JoinSubjectScreenType) => {
    if (!isMobile) {
        return (
            <View style={joinClassStyles.joinClassContainer}>
                <View style={commonStyles.leftColumn}>
                    <SubjectSearchList
                        subjects={subjects}
                        searchQuery={searchQuery}
                        onSearchChange={onSearchChange}
                        onSubjectSelect={onSubjectSelect}
                    />
                </View>
                <View style={commonStyles.rightColumn}>
                    <UserClassList userClasses={userClasses} onLeaveClass={onLeaveClass} />
                    {selectedSubject && (
                        <SelectedSubjectClasses
                            subject={selectedSubject}
                            schoolClasses={schoolClasses}
                            onJoinClass={onJoinClass}
                        />
                    )}
                </View>
            </View>
        );
    } else {
        return (
            <View style={joinClassStyles.joinClassContainer}>
                {!selectedSubject ? (
                    <View style={commonStyles.leftColumn}>
                        <SubjectSearchList
                            subjects={subjects}
                            searchQuery={searchQuery}
                            onSearchChange={onSearchChange}
                            onSubjectSelect={onSubjectSelect}
                        />
                        <UserClassList userClasses={userClasses} onLeaveClass={onLeaveClass} />
                    </View>
                ) : (
                    <View style={commonStyles.rightColumn}>
                        <View style={{ flex: 1 }}>
                            <SelectedSubjectClasses
                                subject={selectedSubject}
                                schoolClasses={schoolClasses}
                                onJoinClass={onJoinClass}
                            />
                        </View>
                    </View>
                )}
            </View>
        );
    }
};


interface SubjectSearchListType {
    subjects: SubjectInterface[];
    searchQuery: string;
    onSearchChange: (text: string) => void;
    onSubjectSelect: (subject: SubjectInterface) => void;
}

export const SubjectSearchList = ({
    subjects,
    searchQuery,
    onSearchChange,
    onSubjectSelect
}: SubjectSearchListType) => (
    <View>
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
            ListEmptyComponent={<Text style={commonStyles.emptyText}>No subjects found</Text>}
        />
    </View>
);

interface UserClassListType {
    userClasses: SchoolClassInterface[];
    onLeaveClass: (subjectId: number, classId: number) => void;
}

export const UserClassList = ({ userClasses, onLeaveClass }: UserClassListType) => (
    <View style={{ flex: 1, width: '100%' }}>
        <Text style={commonStyles.sectionTitle}>Your Classes</Text>
        {userClasses.length > 0 ? (
            userClasses.map((cls) => (
                <View key={cls.id} style={joinClassStyles.classItem}>
                    <Text>{cls.subject.name} : {cls.name}</Text>
                    <TouchableOpacity
                        style={joinClassStyles.joinClassButton}
                        onPress={() => onLeaveClass(cls.subject.id, cls.id)}
                    >
                        <Text style={joinClassStyles.joinButtonText}>Leave</Text>
                    </TouchableOpacity>
                </View>
            ))
        ) : (
            <Text style={commonStyles.emptyText}>You haven't joined any classes yet.</Text>
        )}
    </View>
);

interface SelectedSubjectClassesType {
    subject: SubjectInterface;
    schoolClasses: SchoolClassInterface[];
    onJoinClass: (subjectId: number, classId: number) => void;
}

export const SelectedSubjectClasses = ({
    subject,
    schoolClasses,
    onJoinClass
}: SelectedSubjectClassesType) => (
    <View style={joinClassStyles.classesSection}>
        <Text style={commonStyles.sectionTitle}>
            Classes for {subject.name}
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
                            onPress={() => onJoinClass(subject.id, item.id)}
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
);
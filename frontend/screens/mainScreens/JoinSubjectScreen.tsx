import {FlatList} from "react-native";
import {JoinSubjectScreenType} from "../types/JoinSubjectScreenType";
import {SubjectInterface} from "../../types/SubjectInterface";
import {SchoolClassInterface} from "../../types/SchoolClassInterface";
import {isMobile} from "../../utils/DeviceType";
import {Card, CenteredContainer, GridColumn, GridRow} from "../css_styling/common/NewContainers";
import {SearchInput} from "../css_styling/common/Inputs";
import {FlatListContainer, FlatListDisplayItem, FlatListItem} from "../css_styling/common/FlatList";
import {Button, ButtonText} from "../css_styling/common/Buttons";
import {BodyText, Subtitle} from "../css_styling/common/Typography";

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
            <CenteredContainer flex={1} justifyContent="center" padding="md">
                <GridRow>
                    <GridColumn widthPercent={50} gap={"md"}>
                        <SubjectSearchList
                            subjects={subjects}
                            searchQuery={searchQuery}
                            onSearchChange={onSearchChange}
                            onSubjectSelect={onSubjectSelect}
                        />
                    </GridColumn>
                    <GridColumn widthPercent={50} gap={"md"}>
                        <GridRow heightPercent={50} gap={"md"}>
                            <UserClassList userClasses={userClasses} onLeaveClass={onLeaveClass} />
                        </GridRow>
                        <GridRow heightPercent={50} gap={"md"}>
                            {selectedSubject && (
                                <SelectedSubjectClasses
                                    subject={selectedSubject}
                                    schoolClasses={schoolClasses}
                                    onJoinClass={onJoinClass}
                                />
                            )}
                        </GridRow>
                    </GridColumn>
                </GridRow>
            </CenteredContainer>
        );
    } else {
        return (
            <CenteredContainer flex={1} padding="md">
                {!selectedSubject ? (
                    <SubjectSearchList
                        subjects={subjects}
                        searchQuery={searchQuery}
                        onSearchChange={onSearchChange}
                        onSubjectSelect={onSubjectSelect}
                    />
                ) : (
                    <SelectedSubjectClasses
                        subject={selectedSubject}
                        schoolClasses={schoolClasses}
                        onJoinClass={onJoinClass}
                    />
                )}
                <UserClassList userClasses={userClasses} onLeaveClass={onLeaveClass} />
            </CenteredContainer>
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
    <CenteredContainer style={{ position: 'relative' , zIndex: 10}} >
        <SearchInput
            placeholder="Search subjects..."
            value={searchQuery}
            onChangeText={onSearchChange}
        />
        {subjects.length > 0 && (
            <FlatListContainer>
                <FlatList
                    data={subjects}
                    keyExtractor={(item) => item.id.toString()}
                    renderItem={({ item }) => (
                        <FlatListItem
                            onPress={() => onSubjectSelect(item)}
                        >
                            <BodyText>{item.name}</BodyText>
                        </FlatListItem>
                    )}
                />
            </FlatListContainer>
        )}
    </CenteredContainer>
);

interface UserClassListType {
    userClasses: SchoolClassInterface[];
    onLeaveClass: (subjectId: number, classId: number) => void;
}

export const UserClassList = ({ userClasses, onLeaveClass }: UserClassListType) => (
    <CenteredContainer gap={"md"}>
        <Card shadow="medium" padding="md" gap={"md"}>
            <Subtitle>Your Classes</Subtitle>
            {userClasses.length > 0 ? (
                <FlatListContainer position={"static"}>
                    <FlatList
                        data={userClasses}
                        keyExtractor={(item) => item.id.toString()}
                        renderItem={({ item }) => (
                            <FlatListDisplayItem>
                                <BodyText>{item.subject.name} : {item.name}</BodyText>
                                <Button
                                    variant="primary"
                                    size="small"
                                    onPress={() => onLeaveClass(item.subject.id, item.id)}
                                >
                                    <ButtonText>Leave</ButtonText>
                                </Button>
                            </FlatListDisplayItem>
                        )}
                    />
                </FlatListContainer>
            ) : (
                <BodyText>You haven't joined any classes yet.</BodyText>
            )}
        </Card>
    </CenteredContainer>
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
    <CenteredContainer gap={"md"}>
        <Card shadow="medium" padding="md" gap={"md"}>
            <Subtitle>Classes for {subject.name}</Subtitle>
            {schoolClasses.length <= 0 ? (
                <BodyText>No classes for this subject yet</BodyText>
            ) : (
                <FlatListContainer position={"static"}>
                    <FlatList
                        data={schoolClasses}
                        keyExtractor={(item) => item.id.toString()}
                        renderItem={({ item }) => (
                            <FlatListDisplayItem>
                                <BodyText>{item.name}</BodyText>
                                <Button
                                    variant="primary"
                                    size="small"
                                    onPress={() => onJoinClass(subject.id, item.id)}
                                >
                                    <ButtonText>Join</ButtonText>
                                </Button>
                            </FlatListDisplayItem>
                        )}
                    />
                </FlatListContainer>
            )}
        </Card>
    </CenteredContainer>
);
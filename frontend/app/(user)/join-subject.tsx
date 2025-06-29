import ErrorHandler from "../(public)/error";
import {JoinSubjectScreen} from "../../screens/mainScreens/JoinSubjectScreen";
import {useJoinSubject} from "../../hooks/useJoinSubject";
import LoadingScreen from "../../screens/auxScreens/LoadingScreen";
import {BackgroundImage} from "../../screens/components/BackgroundImage";

const JoinSubject = () => {
    const {
        subjects,
        schoolClasses,
        selectedSubject,
        searchQuery,
        loadingClasses,
        initialLoading,
        error,
        onSearchChange,
        onSubjectSelect,
        onJoinClass,
        onLeaveClass,
        userClasses
    } = useJoinSubject();

    if (error) return <ErrorHandler errorStatus={error.status} errorMessage={error.message} />;
    if (initialLoading) return <LoadingScreen/>;

    return (
        <BackgroundImage>
            <JoinSubjectScreen
                subjects={subjects}
                schoolClasses={schoolClasses}
                selectedSubject={selectedSubject}
                searchQuery={searchQuery}
                onSearchChange={onSearchChange}
                onSubjectSelect={onSubjectSelect}
                onJoinClass={onJoinClass}
                onLeaveClass={onLeaveClass}
                loadingClasses={loadingClasses}
                userClasses={userClasses}
            />
        </BackgroundImage>
    );
};

export default JoinSubject;
import ErrorHandler from "../(public)/error";
import {JoinSubjectScreen} from "../../screens/subjects/JoinSubjectScreen";
import {useJoinSubject} from "../../hooks/useJoinSubject";

const JoinSubject = () => {
    const {
        subjects,
        schoolClasses,
        selectedSubject,
        searchQuery,
        loadingClasses,
        error,
        onSearchChange,
        onSubjectSelect,
        onJoinClass,
    } = useJoinSubject();

    if (error) return <ErrorHandler errorStatus={error.status} errorMessage={error.message} />;

    return (
        <JoinSubjectScreen
            subjects={subjects}
            schoolClasses={schoolClasses}
            selectedSubject={selectedSubject}
            searchQuery={searchQuery}
            onSearchChange={onSearchChange}
            onSubjectSelect={onSubjectSelect}
            onJoinClass={onJoinClass}
            loadingClasses={loadingClasses}
        />
    );
};

export default JoinSubject;
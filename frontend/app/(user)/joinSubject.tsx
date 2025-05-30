import {useEffect, useState} from "react";
import {SubjectInterface} from "../../interfaces/SubjectInterface";
import {SchoolClassInterface} from "../../interfaces/SchoolClassInterface";
import {ErrorInterface} from "../../interfaces/ErrorInterface";
import ErrorHandler from "../(public)/error";
import {SubjectsRequest} from "../../requests/authorized/SubjectsRequest";
import {JoinSubjectScreen} from "../../screens/JoinSubjectScreen";
import {useDebounce} from 'use-debounce';
import {SchoolClassRequest} from "../../requests/authorized/SchoolClassRequest";
import {JoinSchoolClassRequest} from "../../requests/authorized/JoinSchoolClassRequest";
import {Alert} from "react-native";


const joinSubject = () => {
    const [subjects, setSubjects] = useState<SubjectInterface[]>([]);
    const [selectedSubject, setSelectedSubject] = useState<SubjectInterface | null>(null);
    const [schoolClasses, setSchoolClasses] = useState<SchoolClassInterface[]>([]);
    const [searchQuery, setSearchQuery] = useState('');
    const [debouncedSearchQuery] = useDebounce(searchQuery, 500); // Debounce the search query for 500ms
    const [error, setError] = useState<ErrorInterface | null>(null);

    useEffect(() => {
        // Only run the search if there's actually a query
        if (debouncedSearchQuery.trim().length > 0) {
            const loadSubjects = SubjectsRequest(debouncedSearchQuery, setSubjects, setError);
            loadSubjects();
        } else {
            // Clear results when search is empty
            setSubjects([]);
            setSelectedSubject(null);
            setSchoolClasses([]);
        }
    }, [debouncedSearchQuery]);

    const handleSubjectSelect = (subject: SubjectInterface) => {
        setSelectedSubject(subject);
        const loadClasses = SchoolClassRequest(
            subject.id,
            setSchoolClasses,
            setError
        );
        loadClasses()
    };

    const handleJoinClass = async (subjectId: number, classId: number) => {
        console.log(subjectId, classId)
        const join = JoinSchoolClassRequest(subjectId, classId, setError);
        const success = await join();
        if (success) {
            Alert.alert('Success', 'You have joined the class successfully!');
            setSelectedSubject(null);
            setSchoolClasses([]);
        } else {
            Alert.alert('Error', 'Failed to join the class. Please try again.');
        }
    };

    // Handle errors
    if (error) return <ErrorHandler errorStatus={error.status} errorMessage={error.message} />;
    return <JoinSubjectScreen
            subjects={subjects}
            schoolClasses={schoolClasses}
            selectedSubject={selectedSubject}
            searchQuery={searchQuery}
            onSearchChange={setSearchQuery}
            onSubjectSelect={handleSubjectSelect}
            onJoinClass={handleJoinClass}
        />

}

export default joinSubject;
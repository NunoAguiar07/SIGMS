import {useEffect, useState} from "react";
import {SubjectInterface} from "../../interfaces/SubjectInterface";
import {SchoolClassInterface} from "../../interfaces/SchoolClassInterface";
import {ErrorInterface} from "../../interfaces/ErrorInterface";
import ErrorHandler from "../(public)/error";
import {SubjectsRequest} from "../../requests/authorized/SubjectsRequest";
import {JoinSubjectScreen} from "../../screens/JoinSubjectScreen";
import {useDebounce} from 'use-debounce';
import {SchoolClassRequest} from "../../requests/authorized/SchoolClassRequest";


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

    // Handle errors
    if (error) return <ErrorHandler errorStatus={error.status} errorMessage={error.message} />;
    return <JoinSubjectScreen
            subjects={subjects}
            schoolClasses={schoolClasses}
            selectedSubject={selectedSubject}
            searchQuery={searchQuery}
            onSearchChange={setSearchQuery}
            onSubjectSelect={handleSubjectSelect}
        />

}

export default joinSubject;
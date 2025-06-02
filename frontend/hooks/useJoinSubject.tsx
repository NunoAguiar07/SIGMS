import {useEffect, useState} from "react";
import {SubjectInterface} from "../types/SubjectInterface";
import {SchoolClassInterface} from "../types/SchoolClassInterface";
import {useDebounce} from "use-debounce";
import {ParsedError} from "../types/errors/ParseErrorTypes";
import {Alert} from "react-native";
import {fetchSubjects} from "../services/authorized/fetchSubjects";
import {fetchSubjectClasses} from "../services/authorized/fetchSubjectClasses";
import {joinClass} from "../services/authorized/requestJoinClass";


export const useJoinSubject = () => {
    const [subjects, setSubjects] = useState<SubjectInterface[]>([]);
    const [schoolClasses, setSchoolClasses] = useState<SchoolClassInterface[]>([]);
    const [selectedSubject, setSelectedSubject] = useState<SubjectInterface | null>(null);
    const [searchQuery, setSearchQuery] = useState<string>("");
    const [debouncedSearchQuery] = useDebounce(searchQuery, 500);
    const [loadingClasses, setLoadingClasses] = useState<boolean>(false);
    const [error, setError] = useState<ParsedError | null>(null);

    useEffect(() => {
        if (debouncedSearchQuery.trim().length > 0) {
            fetchSubjects(debouncedSearchQuery)
                .then(setSubjects)
                .catch((err) => setError(err as ParsedError));
        } else {
            setSubjects([]);
            setSelectedSubject(null);
            setSchoolClasses([]);
        }
    }, [debouncedSearchQuery]);

    const onSubjectSelect = async (subject: SubjectInterface) => {
        setSelectedSubject(subject);
        setLoadingClasses(true);
        try {
            const classes = await fetchSubjectClasses(subject.id);
            setSchoolClasses(classes);
        } catch (err) {
            setError(err as ParsedError);
        } finally {
            setLoadingClasses(false);
        }
    };

    const onJoinClass = async (subjectId: number, classId: number) => {
        try {
            const success = await joinClass(subjectId, classId);
            if (success) {
                Alert.alert("Success", "You have joined the class successfully!");
                setSelectedSubject(null);
                setSchoolClasses([]);
            } else {
                Alert.alert("Error", "Failed to join the class. Please try again.");
            }
        } catch (err) {
            setError(err as ParsedError);
            Alert.alert("Error", "Something went wrong while joining the class.");
        }
    };

    return {
        subjects,
        schoolClasses,
        selectedSubject,
        searchQuery,
        loadingClasses,
        error,
        onSearchChange: setSearchQuery,
        onSubjectSelect,
        onJoinClass,
    };
};
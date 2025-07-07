import {useEffect, useState} from "react";
import {SubjectInterface} from "../types/SubjectInterface";
import {SchoolClassInterface} from "../types/SchoolClassInterface";
import {useDebounce} from "use-debounce";
import {ParsedError} from "../types/errors/ParseErrorTypes";
import {Alert} from "react-native";
import {fetchSubjects} from "../services/authorized/FetchSubjects";
import {fetchSubjectClasses} from "../services/authorized/FetchSubjectClasses";
import {joinClass} from "../services/authorized/RequestJoinClass";
import {fetchUserClasses} from "../services/authorized/FetchUserClasses";
import {leaveClass} from "../services/authorized/RequestLeaveClass";


export const useJoinSubject = () => {
    const [subjects, setSubjects] = useState<SubjectInterface[]>([]);
    const [schoolClasses, setSchoolClasses] = useState<SchoolClassInterface[]>([]);
    const [selectedSubject, setSelectedSubject] = useState<SubjectInterface | null>(null);
    const [searchQuery, setSearchQuery] = useState<string>("");
    const [debouncedSearchQuery] = useDebounce(searchQuery, 500);
    const [loadingClasses, setLoadingClasses] = useState<boolean>(false);
    const [error, setError] = useState<ParsedError | null>(null);
    const [userClasses, setUserClasses] = useState<SchoolClassInterface[]>([]);
    const [initialLoading, setInitialLoading] = useState<boolean>(true);

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

    useEffect(() => {
        fetchAndUpdateUserClasses();
    }, []);

    const fetchAndUpdateUserClasses = async () => {
        try {
            setInitialLoading(true);
            const updatedClasses = await fetchUserClasses();
            setUserClasses(updatedClasses);
            setInitialLoading(false);
        } catch (err) {
            setError(err as ParsedError);
        }
    };

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
                setSelectedSubject(null);
                setSchoolClasses([]);
                await fetchAndUpdateUserClasses();
                Alert.alert("Success", "You have joined the class successfully!");
            } else {
                Alert.alert("Error", "Failed to join the class. Please try again.");
            }
        } catch (err) {
            setError(err as ParsedError);
            Alert.alert("Error", "Something went wrong while joining the class.");
        }
    };

    const onLeaveClass = async (subjectId: number, classId: number) =>  {
         try {
            const success = await leaveClass(subjectId, classId)
            if (success) {
                await fetchAndUpdateUserClasses();
                Alert.alert("Success", "You have left the class successfully!")
            } else {
                Alert.alert("Error", "Failed to leave the class. Please try again.")
            }
         } catch (err) {
            setError(err as ParsedError);
            Alert.alert("Error", "Something went wrong while leaving the class.");
         }

    }

    return {
        subjects,
        schoolClasses,
        selectedSubject,
        searchQuery,
        loadingClasses,
        initialLoading,
        error,
        onSearchChange: setSearchQuery,
        onSubjectSelect,
        onJoinClass,
        onLeaveClass,
        userClasses,
    };
};
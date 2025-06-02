import {useDebounce} from "use-debounce";
import {useCallback, useEffect, useState} from "react";
import {EntityType} from "../screens/types/EntityCreationScreenType";
import {ParsedError} from "../types/errors/ParseErrorTypes";
import {FormCreateEntityValues} from "../types/welcome/FormCreateEntityValues";
import {SubjectInterface} from "../types/SubjectInterface";
import {fetchSubjects} from "../services/authorized/fetchSubjects";
import {createSubject} from "../services/authorized/CreateSubjectRequest";
import {createClass} from "../services/authorized/CreateClassRequest";
import {createRoom} from "../services/authorized/CreateRoomSubject";
import {Alert} from "react-native";
import {SchoolClassInterface} from "../types/SchoolClassInterface";
import {fetchSubjectClasses} from "../services/authorized/fetchSubjectClasses";
import {RoomInterface} from "../types/RoomInterface";
import {fetchRooms} from "../services/authorized/fetchRooms";
import {createLecture} from "../services/authorized/CreateLecture";


export const useAdminEntityCreation = () => {
    const [selectedEntity, setSelectedEntity] = useState<EntityType>(null);
    const [formValues, setFormValues] = useState<FormCreateEntityValues>({});
    const [error, setError] = useState<ParsedError | null>(null);
    const [subjects, setSubjects] = useState<SubjectInterface[]>([]);
    const [subjectClasses, setSubjectClasses] = useState<SchoolClassInterface[]>([]);
    const [rooms, setRooms] = useState<RoomInterface[]>([]);
    const [searchQuery, setSearchQuery] = useState('');
    const [debouncedSearchQuery] = useDebounce(searchQuery, 500);
    const [isLoading, setIsLoading] = useState(false);

    const loadSubjects = useCallback(async () => {
        setIsLoading(true);
        try {
            const data = await fetchSubjects(debouncedSearchQuery);
            setSubjects(data);
        } catch (err) {
            setError(err as ParsedError);
        } finally {
            setIsLoading(false);
        }
    }, [debouncedSearchQuery]);

    const loadSubjectClasses = useCallback(async (subjectId: number) => {
        setIsLoading(true);
        try {
            const data = await fetchSubjectClasses(subjectId);
            setSubjectClasses(data);
        } catch (err) {
            setError(err as ParsedError);
        } finally {
            setIsLoading(false);
        }
    }, []);

    const loadRooms = useCallback(async () => {
        setIsLoading(true);
        try {
            const data = await fetchRooms(debouncedSearchQuery);
            setRooms(data);
        } catch (err) {
            setError(err as ParsedError);
        } finally {
            setIsLoading(false);
        }
    }, [debouncedSearchQuery]);

    useEffect(() => {
        if (!selectedEntity) return;

        if (selectedEntity === 'lecture') {
            if (formValues.subjectId) {
                // If we have a subject selected, don't load anything here
                return;
            }
            // If no subject selected, load subjects when searching
            if (debouncedSearchQuery.trim().length > 0) {
                loadSubjects();
            }
        } else if (selectedEntity === 'class' && debouncedSearchQuery.trim().length > 0) {
            loadSubjects();
        }
    }, [selectedEntity, debouncedSearchQuery, loadSubjects, formValues.subjectId]);

    useEffect(() => {
        if (selectedEntity === 'lecture' && formValues.subjectId) {
            loadSubjectClasses(formValues.subjectId);
        }
    }, [selectedEntity, formValues.subjectId, loadSubjectClasses]);

    useEffect(() => {
        if (selectedEntity === 'lecture' && debouncedSearchQuery.trim().length > 0) {
            loadRooms();
        }
    }, [selectedEntity, debouncedSearchQuery, loadRooms])

    const handleSubmit = async () => {
        if (!selectedEntity) return;
        setIsLoading(true);
        try {
            let success = false;
            switch (selectedEntity) {
                case 'subject':
                    success = await createSubject(formValues.name || '');
                    break;
                case 'class':
                    if (formValues.subjectId) {
                        success = await createClass(formValues.name || '', formValues.subjectId);
                    }
                    break;
                case 'room':
                    if (formValues.name && formValues.capacity && formValues.roomType) {
                        success = await createRoom(
                            formValues.name,
                            formValues.capacity,
                            formValues.roomType
                        );
                    }
                    break;
                case 'lecture':
                    if (formValues.schoolClassId && formValues.roomId && formValues.lectureType &&
                        formValues.weekDay && formValues.startTime && formValues.endTime) {
                        success = await createLecture(
                            formValues.schoolClassId,
                            formValues.roomId,
                            formValues.lectureType,
                            formValues.weekDay,
                            formValues.startTime,
                            formValues.endTime
                        );
                    }
                    break;
            }

            if (success) {
                Alert.alert('Success', `Successfully created ${selectedEntity}`);
                setFormValues({});
                setSearchQuery('');
                setSubjects([]);
                setSubjectClasses([]);
                setRooms([]);
                setSelectedEntity(null);
            } else {
                Alert.alert('Error', `Failed to create ${selectedEntity}`);
            }
        } catch (err) {
            setError(err as ParsedError);
            Alert.alert('Error', `Failed to create ${selectedEntity}`);
        } finally {
            setIsLoading(false);
        }
    };

    return {
        selectedEntity,
        setSelectedEntity,
        formValues,
        setFormValues,
        error,
        subjects,
        subjectClasses,
        rooms,
        searchQuery,
        setSearchQuery,
        isLoading,
        handleSubmit,
    };
};
import {useDebounce} from "use-debounce";
import {useEffect, useState} from "react";
import {EntityType} from "../screens/types/EntityCreationScreenType";
import {ParsedError} from "../types/errors/ParseErrorTypes";
import {FormCreateEntityValues} from "../types/welcome/FormCreateEntityValues";
import {SubjectInterface} from "../types/SubjectInterface";
import {fetchSubjects} from "../services/authorized/FetchSubjects";
import {createSubject} from "../services/authorized/CreateSubjectRequest";
import {createClass} from "../services/authorized/CreateClassRequest";
import {createRoom} from "../services/authorized/CreateRoomSubject";
import {SchoolClassInterface} from "../types/SchoolClassInterface";
import {fetchSubjectClasses} from "../services/authorized/FetchSubjectClasses";
import {RoomInterface} from "../types/RoomInterface";
import {fetchRooms} from "../services/authorized/FetchRooms";
import {createLecture} from "../services/authorized/CreateLecture";
import {useAlert} from "./notifications/useAlert";


export const useAdminEntityCreation = () => {
    const [skipSearch, setSkipSearch] = useState(false);
    const [selectedEntity, setSelectedEntity] = useState<EntityType>(null);
    const [formValues, setFormValues] = useState<FormCreateEntityValues>({});
    const [error, setError] = useState<ParsedError | null>(null);
    const [subjects, setSubjects] = useState<SubjectInterface[]>([]);
    const [subjectClasses, setSubjectClasses] = useState<SchoolClassInterface[]>([]);
    const [rooms, setRooms] = useState<RoomInterface[]>([]);
    const [searchQuerySubjects, setSearchQuerySubjects] = useState('');
    const [searchQueryRooms, setSearchQueryRooms] = useState('');
    const [debouncedSearchQuerySubjects] = useDebounce(searchQuerySubjects, 500);
    const [debouncedSearchQueryRooms] = useDebounce(searchQueryRooms, 500);
    const [isLoading, setIsLoading] = useState(false);

    const showAlert = useAlert()

    const handleItemSelect = (item : any) => {
        setSkipSearch(true)
        if(selectedEntity === 'Class' || (selectedEntity === 'Lecture' && !formValues.subjectId) ) {
            setSearchQuerySubjects(item.name)
            setFormValues(prev => ({ ...prev, subjectId: item.id})); // Update form values
            setSubjects([])
        } else if( selectedEntity === 'Lecture' && formValues.subjectId) {
            setSearchQueryRooms(item.name)
            setFormValues(prev => ({ ...prev, roomId: item.id})); // Update form values
            setRooms([])
        }
    };

    const loadSubjects = async () => {
        try {
            const data = await fetchSubjects(debouncedSearchQuerySubjects);
            setSubjects(data);
        } catch (err) {
            setError(err as ParsedError);
        }
    }

    const loadSubjectClasses = async (subjectId: number) => {
        try {
            const data = await fetchSubjectClasses(subjectId);
            setSubjectClasses(data);
        } catch (err) {
            setError(err as ParsedError);
        }
    }

    const loadRooms = async () => {
        try {
            const data = await fetchRooms(debouncedSearchQueryRooms, 'CLASS');
            setRooms(data);
        } catch (err) {
            setError(err as ParsedError);
        }
    }

    useEffect(() => {
        if (skipSearch) {
            setSkipSearch(false);
            return;
        }
        if (debouncedSearchQuerySubjects.trim().length > 0 ) {
            loadSubjects();
        }
        if (debouncedSearchQueryRooms.trim().length > 0 ) {
            loadRooms()
        }
    }, [debouncedSearchQuerySubjects, debouncedSearchQueryRooms]);

    useEffect(() => {
        if(selectedEntity === 'Lecture' && formValues.subjectId) {
            loadSubjectClasses(formValues.subjectId);
        }
    }, [formValues.subjectId]);

    useEffect(() => {
        switch (selectedEntity) {
            case 'Lecture':
                if (formValues.subjectId) {
                    setSearchQueryRooms('')
                    setRooms([]);
                } else {
                    setFormValues({ ...formValues, subjectId: null, schoolClassId: null })
                    setSearchQuerySubjects('')
                    setSubjects([]);
                }
                break;
            default:
                setFormValues({ ...formValues, subjectId: null, schoolClassId: null })
                setSearchQuerySubjects('')
                setSearchQueryRooms('')
                setSubjects([]);
                setSubjectClasses([]);
                setRooms([]);
        }
    }, [selectedEntity]);

    const handleEntitySelect = (entityType: EntityType) => {
        switch (selectedEntity) {
            case 'Lecture':
                if (formValues.subjectId) {
                    setFormValues({ ...formValues, subjectId: null, schoolClassId: null })
                    setSearchQueryRooms('')
                    setRooms([]);
                } else {
                    setFormValues({ ...formValues, subjectId: null, schoolClassId: null })
                    setSearchQuerySubjects('')
                    setSubjects([]);
                }
                break;
            default:
                setFormValues({ ...formValues, subjectId: null, schoolClassId: null })
                setSearchQuerySubjects('')
                setSearchQueryRooms('')
                setSubjects([]);
                setSubjectClasses([]);
                setRooms([]);
        }
    }

    const handleSubmit = async () => {
        if (!selectedEntity) return;
        setIsLoading(true);
        try {
            let success = false;
            switch (selectedEntity) {
                case 'Subject':
                    success = await createSubject(formValues.name || '');
                    break;
                case 'Class':
                    if (formValues.subjectId) {
                        success = await createClass(formValues.name || '', formValues.subjectId);
                    }
                    break;
                case 'Room':
                    if (formValues.name && formValues.capacity && formValues.roomType) {
                        success = await createRoom(
                            formValues.name,
                            formValues.capacity,
                            formValues.roomType
                        );
                    }
                    break;
                case 'Lecture':
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
                showAlert('Success', `Successfully created ${selectedEntity}`);
                setFormValues({});
                setSearchQuerySubjects('');
                setSearchQueryRooms('');
                setSubjects([]);
                setSubjectClasses([]);
                setRooms([]);
                setSelectedEntity(null);
            } else {
                showAlert('Error', `Failed to create ${selectedEntity}`);
            }
        } catch (err) {
            setError(err as ParsedError);
        } finally {
            setIsLoading(false);
        }
    };

    return {
        selectedEntity,
        setSelectedEntity,
        formValues,
        handleEntitySelect,
        setFormValues,
        error,
        subjects,
        subjectClasses,
        rooms,
        searchQuerySubjects,
        setSearchQuerySubjects,
        searchQueryRooms,
        setSearchQueryRooms,
        isLoading,
        handleSubmit,
        handleItemSelect
    };
};
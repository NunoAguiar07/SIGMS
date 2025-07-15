import {ParsedError} from "../types/errors/ParseErrorTypes";
import {useEffect, useState} from "react";
import {SubjectInterface} from "../types/SubjectInterface";
import {SchoolClassInterface} from "../types/SchoolClassInterface";
import {useDebounce} from "use-debounce";
import {RoomInterface} from "../types/RoomInterface";
import {Lecture} from "../types/calendar/Lecture";
import {useAlert} from "./notifications/useAlert";
import {fetchSubjects} from "../services/authorized/FetchSubjects";
import {fetchRooms} from "../services/authorized/FetchRooms";
import {fetchSubjectClasses} from "../services/authorized/FetchSubjectClasses";
import {fetchLectures} from "../services/authorized/FetchLectures";
import {fetchLecturesByClass} from "../services/authorized/FetchLecturesByClass";
import {fetchLecturesByRoom} from "../services/authorized/FetchLecturesByRoom";
import {deleteSubject} from "../services/authorized/DeleteSubject";
import {deleteClass} from "../services/authorized/DeleteClass";
import {deleteRoom} from "../services/authorized/DeleteRoom";
import {deleteLecture} from "../services/authorized/DeleteLecture";
import {EntityType} from "../screens/types/EntityCreationScreenType";

export const useAdminEntityDeletion = () => {
    // State for entity selection and search
    const [selectedEntity, setSelectedEntity] = useState<EntityType | null>(null);
    const [error, setError] = useState<ParsedError | null>(null);
    const [isLoading, setIsLoading] = useState(false);

    // Subject/Class search states (reused from useJoinSubject)
    const [subjects, setSubjects] = useState<SubjectInterface[]>([]);
    const [subjectClasses, setSubjectClasses] = useState<SchoolClassInterface[]>([]);
    const [selectedSubject, setSelectedSubject] = useState<SubjectInterface | null>(null);
    const [selectedClass, setSelectedClass] = useState<SchoolClassInterface | null>(null);
    const [searchQuerySubjects, setSearchQuerySubjects] = useState('');
    const [debouncedSearchQuerySubjects] = useDebounce(searchQuerySubjects, 500);

    // Room search states (reused from useCreateReport)
    const [rooms, setRooms] = useState<RoomInterface[]>([]);
    const [selectedRoom, setSelectedRoom] = useState<RoomInterface | null>(null);
    const [searchQueryRooms, setSearchQueryRooms] = useState('');
    const [debouncedSearchQueryRooms] = useDebounce(searchQueryRooms, 500);

    // Lecture search states (reused from useUpdateLecture)
    const [lectures, setLectures] = useState<Lecture[]>([]);
    const [selectedLecture, setSelectedLecture] = useState<Lecture | null>(null);
    const [lectureFilter, setLectureFilter] = useState<'all' | 'class' | 'room'>('all');

    const [skipSearch, setSkipSearch] = useState(false);

    const showAlert = useAlert();

    // Load subjects when search query changes
    useEffect(() => {
        if (skipSearch) {
            setSkipSearch(false);
            return;
        }
        if (debouncedSearchQuerySubjects.trim().length > 0) {
            fetchSubjects(debouncedSearchQuerySubjects)
                .then(setSubjects)
                .catch(err => setError(err as ParsedError));
        } else {
            setSubjects([]);
        }
    }, [debouncedSearchQuerySubjects]);

    // Load rooms when search query changes
    useEffect(() => {
        if (skipSearch) {
            setSkipSearch(false);
            return;
        }
        if (debouncedSearchQueryRooms.trim().length > 0) {
            fetchRooms(debouncedSearchQueryRooms
                , selectedEntity === 'Room' ? 'all' : 'Class')
                .then(setRooms)
                .catch(err => setError(err as ParsedError));
        } else {
            setRooms([]);
        }
    }, [debouncedSearchQueryRooms]);

    // Load classes when subject is selected
    useEffect(() => {
        if (selectedSubject) {
            fetchSubjectClasses(selectedSubject.id)
                .then(setSubjectClasses)
                .catch(err => setError(err as ParsedError));
        }
    }, [selectedSubject]);

    // Load lectures based on current filter
    useEffect(() => {
        if (selectedEntity !== 'Lecture') return;

        const loadLectures = async () => {
            try {
                if (lectureFilter === 'all') {
                    const data = await fetchLectures();
                    console.log(data);
                    setLectures(data);
                } else if (lectureFilter === 'class' && selectedClass && selectedSubject) {
                    const data = await fetchLecturesByClass(selectedSubject.id, selectedClass.id);
                    console.log(data);
                    setLectures(data);
                } else if (lectureFilter === 'room' && selectedRoom) {
                    const data = await fetchLecturesByRoom(selectedRoom.id);
                    console.log(data);
                    setLectures(data);
                }
            } catch (err) {
                setError(err as ParsedError);
            }
        };

        loadLectures();
    }, [lectureFilter, selectedClass, selectedRoom, selectedEntity]);

    // Handle subject selection
    const handleSubjectSelect = (subject: SubjectInterface) => {
        setSkipSearch(true);
        setSelectedSubject(subject);
        setSelectedClass(null);
        setSearchQuerySubjects(subject.name);
        setSubjects([]);
    };

    // Handle class selection
    const handleClassSelect = (schoolClass: SchoolClassInterface) => {
        setSelectedClass(schoolClass);
        if (selectedEntity === 'Lecture') {
            setLectureFilter('class');
        }
    };

    // Handle room selection
    const handleRoomSelect = (room: RoomInterface) => {
        setSkipSearch(true);
        setSelectedRoom(room);
        setSearchQueryRooms(room.name);
        setRooms([]);
        if (selectedEntity === 'Lecture') {
            setLectureFilter('room');
        }
    };

    // Handle lecture selection
    const handleLectureSelect = (lecture: Lecture) => {
        setSelectedLecture(lecture);
        setSelectedRoom(lecture.room);
    };

    // Reset all selections when entity type changes
    const handleEntitySelect = (entityType: EntityType) => {
        setSelectedEntity(entityType);
        setSelectedSubject(null);
        setSelectedClass(null);
        setSelectedRoom(null);
        setSelectedLecture(null);
        setSearchQuerySubjects('');
        setSearchQueryRooms('');
        setSubjects([]);
        setSubjectClasses([]);
        setRooms([]);
        setLectures([]);
    };

    // Main deletion function
    const handleDelete = async () => {
        if (!selectedEntity) return;

        setIsLoading(true);
        try {
            let success = false;

            switch (selectedEntity) {
                case 'Subject':
                    if (selectedSubject) {
                        success = await deleteSubject(selectedSubject.id);
                    }
                    break;
                case 'Class':
                    if (selectedSubject && selectedClass) {
                        success = await deleteClass(selectedSubject.id, selectedClass.id);
                    }
                    break;
                case 'Room':
                    if (selectedRoom) {
                        success = await deleteRoom(selectedRoom.id);
                    }
                    break;
                case 'Lecture':
                    if (selectedLecture) {
                        success = await deleteLecture(selectedLecture.id);
                    }
                    break;
            }

            if (success) {
                showAlert('Success', `${selectedEntity} deleted successfully`);
                // Reset relevant state after successful deletion
                switch (selectedEntity) {
                    case 'Subject':
                        setSelectedSubject(null);
                        setSearchQuerySubjects('');
                        break;
                    case 'Class':
                        setSelectedClass(null);
                        break;
                    case 'Room':
                        setSelectedRoom(null);
                        setSearchQueryRooms('');
                        break;
                    case 'Lecture':
                        setSelectedLecture(null);
                        break;
                }
            } else {
                showAlert('Error', `Failed to delete ${selectedEntity}`);
            }
        } catch (err) {
            setError(err as ParsedError);
        } finally {
            setIsLoading(false);
        }
    };

    return {
        // Entity selection
        selectedEntity,
        handleEntitySelect,

        // Subject/Class search and selection
        subjects,
        subjectClasses,
        selectedSubject,
        selectedClass,
        searchQuerySubjects,
        setSearchQuerySubjects,
        handleSubjectSelect,
        handleClassSelect,

        // Room search and selection
        rooms,
        selectedRoom,
        searchQueryRooms,
        setSearchQueryRooms,
        handleRoomSelect,

        // Lecture search and selection
        lectures,
        selectedLecture,
        lectureFilter,
        setLectureFilter,
        handleLectureSelect,

        // Common
        error,
        isLoading,
        handleDelete
    };
};
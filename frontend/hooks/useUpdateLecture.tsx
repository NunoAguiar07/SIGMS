import {useEffect, useState} from "react";
import {Lecture} from "../types/calendar/Lecture";
import {ParsedError} from "../types/errors/ParseErrorTypes";
import {fetchLectures} from "../services/authorized/FetchLectures";
import {updateLectureSchedule} from "../services/authorized/RequestUpdateLecture";
import {fetchRooms} from "../services/authorized/FetchRooms";
import {RoomInterface} from "../types/RoomInterface";
import {useDebounce} from "use-debounce";
import {SubjectInterface} from "../types/SubjectInterface";
import {SchoolClassInterface} from "../types/SchoolClassInterface";
import {fetchLecturesByClass} from "../services/authorized/FetchLecturesByClass";
import {fetchSubjects} from "../services/authorized/FetchSubjects";
import {fetchLecturesByRoom} from "../services/authorized/FetchLecturesByRoom";
import {fetchSubjectClasses} from "../services/authorized/FetchSubjectClasses";
import {useAlert} from "./notifications/useAlert";

export const useUpdateLecture = () => {
    const [lectures, setLectures] = useState<Lecture[]>([]);
    const [selectedLecture, setSelectedLecture] = useState<Lecture | null>(null);
    const [error, setError] = useState<ParsedError | null>(null);
    const [isSaving, setIsSaving] = useState<boolean>(false);
    const [effectiveFrom, setEffectiveFrom] = useState<Date | null>(null);
    const [effectiveUntil, setEffectiveUntil] = useState<Date | null>(null);
    const [rooms, setRooms] = useState<RoomInterface[]>([]);
    const [searchQueryRoom, setSearchQueryRoom] = useState('');
    const [debouncedSearchQueryRoom] = useDebounce(searchQueryRoom, 500);
    const [selectedRoom, setSelectedRoom] = useState<RoomInterface | null>(null);
    const [effectiveFromText, setEffectiveFromText] = useState('');
    const [effectiveUntilText, setEffectiveUntilText] = useState('');
    const [skipSearch, setSkipSearch] = useState<boolean>(false);

    const [page, setPage] = useState(0);
    const [lectureFilter, setLectureFilter] = useState<'all' | 'class' | 'room'>('all');
    const [subjects, setSubjects] = useState<SubjectInterface[]>([]);
    const [selectedSubject, setSelectedSubject] = useState<SubjectInterface | null>(null);
    const [classes, setClasses] = useState<SchoolClassInterface[]>([]);
    const [selectedClass, setSelectedClass] = useState<SchoolClassInterface | null>(null);

    const [searchQuerySubjects, setSearchQuerySubjects] = useState<string>("");
    const [debouncedSearchQuerySubjects] = useDebounce(searchQuerySubjects, 500);

    const limit = 5;

    const showAlert = useAlert();

    const handleNext = () => setPage((prev) => prev + 1);
    const handlePrevious = () => setPage((prev) => Math.max(prev - 1, 0));


    useEffect(() => {
        if (debouncedSearchQueryRoom.trim().length > 0 && !skipSearch) {
            fetchRooms(debouncedSearchQueryRoom, 'CLASS')
                .then((data) => {
                    setRooms(data);
                }).catch(err => {
                setError(err as ParsedError);
                setRooms([]);
                setSelectedRoom(null);
            })
        } else {
            setRooms([]);
            setSelectedRoom(null);
            setError(null);
            setSkipSearch(false);
        }
    }, [debouncedSearchQueryRoom]);

    useEffect(() => {
        if (debouncedSearchQuerySubjects.trim().length > 0 && !skipSearch) {
            fetchSubjects(debouncedSearchQuerySubjects)
                .then((data) => {
                    setSubjects(data);
                }).catch(err => {
                setError(err as ParsedError);
                setSubjects([]);
            });
        } else {
            setSkipSearch(false)
            setSubjects([]);
        }
    }, [debouncedSearchQuerySubjects]);

    useEffect(() => {
        const parsed = new Date(effectiveFromText);
        if (!isNaN(parsed.getTime())) {
            setEffectiveFrom(parsed);
        }
    }, [effectiveFromText]);

    useEffect(() => {
        const parsed = new Date(effectiveUntilText);
        if (!isNaN(parsed.getTime())) {
            setEffectiveUntil(parsed);
        }
    }, [effectiveUntilText]);

    const handleRoomSelect = async (room: RoomInterface) => {
        setSelectedRoom(room);
        setSkipSearch(true)
        setSearchQueryRoom(room.name);
        setRooms([])
        if (selectedLecture) {
            setSelectedLecture({
                ...selectedLecture,
                room,
            });
        } else {
            try {
                setLectureFilter('room');
                const offset = page * limit;
                const data = await fetchLecturesByRoom(room.id, limit, offset);
                setLectures(data);
            } catch (err) {
                setError(err as ParsedError);
            }
        }
    };

    const handleOnSubjectSelect = async (subject: SubjectInterface) => {
        setSelectedSubject(subject);
        setSkipSearch(true);
        setSearchQuerySubjects(subject.name);
        setClasses([]);
        setSelectedClass(null);
        const classes = await fetchSubjectClasses(subject.id)
        setClasses(classes);
    }

    const handleClassSelect = async (schoolClass : SchoolClassInterface) => {
        if (!schoolClass || !selectedSubject) {
            showAlert("Missing Class", "Please select a class to filter lectures.");
            return;
        }
        setSelectedClass(schoolClass);
        try {
            setLectureFilter('class');
            const offset = page * limit;
            const data = await fetchLecturesByClass(selectedSubject.id, schoolClass.id, limit, offset);
            setLectures(data);
        } catch (err) {
            setError(err as ParsedError);
        }
    }

    const onGetAllLectures = async () => {
        try {
            setLectureFilter('all')
            const offset = page * limit;
            const data = await fetchLectures(limit, offset);
            setLectures(data);
        } catch (err) {
            setError(err as ParsedError);
        }
    };

    const onGetLecturesByClass = async () => {
        try {
            if (!selectedClass || !selectedSubject) {
                showAlert("Missing Class", "Please select a class to filter lectures.");
                return;
            }
            const offset = page * limit;
            const data = await fetchLecturesByClass(selectedSubject.id, selectedClass.id, limit, offset);
            setLectures(data);
        } catch (err) {
            setError(err as ParsedError);
        }
    }

    const onGetLecturesByRoom = async () => {
        try {
            if (!selectedRoom) {
                showAlert("Missing Room", "Please select a room to filter lectures.");
                return;
            }
            const offset = page * limit;
            const data = await fetchLecturesByRoom(selectedRoom.id, limit, offset);
            setLectures(data);
        } catch (err) {
            setError(err as ParsedError);
        }
    };

    const onLectureSelect = (lecture: Lecture) => {
        setSearchQueryRoom('')
        setSelectedLecture({ ...lecture });
        setSelectedRoom(lecture.room);
    };

    useEffect(() => {
        setError(null);
        setSelectedLecture(null);
        if (lectureFilter === 'all') {
            onGetAllLectures();
        } else if (lectureFilter === 'class') {
            onGetLecturesByClass();
        } else if (lectureFilter === 'room') {
            onGetLecturesByRoom();
        }
    }, [page]);

    const onScheduleChange = (changes: Partial<Lecture>) => {
        if (!selectedLecture) return;
        setSelectedLecture({
            ...selectedLecture,
            ...changes,
        });
    };

    const onSaveSchedule = async () => {
        let effectiveFromDate: string | null = null;
        let effectiveUntilDate: string | null = null;
        if (!selectedLecture) {
            showAlert("Missing Schedule", "Please select a class to filter lectures.");
            return;
        }

        setIsSaving(true);
        try {

            if (effectiveFrom != null && effectiveUntil != null && ( effectiveFrom >= effectiveUntil)) {
                showAlert("Invalid Dates", "Effective From date must be before Effective Until date.");
                return;
            }
            if (effectiveFrom && effectiveUntil) {
                 effectiveFromDate = effectiveFrom.toISOString()
                 effectiveUntilDate = effectiveUntil.toISOString()
            }
            const updated = await updateLectureSchedule(
                selectedLecture,
                effectiveFromDate,
                effectiveUntilDate
            );

            if(updated) {
                const updatedLectures = lectures.map((lec) =>
                    lec.id === selectedLecture.id ? selectedLecture : lec
                ).sort((a, b) =>
                    a.schoolClass.subject.name.localeCompare(b.schoolClass.subject.name)
                );
                setLectures(updatedLectures);
                showAlert("Success", "Lecture schedule updated successfully.");
            }
        } catch (err) {
            setError(err as ParsedError);
        } finally {
            setIsSaving(false);
        }
    };


    return {
        lectures,
        selectedLecture,
        onLectureSelect,
        onScheduleChange,
        onSaveSchedule,
        error,
        isSaving,
        rooms,
        setSearchQueryRoom,
        searchQueryRoom,
        handleRoomSelect,
        effectiveFrom,
        setEffectiveFrom,
        effectiveUntil,
        setEffectiveUntil,
        effectiveFromText,
        setEffectiveFromText,
        effectiveUntilText,
        setEffectiveUntilText,
        page,
        lectureFilter,
        setLectureFilter,
        searchQuerySubjects,
        setSearchQuerySubjects,
        debouncedSearchQuerySubjects,
        subjects,
        classes,
        onGetAllLectures,
        handleOnSubjectSelect,
        handleClassSelect,
        handleNext,
        handlePrevious,
    };
};
import {useEffect, useState} from "react";
import {Lecture} from "../types/calendar/Lecture";
import {ParsedError} from "../types/errors/ParseErrorTypes";
import {fetchLectures} from "../services/authorized/FetchLectures";
import {Alert} from "react-native";
import {updateLectureSchedule} from "../services/authorized/RequestUpdateLecture";
import {fetchRooms} from "../services/authorized/FetchRooms";
import {RoomInterface} from "../types/RoomInterface";
import {useDebounce} from "use-debounce";

export const useUpdateLecture = () => {
    const [lectures, setLectures] = useState<Lecture[]>([]);
    const [selectedLecture, setSelectedLecture] = useState<Lecture | null>(null);
    const [error, setError] = useState<ParsedError | null>(null);
    const [isSaving, setIsSaving] = useState<boolean>(false);
    const [effectiveFrom, setEffectiveFrom] = useState<Date | null>(null);
    const [effectiveUntil, setEffectiveUntil] = useState<Date | null>(null);
    const [rooms, setRooms] = useState<RoomInterface[]>([]);
    const [searchQuery, setSearchQuery] = useState('');
    const [debouncedSearchQuery] = useDebounce(searchQuery, 500);
    const [selectedRoom, setSelectedRoom] = useState<RoomInterface | null>(null);
    const [effectiveFromText, setEffectiveFromText] = useState('');
    const [effectiveUntilText, setEffectiveUntilText] = useState('');


    useEffect(() => {
        if (debouncedSearchQuery.trim().length > 0) {
            fetchRooms(debouncedSearchQuery)
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
        }
    }, [debouncedSearchQuery]);

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

    const handleRoomSelect = (room: RoomInterface) => {
        setSelectedRoom(room);
        if (selectedLecture) {
            setSelectedLecture({
                ...selectedLecture,
                room,
            });
        }
    };

    useEffect(() => {
        const loadLectures = async () => {
            try {
                const data = await fetchLectures();
                setLectures(data);
            } catch (err) {
                setError(err as ParsedError);
            }
        };
        loadLectures();
    }, []);

    const onLectureSelect = (lecture: Lecture) => {
        setSelectedLecture({ ...lecture });
        setSelectedRoom(lecture.room);
    };
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
            Alert.alert("Missing Information", "Please select a lecture and both dates.");
            return;
        }

        setIsSaving(true);
        try {

            if (effectiveFrom != null && effectiveUntil != null && ( effectiveFrom >= effectiveUntil)) {
                Alert.alert("Invalid Dates", "Effective From date must be before Effective Until date.");
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

            if (!updated) {
                throw new Error("Update failed.");
            }

            const updatedLectures = lectures.map((lec) =>
                lec.id === selectedLecture.id ? selectedLecture : lec
            );
            setLectures(updatedLectures);

            Alert.alert("Success", "Lecture schedule updated successfully.");
        } catch (err) {
            setError(err as ParsedError);
            Alert.alert("Error", "Failed to save the lecture schedule.");
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
        setSearchQuery,
        searchQuery,
        selectedRoom,
        handleRoomSelect,
        effectiveFrom,
        setEffectiveFrom,
        effectiveUntil,
        setEffectiveUntil,
        effectiveFromText,
        setEffectiveFromText,
        effectiveUntilText,
        setEffectiveUntilText,

    };
};
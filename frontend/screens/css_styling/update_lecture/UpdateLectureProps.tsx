import {Lecture} from "../../../types/calendar/Lecture";
import {RoomInterface} from "../../../types/RoomInterface";


export interface UpdateLectureProps {
    lectures: Lecture[];
    selectedLecture: Lecture | null;
    onLectureSelect: (lecture: Lecture) => void;
    onScheduleChange: (changes: Partial<Lecture>) => void;
    onSaveSchedule: () => void;
    isSaving: boolean;
    rooms: RoomInterface[];
    setSearchQuery: (query: string) => void;
    searchQuery: string;
    selectedRoom: RoomInterface | null;
    handleRoomSelect: (room: RoomInterface) => void;
    setEffectiveFromText: (text: string) => void;
    setEffectiveUntilText: (text: string) => void;
    effectiveFromText: string;
    effectiveUntilText: string;
}
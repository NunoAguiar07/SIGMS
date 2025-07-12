import {Lecture} from "../../../types/calendar/Lecture";
import {RoomInterface} from "../../../types/RoomInterface";
import {SubjectInterface} from "../../../types/SubjectInterface";
import {SchoolClassInterface} from "../../../types/SchoolClassInterface";


export interface UpdateLectureProps {
    lectures: Lecture[];
    selectedLecture: Lecture | null;
    onLectureSelect: (lecture: Lecture) => void;
    onScheduleChange: (changes: Partial<Lecture>) => void;
    onSaveSchedule: () => void;
    isSaving: boolean;
    rooms: RoomInterface[];
    searchQueryRoom: string;
    setSearchQueryRoom: (query: string) => void;
    handleRoomSelect: (room: RoomInterface) => void;
    setEffectiveFromText: (text: string) => void;
    setEffectiveUntilText: (text: string) => void;
    effectiveFromText: string;
    effectiveUntilText: string;
    searchQuerySubjects: string;
    setSearchQuerySubjects: (query: string) => void;
    subjects: SubjectInterface[];
    onGetAllLectures: () => void;
    handleOnSubjectSelect: (subject: SubjectInterface) => void;
    handleClassSelect: (schoolClass: SchoolClassInterface) => void;
    classes: SchoolClassInterface[];
    lectureFilter: 'all' | 'class' | 'room';
    setLectureFilter: (filter: 'all' | 'class' | 'room') => void;
    handleNext: () => void;
    handlePrevious: () => void;
    page: number;
}
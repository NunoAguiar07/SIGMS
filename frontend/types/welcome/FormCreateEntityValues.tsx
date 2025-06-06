
export type RoomType = 'STUDY' | 'CLASS' | 'OFFICE';

export type LectureType = 'PRACTICAL' | 'THEORETICAL' | 'THEORETICAL_PRACTICAL';

export interface FormCreateEntityValues {
    name?: string;
    subjectId?: number | null;
    capacity?: number | null;
    roomType?: RoomType;
    lectureType?: LectureType;
    schoolClassId?: number | null;
    roomId?: number;
    weekDay?: number;
    startTime?: string;
    endTime?: string;
}
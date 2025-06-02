
export type RoomType = 'STUDY' | 'CLASS' | 'OFFICE';

export type LectureType = 'PRACTICAL' | 'THEORETICAL' | 'THEORETICAL_PRACTICAL';

export interface FormCreateEntityValues {
    name?: string;
    subjectId?: number;
    capacity?: number;
    roomType?: RoomType;
    lectureType?: LectureType;
    schoolClassId?: number;
    roomId?: number;
    weekDay?: number;
    startTime?: string;
    endTime?: string;
}
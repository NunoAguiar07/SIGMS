

export interface Lecture {
    id: number;
    weekDay: number;
    startTime: string;
    endTime: string;
    type: string;
    room: {
        id: number;
        name: string;
    };
    schoolClass: {
        id: number;
        name: string;
        subject: {
            name: string;
        };
    };
    effectiveUntil?: string;
    effectiveFrom?: string;
}
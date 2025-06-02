

export interface Lecture {
    id: number;
    weekDay: string;
    startTime: string;
    endTime: string;
    type: string;
    room: {
        name: string;
    };
    schoolClass: {
        name: string;
        subject: {
            name: string;
        };
    };
}
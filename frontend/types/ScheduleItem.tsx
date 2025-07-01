import {Lecture} from "./calendar/Lecture";

export type ScheduleItem = {
    lecture: Lecture;
    teacher: { user: { username: string; } }[];
};